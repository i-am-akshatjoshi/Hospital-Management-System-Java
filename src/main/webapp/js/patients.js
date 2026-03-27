/* patients.js — Patient panel JS */

async function loadPatients() {
    const data = await apiGet('api/patients');
    if (!data) return;
    const tbody = document.getElementById('patBody');
    tbody.innerHTML = '';
    data.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${p.patientId}</td><td>${p.fullName}</td><td>${p.gender}</td>
            <td>${p.dob||''}</td><td>${p.phone}</td><td>${p.email||''}</td>
            <td>${p.bloodGroup||''}</td><td>${p.emergencyContact||''}</td>`;
        tbody.appendChild(tr);
    });
    makeTableSelectable('patBody', (row) => {
        const cells = row.querySelectorAll('td');
        document.getElementById('pId').value      = cells[0].textContent;
        document.getElementById('pName').value    = cells[1].textContent;
        document.getElementById('pGender').value  = cells[2].textContent;
        document.getElementById('pDob').value     = cells[3].textContent;
        document.getElementById('pPhone').value   = cells[4].textContent;
        document.getElementById('pEmail').value   = cells[5].textContent;
        document.getElementById('pBlood').value   = cells[6].textContent;
        document.getElementById('pEC').value      = cells[7].textContent;
    });
}

async function addPatient() {
    const name  = document.getElementById('pName').value.trim();
    const phone = document.getElementById('pPhone').value.trim();
    const dob   = document.getElementById('pDob').value.trim();
    const ec    = document.getElementById('pEC').value.trim();

    if (!name)  { warn('Patient name is required!'); return; }
    if (!isValidPhone(phone)) { warn('Phone must be exactly 10 digits!'); return; }
    if (dob && !isValidDate(dob)) { warn('Date of Birth must be DD-MM-YYYY format!'); return; }
    if (ec && !isValidPhone(ec)) { warn('Emergency contact must be 10 digits!'); return; }

    const data = await apiPost('api/patients', {
        fullName: name,
        gender:   document.getElementById('pGender').value,
        dob:      dob,
        phone:    phone,
        email:    document.getElementById('pEmail').value.trim(),
        address:  document.getElementById('pAddr').value.trim(),
        bloodGroup: document.getElementById('pBlood').value,
        emergencyContact: ec
    });
    if (data && data.success) { ok('Patient added successfully!'); loadPatients(); clearPatient(); }
    else if (data) err(data.message || 'Failed to add patient.');
}

async function updatePatient() {
    const id = document.getElementById('pId').value;
    if (!id) { warn('Select a patient from the table first!'); return; }
    const phone = document.getElementById('pPhone').value.trim();
    const dob   = document.getElementById('pDob').value.trim();
    if (!isValidPhone(phone)) { warn('Phone must be 10 digits!'); return; }
    if (dob && !isValidDate(dob)) { warn('Date must be DD-MM-YYYY!'); return; }

    const data = await apiPut('api/patients?id=' + id, {
        fullName: document.getElementById('pName').value.trim(),
        gender:   document.getElementById('pGender').value,
        dob:      dob,
        phone:    phone,
        email:    document.getElementById('pEmail').value.trim(),
        address:  document.getElementById('pAddr').value.trim(),
        bloodGroup: document.getElementById('pBlood').value,
        emergencyContact: document.getElementById('pEC').value.trim()
    });
    if (data && data.success) { ok('Patient updated!'); loadPatients(); }
    else if (data) err(data.message || 'Update failed.');
}

async function deletePatient() {
    const id = document.getElementById('pId').value;
    if (!id) { warn('Select a patient to delete!'); return; }
    if (!confirm2('Delete this patient?')) return;
    const data = await apiDelete('api/patients?id=' + id);
    if (data && data.success) { ok('Patient deleted!'); loadPatients(); clearPatient(); }
    else if (data) err(data.message || 'Delete failed.');
}

async function searchPatient() {
    const q = document.getElementById('pSearch').value.trim();
    const data = await apiGet('api/patients?search=' + encodeURIComponent(q));
    if (!data) return;
    const tbody = document.getElementById('patBody');
    tbody.innerHTML = '';
    data.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${p.patientId}</td><td>${p.fullName}</td><td>${p.gender}</td>
            <td>${p.dob||''}</td><td>${p.phone}</td><td>${p.email||''}</td>
            <td>${p.bloodGroup||''}</td><td>${p.emergencyContact||''}</td>`;
        tbody.appendChild(tr);
    });
}

function clearPatient() {
    ['pId','pName','pDob','pPhone','pEmail','pAddr','pEC','pSearch'].forEach(id => {
        document.getElementById(id).value = '';
    });
    document.getElementById('pGender').selectedIndex = 0;
    document.getElementById('pBlood').selectedIndex = 0;
    document.querySelectorAll('#patBody tr').forEach(r => r.classList.remove('selected'));
}
