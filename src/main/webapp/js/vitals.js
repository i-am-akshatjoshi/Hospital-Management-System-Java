/* vitals.js — Vital Signs panel JS */

function calcBMI() {
    const w = parseFloat(document.getElementById('vWeight').value);
    const h = parseFloat(document.getElementById('vHeight').value);
    const display = document.getElementById('bmiDisplay');
    if (w > 0 && h > 0) {
        const bmi = w / Math.pow(h / 100, 2);
        let cat, color;
        if      (bmi < 18.5) { cat = 'Underweight'; color = '#c86e00'; }
        else if (bmi < 25)   { cat = 'Normal';       color = '#228b22'; }
        else if (bmi < 30)   { cat = 'Overweight';   color = '#c86e00'; }
        else                 { cat = 'Obese';         color = '#b41e1e'; }
        display.textContent = `BMI: ${bmi.toFixed(1)}  (${cat})`;
        display.style.color = color;
    } else {
        display.textContent = 'BMI: —';
        display.style.color = '#1e508c';
    }
}

async function saveVitals() {
    const patId = document.getElementById('vPatId').value.trim();
    if (!patId) { warn('Patient ID is required!'); return; }

    const w = parseFloat(document.getElementById('vWeight').value) || 0;
    const h = parseFloat(document.getElementById('vHeight').value) || 0;
    const bmi = (w > 0 && h > 0) ? w / Math.pow(h / 100, 2) : 0;

    const data = await apiPost('api/vitals', {
        patientId:   parseInt(patId),
        apptId:      parseInt(document.getElementById('vApptId').value) || 0,
        bloodPressure: document.getElementById('vBP').value.trim(),
        pulseRate:   parseInt(document.getElementById('vPulse').value) || 0,
        temperature: parseFloat(document.getElementById('vTemp').value) || 0,
        oxygenLevel: parseFloat(document.getElementById('vOxygen').value) || 0,
        weightKg:    w,
        heightCm:    h,
        bmi:         parseFloat(bmi.toFixed(2)),
        notes:       document.getElementById('vNotes').value.trim()
    });
    if (data && data.success) { ok('Vital signs saved!'); loadVitals(); }
    else if (data) err(data.message || 'Failed to save vitals.');
}

async function loadVitals() {
    const patId = document.getElementById('vPatId').value.trim();
    if (!patId) { warn('Enter Patient ID first!'); return; }
    const data = await apiGet('api/vitals?patientId=' + patId);
    if (!data) return;
    const tbody = document.getElementById('vitalsBody');
    tbody.innerHTML = '';
    if (data.length === 0) { warn('No vital signs records found for this Patient ID.'); return; }
    data.forEach(v => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${v.vitalId}</td><td>${v.patientId}</td><td>${v.apptId||''}</td>
            <td>${v.recordedDate||''}</td><td>${v.bloodPressure||''}</td><td>${v.pulseRate}</td>
            <td>${v.temperature}</td><td>${v.oxygenLevel}</td><td>${v.weightKg}</td>
            <td>${v.heightCm}</td><td>${parseFloat(v.bmi).toFixed(1)}</td><td>${v.notes||''}</td>`;
        tbody.appendChild(tr);
    });
    makeTableSelectable('vitalsBody', (row) => {
        document.getElementById('vId').value = row.querySelectorAll('td')[0].textContent;
    });
}

async function deleteVitals() {
    const id = document.getElementById('vId').value;
    if (!id) { warn('Select a record to delete!'); return; }
    if (!confirm2('Delete this vital signs record?')) return;
    const data = await apiDelete('api/vitals?id=' + id);
    if (data && data.success) { ok('Deleted!'); loadVitals(); }
    else if (data) err(data.message || 'Delete failed.');
}
