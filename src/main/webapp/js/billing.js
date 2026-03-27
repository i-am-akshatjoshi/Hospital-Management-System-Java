/* billing.js — Billing panel JS */

function updateTotal() {
    const c = parseFloat(document.getElementById('bConsult').value)  || 0;
    const m = parseFloat(document.getElementById('bMedicine').value) || 0;
    const t = parseFloat(document.getElementById('bTest').value)     || 0;
    document.getElementById('bTotal').textContent = 'Rs. ' + (c + m + t).toFixed(2);
}

async function loadBills() {
    const data = await apiGet('api/billing');
    if (!data) return;
    renderBills(data);
}

async function loadPendingBills() {
    const data = await apiGet('api/billing?filter=pending');
    if (!data) return;
    renderBills(data);
}

function renderBills(data) {
    const tbody = document.getElementById('billBody');
    tbody.innerHTML = '';
    data.forEach(b => {
        const sc = b.paymentStatus === 'PAID' ? 'status-paid' : 'status-pending';
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${b.billId}</td><td>${b.patientName||''}</td><td>${b.apptId}</td>
            <td>Rs.${b.consultCharge}</td><td>Rs.${b.medicineCharge}</td><td>Rs.${b.testCharge}</td>
            <td><b>Rs.${parseFloat(b.totalAmount).toFixed(2)}</b></td>
            <td>${b.billDate||''}</td><td class="${sc}">${b.paymentStatus}</td>`;
        tbody.appendChild(tr);
    });
    makeTableSelectable('billBody', (row) => {
        const cells = row.querySelectorAll('td');
        document.getElementById('bId').value = cells[0].textContent;
    });
}

async function generateBill() {
    const apptId = document.getElementById('bApptId').value.trim();
    if (!apptId) { warn('Please enter an Appointment ID!'); return; }

    const data = await apiPost('api/billing', {
        apptId:        parseInt(apptId),
        consultCharge: parseFloat(document.getElementById('bConsult').value)  || 0,
        medicineCharge:parseFloat(document.getElementById('bMedicine').value) || 0,
        testCharge:    parseFloat(document.getElementById('bTest').value)     || 0
    });
    if (data && data.success) {
        ok('Bill generated! Total: Rs. ' + data.total);
        loadBills();
        ['bApptId','bConsult','bMedicine','bTest'].forEach(id => document.getElementById(id).value = '');
        document.getElementById('bTotal').textContent = 'Rs. 0.00';
    } else if (data) err(data.message || 'Failed to generate bill.');
}

async function markPaid() {
    const id = document.getElementById('bId').value;
    if (!id) { warn('Select a bill from the table!'); return; }
    if (!confirm2('Mark this bill as PAID?')) return;
    const data = await apiPut('api/billing?id=' + id + '&action=paid', {});
    if (data && data.success) { ok('Marked as PAID!'); loadBills(); }
    else if (data) err(data.message || 'Update failed.');
}

async function deleteBill() {
    const id = document.getElementById('bId').value;
    if (!id) { warn('Select a bill to delete!'); return; }
    if (!confirm2('Delete this bill?')) return;
    const data = await apiDelete('api/billing?id=' + id);
    if (data && data.success) { ok('Bill deleted!'); loadBills(); }
    else if (data) err(data.message || 'Delete failed.');
}

async function showRevenue() {
    const data = await apiGet('api/billing?action=revenue');
    if (data) alert('Total Revenue from Paid Bills:\n\nRs. ' + parseFloat(data.revenue).toLocaleString('en-IN', {minimumFractionDigits:2}));
}
