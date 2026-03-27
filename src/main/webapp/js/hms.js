/* ═══════════════════════════════════════════════════════════
   hms.js — Shared utilities used by all panel JS files
═══════════════════════════════════════════════════════════ */

// ── Currently selected row ID per panel ──
let selectedPatId   = null;
let selectedDocId   = null;
let selectedApptId  = null;
let selectedBillId  = null;
let selectedHistId  = null;
let selectedAlId    = null;
let selectedInsId   = null;
let selectedVitalId = null;

// ── Medicine in-memory store ──
let medicines = [];
let medCounter = 1;

// ══════════════════════════════════════════════════════════
//  TAB SWITCHING
// ══════════════════════════════════════════════════════════
function showTab(name) {
    // Hide all panels
    document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));

    // Show selected panel
    document.getElementById('tab-' + name).classList.add('active');

    // Activate button
    const btns = document.querySelectorAll('.tab-btn');
    const tabMap = {
        'patients': 0, 'doctors': 1, 'appointments': 2, 'billing': 3,
        'medicine': 4, 'medhistory': 5, 'allergies': 6, 'insurance': 7, 'vitals': 8
    };
    btns[tabMap[name]].classList.add('active');

    // Load data when tab is opened
    if (name === 'appointments') {
        loadAppointmentCombos();
    }
}

// ══════════════════════════════════════════════════════════
//  TOAST NOTIFICATIONS
// ══════════════════════════════════════════════════════════
let toastTimer = null;

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = 'toast ' + type;
    if (toastTimer) clearTimeout(toastTimer);
    toastTimer = setTimeout(() => {
        toast.className = 'toast hidden';
    }, 3500);
}

function ok(msg)   { showToast(msg, 'success'); }
function err(msg)  { showToast(msg, 'error'); }
function warn(msg) { showToast(msg, 'warn'); }

function confirm2(msg) { return window.confirm(msg); }

// ══════════════════════════════════════════════════════════
//  VALIDATION HELPERS
// ══════════════════════════════════════════════════════════
function isValidDate(val) {
    return /^\d{2}-\d{2}-\d{4}$/.test(val);
}

function isValidPhone(val) {
    return /^\d{10}$/.test(val);
}

function validateDateField(input) {
    const val = input.value.trim();
    if (val && !isValidDate(val)) {
        input.classList.add('invalid');
        input.title = '⚠ Use DD-MM-YYYY format (e.g. 25-06-1990)';
        return false;
    }
    input.classList.remove('invalid');
    input.title = 'Format: DD-MM-YYYY';
    return true;
}

function validatePhoneField(input) {
    const val = input.value.trim();
    if (val && !isValidPhone(val)) {
        input.classList.add('invalid');
        input.title = '⚠ Must be exactly 10 digits';
        return false;
    }
    input.classList.remove('invalid');
    input.title = 'Enter 10-digit number';
    return true;
}

// Auto-add hyphens to date fields as user types
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.date-field').forEach(input => {
        input.addEventListener('input', () => {
            let v = input.value.replace(/\D/g, '');
            if (v.length > 2)  v = v.slice(0,2) + '-' + v.slice(2);
            if (v.length > 5)  v = v.slice(0,5) + '-' + v.slice(5);
            if (v.length > 10) v = v.slice(0,10);
            input.value = v;
        });
        input.addEventListener('blur', () => validateDateField(input));
    });

    document.querySelectorAll('.phone-field').forEach(input => {
        input.addEventListener('input', () => {
            input.value = input.value.replace(/\D/g, '').slice(0, 10);
        });
        input.addEventListener('blur', () => validatePhoneField(input));
    });

    // Load initial data
    loadPatients();
    loadDoctors();
    loadBills();
    loadAppointments();
});

// ══════════════════════════════════════════════════════════
//  FETCH HELPERS — wrap all API calls
// ══════════════════════════════════════════════════════════

// GET request
async function apiGet(url) {
    try {
        const res = await fetch(url);
        if (!res.ok) throw new Error('Server error: ' + res.status);
        return await res.json();
    } catch (e) {
        err('Request failed: ' + e.message);
        return null;
    }
}

// POST request
async function apiPost(url, data) {
    try {
        const res = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error('Server error: ' + res.status);
        return await res.json();
    } catch (e) {
        err('Request failed: ' + e.message);
        return null;
    }
}

// PUT request
async function apiPut(url, data) {
    try {
        const res = await fetch(url, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error('Server error: ' + res.status);
        return await res.json();
    } catch (e) {
        err('Request failed: ' + e.message);
        return null;
    }
}

// DELETE request
async function apiDelete(url) {
    try {
        const res = await fetch(url, { method: 'DELETE' });
        if (!res.ok) throw new Error('Server error: ' + res.status);
        return await res.json();
    } catch (e) {
        err('Request failed: ' + e.message);
        return null;
    }
}

// ══════════════════════════════════════════════════════════
//  TABLE ROW SELECTION — click row to select
// ══════════════════════════════════════════════════════════
function makeTableSelectable(tbodyId, onSelect) {
    const tbody = document.getElementById(tbodyId);
    if (!tbody) return;
    tbody.addEventListener('click', (e) => {
        const row = e.target.closest('tr');
        if (!row) return;
        // Deselect previous
        tbody.querySelectorAll('tr.selected').forEach(r => r.classList.remove('selected'));
        row.classList.add('selected');
        onSelect(row);
    });
}

// ══════════════════════════════════════════════════════════
//  GET SELECTED ROW from a tbody
// ══════════════════════════════════════════════════════════
function getSelectedRow(tbodyId) {
    const tbody = document.getElementById(tbodyId);
    if (!tbody) return null;
    return tbody.querySelector('tr.selected');
}

function getSelectedCell(tbodyId, colIndex) {
    const row = getSelectedRow(tbodyId);
    if (!row) return null;
    const cells = row.querySelectorAll('td');
    return cells[colIndex] ? cells[colIndex].textContent.trim() : null;
}
