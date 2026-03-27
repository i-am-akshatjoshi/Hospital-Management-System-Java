/* doctors.js — Doctor panel JS */

async function loadDoctors() {
    const data = await apiGet('api/doctors');
    if (!data) return;
    const tbody = document.getElementById('docBody');
    tbody.innerHTML = '';
    data.forEach(d => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${d.doctorId}</td><td>${d.fullName}</td><td>${d.gender}</td>
            <td>${d.specialization||''}</td><td>${d.qualification||''}</td>
            <td>Rs.${d.consultFee}</td><td>${d.availableDays||''}</td>`;
        tbody.appendChild(tr);
    });
    makeTableSelectable('docBody', (row) => {
        const cells = row.querySelectorAll('td');
        document.getElementById('dId').value    = cells[0].textContent;
        document.getElementById('dName').value  = cells[1].textContent;
        document.getElementById('dGender').value= cells[2].textContent;
        document.getElementById('dSpec').value  = cells[3].textContent;
        document.getElementById('dQual').value  = cells[4].textContent;
        document.getElementById('dFee').value   = cells[5].textContent.replace('Rs.','');
        document.getElementById('dDays').value  = cells[6].textContent;
    });
}

async function addDoctor() {
    const name  = document.getElementById('dName').value.trim();
    const phone = document.getElementById('dPhone').value.trim();
    const dob   = document.getElementById('dDob').value.trim();
    const fee   = parseFloat(document.getElementById('dFee').value);

    if (!name)  { warn('Doctor name is required!'); return; }
    if (!isValidPhone(phone)) { warn('Phone must be exactly 10 digits!'); return; }
    if (dob && !isValidDate(dob)) { warn('Date of Birth must be DD-MM-YYYY format!'); return; }
    if (!fee || fee <= 0) { warn('Consult Fee must be a positive number!'); return; }

    const data = await apiPost('api/doctors', {
        fullName:       name,
        gender:         document.getElementById('dGender').value,
        dob:            dob,
        phone:          phone,
        email:          document.getElementById('dEmail').value.trim(),
        address:        document.getElementById('dAddr').value.trim(),
        specialization: document.getElementById('dSpec').value.trim(),
        qualification:  document.getElementById('dQual').value.trim(),
        consultFee:     fee,
        availableDays:  document.getElementById('dDays').value.trim()
    });
    if (data && data.success) { ok('Doctor added successfully!'); loadDoctors(); clearDoctor(); }
    else if (data) err(data.message || 'Failed to add doctor.');
}

async function updateDoctor() {
    const id = document.getElementById('dId').value;
    if (!id) { warn('Select a doctor from the table first!'); return; }
    const phone = document.getElementById('dPhone').value.trim();
    const dob   = document.getElementById('dDob').value.trim();
    const fee   = parseFloat(document.getElementById('dFee').value);
    if (phone && !isValidPhone(phone)) { warn('Phone must be 10 digits!'); return; }
    if (dob && !isValidDate(dob)) { warn('Date must be DD-MM-YYYY!'); return; }
    if (!fee || fee <= 0) { warn('Consult Fee must be positive!'); return; }

    const data = await apiPut('api/doctors?id=' + id, {
        fullName:       document.getElementById('dName').value.trim(),
        gender:         document.getElementById('dGender').value,
        dob:            dob,
        phone:          phone,
        email:          document.getElementById('dEmail').value.trim(),
        address:        document.getElementById('dAddr').value.trim(),
        specialization: document.getElementById('dSpec').value.trim(),
        qualification:  document.getElementById('dQual').value.trim(),
        consultFee:     fee,
        availableDays:  document.getElementById('dDays').value.trim()
    });
    if (data && data.success) { ok('Doctor updated!'); loadDoctors(); }
    else if (data) err(data.message || 'Update failed.');
}

async function deleteDoctor() {
    const id = document.getElementById('dId').value;
    if (!id) { warn('Select a doctor to delete!'); return; }
    if (!confirm2('Delete this doctor?')) return;
    const data = await apiDelete('api/doctors?id=' + id);
    if (data && data.success) { ok('Doctor deleted!'); loadDoctors(); clearDoctor(); }
    else if (data) err(data.message || 'Delete failed.');
}

async function searchDoctor() {
    const q = document.getElementById('dSearch').value.trim();
    const data = await apiGet('api/doctors?search=' + encodeURIComponent(q));
    if (!data) return;
    const tbody = document.getElementById('docBody');
    tbody.innerHTML = '';
    data.forEach(d => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${d.doctorId}</td><td>${d.fullName}</td><td>${d.gender}</td>
            <td>${d.specialization||''}</td><td>${d.qualification||''}</td>
            <td>Rs.${d.consultFee}</td><td>${d.availableDays||''}</td>`;
        tbody.appendChild(tr);
    });
}

function clearDoctor() {
    ['dId','dName','dDob','dPhone','dEmail','dAddr','dSpec','dQual','dFee','dDays','dSearch']
        .forEach(id => document.getElementById(id).value = '');
    document.getElementById('dGender').selectedIndex = 0;
    document.querySelectorAll('#docBody tr').forEach(r => r.classList.remove('selected'));
}
