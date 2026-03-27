/* allergies.js — Allergy panel JS */

async function addAllergy() {
    const patId = document.getElementById('alPatId').value.trim();
    const name  = document.getElementById('alName').value.trim();
    if (!patId || !name) { warn('Patient ID and Allergy Name are required!'); return; }

    const data = await apiPost('api/allergies', {
        patientId:   parseInt(patId),
        allergyType: document.getElementById('alType').value,
        allergyName: name,
        severity:    document.getElementById('alSeverity').value,
        reaction:    document.getElementById('alReaction').value.trim()
    });
    if (data && data.success) {
        ok('Allergy record added!'); loadAllergies(); clearAllergy();
    } else if (data) err(data.message || 'Failed to add allergy.');
}

async function loadAllergies() {
    const patId = document.getElementById('alPatId').value.trim();
    if (!patId) { warn('Enter Patient ID first!'); return; }
    const data = await apiGet('api/allergies?patientId=' + patId);
    if (!data) return;
    const tbody = document.getElementById('allergyBody');
    tbody.innerHTML = '';
    if (data.length === 0) { warn('No allergy records found for this Patient ID.'); return; }
    data.forEach(a => {
        const cls = a.severity === 'SEVERE'   ? 'sev-severe'
                  : a.severity === 'MODERATE' ? 'sev-moderate'
                  : 'sev-mild';
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${a.allergyId}</td><td>${a.patientId}</td><td>${a.allergyType}</td>
            <td>${a.allergyName}</td><td class="${cls}">${a.severity}</td><td>${a.reaction||''}</td>`;
        tbody.appendChild(tr);
    });
    makeTableSelectable('allergyBody', (row) => {
        document.getElementById('alId').value = row.querySelectorAll('td')[0].textContent;
    });
}

async function deleteAllergy() {
    const id = document.getElementById('alId').value;
    if (!id) { warn('Select a record to delete!'); return; }
    if (!confirm2('Delete this allergy record?')) return;
    const data = await apiDelete('api/allergies?id=' + id);
    if (data && data.success) { ok('Deleted!'); loadAllergies(); }
    else if (data) err(data.message || 'Delete failed.');
}

function clearAllergy() {
    ['alName','alReaction','alId'].forEach(id => document.getElementById(id).value = '');
    document.getElementById('alType').selectedIndex = 0;
    document.getElementById('alSeverity').selectedIndex = 0;
    document.querySelectorAll('#allergyBody tr').forEach(r => r.classList.remove('selected'));
}
