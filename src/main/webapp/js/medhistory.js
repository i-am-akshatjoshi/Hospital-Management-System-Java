/* medhistory.js — Medical History panel JS */

async function addMedHistory() {
    const patId     = document.getElementById('hPatId').value.trim();
    const condition = document.getElementById('hCondition').value.trim();
    const date      = document.getElementById('hDate').value.trim();

    if (!patId || !condition) { warn('Patient ID and Condition are required!'); return; }
    if (date && !isValidDate(date)) { warn('Date must be DD-MM-YYYY format!'); return; }

    const data = await apiPost('api/medhistory', {
        patientId:      parseInt(patId),
        conditionName:  condition,
        diagnosedDate:  date,
        treatmentGiven: document.getElementById('hTreatment').value.trim(),
        isChronic:      document.getElementById('hChronic').value,
        notes:          document.getElementById('hNotes').value.trim()
    });
    if (data && data.success) {
        ok('Medical history record added!'); loadMedHistory(); clearMedHistory();
    } else if (data) err(data.message || 'Failed to add record.');
}

async function loadMedHistory() {
    const patId = document.getElementById('hPatId').value.trim();
    if (!patId) { warn('Enter Patient ID first!'); return; }
    const data = await apiGet('api/medhistory?patientId=' + patId);
    if (!data) return;
    const tbody = document.getElementById('histBody');
    tbody.innerHTML = '';
    if (data.length === 0) { warn('No medical history found for this Patient ID.'); return; }
    data.forEach(h => {
        const cls = h.isChronic === 'YES' ? 'chronic-yes' : 'chronic-no';
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${h.historyId}</td><td>${h.patientId}</td><td>${h.conditionName}</td>
            <td>${h.diagnosedDate||''}</td><td>${h.treatmentGiven||''}</td>
            <td class="${cls}">${h.isChronic}</td><td>${h.notes||''}</td>`;
        tbody.appendChild(tr);
    });
    makeTableSelectable('histBody', (row) => {
        document.getElementById('hId').value = row.querySelectorAll('td')[0].textContent;
    });
}

async function deleteMedHistory() {
    const id = document.getElementById('hId').value;
    if (!id) { warn('Select a record to delete!'); return; }
    if (!confirm2('Delete this medical history record?')) return;
    const data = await apiDelete('api/medhistory?id=' + id);
    if (data && data.success) { ok('Deleted!'); loadMedHistory(); }
    else if (data) err(data.message || 'Delete failed.');
}

function clearMedHistory() {
    ['hCondition','hDate','hTreatment','hNotes','hId'].forEach(id => document.getElementById(id).value = '');
    document.getElementById('hChronic').selectedIndex = 0;
    document.querySelectorAll('#histBody tr').forEach(r => r.classList.remove('selected'));
}
