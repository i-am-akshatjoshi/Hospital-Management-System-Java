/* appointments.js — Appointment panel JS */

async function loadAppointmentCombos() {
    const [patients, doctors] = await Promise.all([
        apiGet('api/patients'),
        apiGet('api/doctors')
    ]);
    const pSel = document.getElementById('aPatient');
    const dSel = document.getElementById('aDoctor');
    if (patients) {
        pSel.innerHTML = '<option value="">-- Select Patient --</option>';
        patients.forEach(p => {
            pSel.innerHTML += `<option value="${p.patientId}">${p.patientId} - ${p.fullName}</option>`;
        });
    }
    if (doctors) {
        dSel.innerHTML = '<option value="">-- Select Doctor --</option>';
        doctors.forEach(d => {
            dSel.innerHTML += `<option value="${d.doctorId}">${d.doctorId} - ${d.fullName} (${d.specialization||''})</option>`;
        });
    }
}

async function loadAppointments() {
    const data = await apiGet('api/appointments');
    if (!data) return;
    renderAppointments(data);
}

async function loadTodayAppts() {
    const data = await apiGet('api/appointments?filter=today');
    if (!data) return;
    renderAppointments(data);
}

function renderAppointments(data) {
    const tbody = document.getElementById('apptBody');
    tbody.innerHTML = '';
    data.forEach(a => {
        const statusClass = a.status === 'COMPLETED' ? 'status-completed'
                          : a.status === 'CANCELLED'  ? 'status-cancelled'
                          : 'status-scheduled';
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${a.apptId}</td><td>${a.patientName||''}</td><td>${a.doctorName||''}</td>
            <td>${a.apptDate||''}</td><td>${a.apptTime||''}</td>
            <td class="${statusClass}">${a.status||''}</td><td>${a.notes||''}</td>`;
        tbody.appendChild(tr);
    });
    makeTableSelectable('apptBody', (row) => {
        const cells = row.querySelectorAll('td');
        document.getElementById('aId').value = cells[0].textContent;
    });
}

async function bookAppointment() {
    const pid   = document.getElementById('aPatient').value;
    const did   = document.getElementById('aDoctor').value;
    const date  = document.getElementById('aDate').value.trim();
    const time  = document.getElementById('aTime').value.trim();

    if (!pid || !did) { warn('Please select a patient and doctor!'); return; }
    if (!isValidDate(date)) { warn('Appointment date must be DD-MM-YYYY format!\nExample: 25-06-2025'); return; }

    const data = await apiPost('api/appointments', {
        patientId: parseInt(pid),
        doctorId:  parseInt(did),
        apptDate:  date,
        apptTime:  time,
        notes:     document.getElementById('aNotes').value.trim()
    });
    if (data && data.success) {
        ok('Appointment booked!');
        loadAppointments();
        document.getElementById('aDate').value = '';
        document.getElementById('aTime').value = '';
        document.getElementById('aNotes').value = '';
        loadAppointmentCombos();
    } else if (data) err(data.message || 'Booking failed.');
}

async function setApptStatus(status) {
    const id = document.getElementById('aId').value;
    if (!id) { warn('Select an appointment from the table first!'); return; }
    const data = await apiPut('api/appointments?id=' + id + '&action=status', { status });
    if (data && data.success) { ok('Status updated to: ' + status); loadAppointments(); }
    else if (data) err(data.message || 'Update failed.');
}

async function deleteAppointment() {
    const id = document.getElementById('aId').value;
    if (!id) { warn('Select an appointment!'); return; }
    if (!confirm2('Delete this appointment?')) return;
    const data = await apiDelete('api/appointments?id=' + id);
    if (data && data.success) { ok('Deleted!'); loadAppointments(); }
    else if (data) err(data.message || 'Delete failed.');
}
