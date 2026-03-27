/* insurance.js — Insurance panel JS */

async function addInsurance() {
    const patId    = document.getElementById('inPatId').value.trim();
    const provider = document.getElementById('inProvider').value.trim();
    const policy   = document.getElementById('inPolicy').value.trim();
    const fromDate = document.getElementById('inFrom').value.trim();
    const toDate   = document.getElementById('inTo').value.trim();

    if (!patId || !provider || !policy) {
        warn('Patient ID, Provider, and Policy Number are required!'); return;
    }
    if (fromDate && !isValidDate(fromDate)) { warn('Valid From must be DD-MM-YYYY!'); return; }
    if (toDate   && !isValidDate(toDate))   { warn('Valid To must be DD-MM-YYYY!');   return; }

    const data = await apiPost('api/insurance', {
        patientId:      parseInt(patId),
        providerName:   provider,
        policyNumber:   policy,
        coverageAmount: parseFloat(document.getElementById('inCoverage').value) || 0,
        validFrom:      fromDate,
        validTo:        toDate,
        policyType:     document.getElementById('inType').value,
        status:         document.getElementById('inStatus').value
    });
    if (data && data.success) {
        ok('Insurance record added!'); loadInsurance(); clearInsurance();
    } else if (data) err(data.message || 'Failed to add insurance.');
}

async function loadInsurance() {
    const patId = document.getElementById('inPatId').value.trim();
    if (!patId) { warn('Enter Patient ID first!'); return; }
    const data = await apiGet('api/insurance?patientId=' + patId);
    if (!data) return;
    const tbody = document.getElementById('insBody');
    tbody.innerHTML = '';
    if (data.length === 0) { warn('No insurance records found for this Patient ID.'); return; }
    data.forEach(i => {
        const cls = i.status === 'ACTIVE'    ? 'status-active'
                  : i.status === 'SUSPENDED' ? 'status-suspended'
                  : 'status-expired';
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${i.insuranceId}</td><td>${i.patientId}</td><td>${i.providerName}</td>
            <td>${i.policyNumber}</td><td>Rs.${parseFloat(i.coverageAmount).toFixed(2)}</td>
            <td>${i.validFrom||''}</td><td>${i.validTo||''}</td><td>${i.policyType}</td>
            <td class="${cls}">${i.status}</td>`;
        tbody.appendChild(tr);
    });
    makeTableSelectable('insBody', (row) => {
        document.getElementById('inId').value = row.querySelectorAll('td')[0].textContent;
    });
}

async function markInsuranceExpired() {
    const id = document.getElementById('inId').value;
    if (!id) { warn('Select an insurance record first!'); return; }
    if (!confirm2('Mark this policy as EXPIRED?')) return;
    const data = await apiPut('api/insurance?id=' + id + '&action=expire', {});
    if (data && data.success) { ok('Marked as EXPIRED.'); loadInsurance(); }
    else if (data) err(data.message || 'Update failed.');
}

async function deleteInsurance() {
    const id = document.getElementById('inId').value;
    if (!id) { warn('Select a record to delete!'); return; }
    if (!confirm2('Delete this insurance record?')) return;
    const data = await apiDelete('api/insurance?id=' + id);
    if (data && data.success) { ok('Deleted!'); loadInsurance(); }
    else if (data) err(data.message || 'Delete failed.');
}

function clearInsurance() {
    ['inProvider','inPolicy','inCoverage','inFrom','inTo','inId']
        .forEach(id => document.getElementById(id).value = '');
    document.getElementById('inType').selectedIndex = 0;
    document.getElementById('inStatus').selectedIndex = 0;
    document.querySelectorAll('#insBody tr').forEach(r => r.classList.remove('selected'));
}
