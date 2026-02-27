package com.hms.ui;

import com.hms.dao.*;
import com.hms.model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * HMSMainFrame — Hospital Management System
 * Clean, formal, medical-grade UI
 * Color theme: white + steel blue + soft grays (like a real hospital software)
 */
public class HMSMainFrame extends JFrame {

    // ─────────────────────────────────────────────────────
    //  COLORS  — soft medical blues and whites
    // ─────────────────────────────────────────────────────
    private static final Color BLUE_DARK   = new Color(30,  80, 140);  // header, buttons
    private static final Color BLUE_MID    = new Color(50, 115, 185);  // tab headers
    private static final Color BLUE_LIGHT  = new Color(220, 235, 250); // row highlight
    private static final Color WHITE       = new Color(255, 255, 255);
    private static final Color GRAY_BG     = new Color(225, 232, 245);  // page background
    private static final Color GRAY_PANEL  = new Color(255, 255, 255);  // card background
    private static final Color GRAY_BORDER = new Color(200, 210, 225); // borders
    private static final Color TEXT_DARK   = new Color(  5,  15,  40);  // main text
    private static final Color TEXT_GRAY   = new Color( 30,  50,  90);  // labels
    private static final Color GREEN       = new Color( 34, 139,  34); // success / paid
    private static final Color RED         = new Color(180,  30,  30); // danger / delete
    private static final Color ORANGE      = new Color(200, 110,   0); // pending / warning

    // ─────────────────────────────────────────────────────
    //  FONTS
    // ─────────────────────────────────────────────────────
    private static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD,  16);
    private static final Font FONT_LABEL  = new Font("SansSerif", Font.BOLD,  12);
    private static final Font FONT_FIELD  = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("SansSerif", Font.BOLD,  12);
    private static final Font FONT_TABLE  = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_TH     = new Font("SansSerif", Font.BOLD,  12);

    // ─────────────────────────────────────────────────────
    //  DAOs  — one object per table
    // ─────────────────────────────────────────────────────
    private final PatientDAO     patientDAO     = new PatientDAO();
    private final DoctorDAO      doctorDAO      = new DoctorDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillDAO        billDAO        = new BillDAO();

    // ─────────────────────────────────────────────────────
    //  PATIENT TAB — form fields + table
    // ─────────────────────────────────────────────────────
    private JTextField tfPName, tfPGender, tfPDob, tfPPhone,
                       tfPEmail, tfPAddr, tfPBG, tfPEC, tfPSearch;
    private DefaultTableModel patModel;
    private JTable            tblPat;

    // ─────────────────────────────────────────────────────
    //  DOCTOR TAB
    // ─────────────────────────────────────────────────────
    private JTextField tfDName, tfDGender, tfDDob, tfDPhone,
                       tfDEmail, tfDAddr, tfDSpec, tfDQual,
                       tfDFee,  tfDDays,  tfDSearch;
    private DefaultTableModel docModel;
    private JTable            tblDoc;

    // ─────────────────────────────────────────────────────
    //  APPOINTMENT TAB
    // ─────────────────────────────────────────────────────
    private JComboBox<String>  cbPatient, cbDoctor;
    private JTextField         tfADate, tfATime, tfANotes;
    private DefaultTableModel  apptModel;
    private JTable             tblAppt;

    // ─────────────────────────────────────────────────────
    //  BILLING TAB
    // ─────────────────────────────────────────────────────
    private JTextField     tfBApptId, tfBConsult, tfBMedicine, tfBTest;
    private JLabel         lblTotal;
    private DefaultTableModel billModel;
    private JTable            tblBill;

    // ─────────────────────────────────────────────────────
    //  MEDICINE TAB
    // ─────────────────────────────────────────────────────
    private JTextField tfMName, tfMDose, tfMQty, tfMPrice;
    private DefaultTableModel medModel;
    private JTable            tblMed;
    private int               medRowId = 1; // simple counter for medicine rows

    // ═════════════════════════════════════════════════════
    //  CONSTRUCTOR — builds the entire window
    // ═════════════════════════════════════════════════════
    public HMSMainFrame() {
        setTitle("Hospital Management System");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);           // center on screen
        getContentPane().setBackground(GRAY_BG);

        // 3 sections: top header, middle tabs, bottom status bar
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildTabs(),      BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        loadAll(); // load all DB data when app starts
        setVisible(true);
    }

    // ═════════════════════════════════════════════════════
    //  TOP HEADER
    // ═════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE_DARK);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Hospital name on the left
        JLabel title = new JLabel("🏥  HOSPITAL MANAGEMENT SYSTEM");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(WHITE);

        // Small info text on the right
        JLabel info = new JLabel("Core Java  ·  JDBC  ·  Oracle DB");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(new Color(180, 205, 235));

        header.add(title, BorderLayout.WEST);
        header.add(info,  BorderLayout.EAST);
        return header;
    }

    // ═════════════════════════════════════════════════════
    //  BOTTOM STATUS BAR
    // ═════════════════════════════════════════════════════
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(215, 225, 240));
        bar.setPreferredSize(new Dimension(0, 24));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel lbl = new JLabel("HMS v1.0  —  Hospital Management System  |  Ready");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(new Color(20, 40, 80));
        bar.add(lbl, BorderLayout.WEST);
        return bar;
    }

    // ═════════════════════════════════════════════════════
    //  TABBED PANE — 5 tabs
    // ═════════════════════════════════════════════════════
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(GRAY_BG);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabs.addTab("  Patients     ", buildPatientPanel());
        tabs.addTab("  Doctors      ", buildDoctorPanel());
        tabs.addTab("  Appointments ", buildAppointmentPanel());
        tabs.addTab("  Billing      ", buildBillingPanel());
        tabs.addTab("  Medicine     ", buildMedicinePanel());
        return tabs;
    }

    // ═════════════════════════════════════════════════════
    //  ── PATIENT PANEL ──
    // ═════════════════════════════════════════════════════
    private JPanel buildPatientPanel() {

        // Root panel: left form | right table
        JPanel root = rootPanel();

        // ── LEFT: form ──────────────────────────────────
        JPanel left = leftPanel("Patient Details");

        // Create all text fields
        tfPName   = field(); tfPGender = field();
        tfPDob    = field(); tfPPhone  = field();
        tfPEmail  = field(); tfPAddr   = field();
        tfPBG     = field(); tfPEC     = field();
        tfPSearch = field();

        // Add label + field pairs into the form
        JPanel form = formGrid(9); // 9 rows
        addField(form, "Full Name",          tfPName);
        addField(form, "Gender",             tfPGender);
        addField(form, "Date of Birth",      tfPDob);
        addField(form, "Phone",              tfPPhone);
        addField(form, "Email",              tfPEmail);
        addField(form, "Address",            tfPAddr);
        addField(form, "Blood Group",        tfPBG);
        addField(form, "Emergency Contact",  tfPEC);
        addField(form, "Search by Name",     tfPSearch);

        left.add(form, BorderLayout.CENTER);

        // ── Buttons ─────────────────────────────────────
        // 6 buttons in a 2x3 grid
        JPanel btns = new JPanel(new GridLayout(2, 3, 8, 8));
        btns.setOpaque(false);
        btns.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));

        JButton btnAdd    = btn("Add Patient",  BLUE_DARK);
        JButton btnUpdate = btn("Update",       new Color(0, 110, 60));
        JButton btnDelete = btn("Delete",       RED);
        JButton btnSearch = btn("Search",       new Color(80, 60, 150));
        JButton btnClear  = btn("Clear Form",   TEXT_GRAY);
        JButton btnRefresh= btn("Refresh",      new Color(0, 110, 140));

        // Wire each button to its method
        btnAdd.addActionListener(    e -> addPatient());
        btnUpdate.addActionListener( e -> updatePatient());
        btnDelete.addActionListener( e -> deletePatient());
        btnSearch.addActionListener( e -> searchPatient());
        btnClear.addActionListener(  e -> clearPatient());
        btnRefresh.addActionListener(e -> loadPatients());

        btns.add(btnAdd); btns.add(btnUpdate); btns.add(btnDelete);
        btns.add(btnSearch); btns.add(btnClear); btns.add(btnRefresh);
        left.add(btns, BorderLayout.SOUTH);

        // ── RIGHT: table ─────────────────────────────────
        JPanel right = rightPanel("Patient Records");

        patModel = model(new String[]{
            "ID", "Name", "Gender", "DOB", "Phone", "Email", "Blood Grp", "Emergency"
        });
        tblPat = styledTable(patModel);

        // When user clicks a row → fill the form fields automatically
        tblPat.getSelectionModel().addListSelectionListener(e -> fillPatientForm());

        right.add(scroll(tblPat), BorderLayout.CENTER);

        root.add(left,  BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        return root;
    }

    // ── Patient button methods ────────────────────────────

    private void addPatient() {
        if (tfPName.getText().trim().isEmpty()) {
            warn("Patient name is required!"); return;
        }
        try {
            // Step 1: Read values from text fields into a Patient object
            Patient p = new Patient();
            p.setFullName(tfPName.getText().trim());
            p.setGender(tfPGender.getText().trim());
            p.setDob(tfPDob.getText().trim());
            p.setPhone(tfPPhone.getText().trim());
            p.setEmail(tfPEmail.getText().trim());
            p.setAddress(tfPAddr.getText().trim());
            p.setBloodGroup(tfPBG.getText().trim());
            p.setEmergencyContact(tfPEC.getText().trim());

            // Step 2: Call DAO to insert into DB
            if (patientDAO.addPatient(p)) {
                success("Patient added successfully!");
                loadPatients();   // refresh table
                clearPatient();   // clear form
            }
        } catch (Exception ex) {
            error("Error: " + ex.getMessage());
        }
    }

    private void updatePatient() {
        int row = tblPat.getSelectedRow();
        if (row < 0) { warn("Please select a patient from the table first!"); return; }
        try {
            // Get the patient from DB using the ID in column 0
            Patient p = patientDAO.getPatientById((int) patModel.getValueAt(row, 0));
            p.setFullName(tfPName.getText().trim());
            p.setGender(tfPGender.getText().trim());
            p.setDob(tfPDob.getText().trim());
            p.setPhone(tfPPhone.getText().trim());
            p.setEmail(tfPEmail.getText().trim());
            p.setAddress(tfPAddr.getText().trim());
            p.setBloodGroup(tfPBG.getText().trim());
            p.setEmergencyContact(tfPEC.getText().trim());
            if (patientDAO.updatePatient(p)) {
                success("Patient updated!"); loadPatients();
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void deletePatient() {
        int row = tblPat.getSelectedRow();
        if (row < 0) { warn("Select a patient to delete!"); return; }
        if (confirm("Are you sure you want to delete this patient?")) {
            try {
                patientDAO.deletePatient((int) patModel.getValueAt(row, 0));
                success("Patient deleted!"); loadPatients(); clearPatient();
            } catch (Exception ex) { error(ex.getMessage()); }
        }
    }

    private void searchPatient() {
        try {
            List<Patient> list = patientDAO.searchByName(tfPSearch.getText().trim());
            patModel.setRowCount(0); // clear table
            for (Patient p : list)
                patModel.addRow(patientRow(p));
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void loadPatients() {
        try {
            List<Patient> list = patientDAO.getAllPatients();
            patModel.setRowCount(0);
            for (Patient p : list)
                patModel.addRow(patientRow(p));
        } catch (Exception ex) { error("Could not load patients: " + ex.getMessage()); }
    }

    // Convert a Patient object into a table row
    private Object[] patientRow(Patient p) {
        return new Object[]{
            p.getPatientId(), p.getFullName(), p.getGender(),
            p.getDob(), p.getPhone(), p.getEmail(),
            p.getBloodGroup(), p.getEmergencyContact()
        };
    }

    // When a row is clicked in the table → fill form fields
    private void fillPatientForm() {
        int row = tblPat.getSelectedRow();
        if (row < 0) return;
        tfPName.setText(str(patModel.getValueAt(row, 1)));
        tfPGender.setText(str(patModel.getValueAt(row, 2)));
        tfPDob.setText(str(patModel.getValueAt(row, 3)));
        tfPPhone.setText(str(patModel.getValueAt(row, 4)));
        tfPEmail.setText(str(patModel.getValueAt(row, 5)));
        tfPBG.setText(str(patModel.getValueAt(row, 6)));
        tfPEC.setText(str(patModel.getValueAt(row, 7)));
    }

    private void clearPatient() {
        for (JTextField f : new JTextField[]{
            tfPName,tfPGender,tfPDob,tfPPhone,tfPEmail,tfPAddr,tfPBG,tfPEC,tfPSearch})
            f.setText("");
        tblPat.clearSelection();
    }

    // ═════════════════════════════════════════════════════
    //  ── DOCTOR PANEL ──
    // ═════════════════════════════════════════════════════
    private JPanel buildDoctorPanel() {
        JPanel root = rootPanel();

        JPanel left = leftPanel("Doctor Details");

        tfDName  = field(); tfDGender = field(); tfDDob   = field();
        tfDPhone = field(); tfDEmail  = field(); tfDAddr  = field();
        tfDSpec  = field(); tfDQual   = field();
        tfDFee   = field(); tfDDays   = field();
        tfDSearch = field();

        JPanel form = formGrid(11);
        addField(form, "Full Name",         tfDName);
        addField(form, "Gender",            tfDGender);
        addField(form, "Date of Birth",     tfDDob);
        addField(form, "Phone",             tfDPhone);
        addField(form, "Email",             tfDEmail);
        addField(form, "Address",           tfDAddr);
        addField(form, "Specialization",    tfDSpec);
        addField(form, "Qualification",     tfDQual);
        addField(form, "Consult Fee (Rs.)", tfDFee);
        addField(form, "Available Days",    tfDDays);
        addField(form, "Search by Name",    tfDSearch);
        left.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(2, 3, 8, 8));
        btns.setOpaque(false);
        btns.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));

        JButton btnAdd    = btn("Add Doctor",  BLUE_DARK);
        JButton btnUpdate = btn("Update",      new Color(0, 110, 60));
        JButton btnDelete = btn("Delete",      RED);
        JButton btnSearch = btn("Search",      new Color(80, 60, 150));
        JButton btnClear  = btn("Clear Form",  TEXT_GRAY);
        JButton btnRef    = btn("Refresh",     new Color(0, 110, 140));

        btnAdd.addActionListener(    e -> addDoctor());
        btnUpdate.addActionListener( e -> updateDoctor());
        btnDelete.addActionListener( e -> deleteDoctor());
        btnSearch.addActionListener( e -> searchDoctor());
        btnClear.addActionListener(  e -> clearDoctor());
        btnRef.addActionListener(    e -> loadDoctors());

        btns.add(btnAdd); btns.add(btnUpdate); btns.add(btnDelete);
        btns.add(btnSearch); btns.add(btnClear); btns.add(btnRef);
        left.add(btns, BorderLayout.SOUTH);

        JPanel right = rightPanel("Doctor Records");
        docModel = model(new String[]{
            "ID","Name","Gender","Specialization","Qualification","Fee (Rs.)","Available Days"
        });
        tblDoc = styledTable(docModel);
        tblDoc.getSelectionModel().addListSelectionListener(e -> fillDoctorForm());
        right.add(scroll(tblDoc), BorderLayout.CENTER);

        root.add(left,  BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        return root;
    }

    private void addDoctor() {
        if (tfDName.getText().trim().isEmpty()) { warn("Doctor name required!"); return; }
        try {
            Doctor d = new Doctor();
            d.setFullName(tfDName.getText().trim());
            d.setGender(tfDGender.getText().trim());
            d.setDob(tfDDob.getText().trim());
            d.setPhone(tfDPhone.getText().trim());
            d.setEmail(tfDEmail.getText().trim());
            d.setAddress(tfDAddr.getText().trim());
            d.setSpecialization(tfDSpec.getText().trim());
            d.setQualification(tfDQual.getText().trim());
            d.setConsultFee(toDouble(tfDFee.getText()));
            d.setAvailableDays(tfDDays.getText().trim());
            if (doctorDAO.addDoctor(d)) {
                success("Doctor added!"); loadDoctors(); clearDoctor();
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void updateDoctor() {
        int row = tblDoc.getSelectedRow();
        if (row < 0) { warn("Select a doctor from the table!"); return; }
        try {
            Doctor d = doctorDAO.getDoctorById((int) docModel.getValueAt(row, 0));
            d.setFullName(tfDName.getText().trim());
            d.setGender(tfDGender.getText().trim());
            d.setPhone(tfDPhone.getText().trim());
            d.setEmail(tfDEmail.getText().trim());
            d.setAddress(tfDAddr.getText().trim());
            d.setSpecialization(tfDSpec.getText().trim());
            d.setQualification(tfDQual.getText().trim());
            d.setConsultFee(toDouble(tfDFee.getText()));
            d.setAvailableDays(tfDDays.getText().trim());
            if (doctorDAO.updateDoctor(d)) { success("Doctor updated!"); loadDoctors(); }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void deleteDoctor() {
        int row = tblDoc.getSelectedRow();
        if (row < 0) { warn("Select a doctor to delete!"); return; }
        if (confirm("Delete this doctor?")) {
            try {
                doctorDAO.deleteDoctor((int) docModel.getValueAt(row, 0));
                success("Doctor deleted!"); loadDoctors(); clearDoctor();
            } catch (Exception ex) { error(ex.getMessage()); }
        }
    }

    private void searchDoctor() {
        try {
            List<Doctor> list = doctorDAO.searchByName(tfDSearch.getText().trim());
            docModel.setRowCount(0);
            for (Doctor d : list) docModel.addRow(new Object[]{
                d.getDoctor_Id(), d.getFullName(), d.getGender(),
                d.getSpecialization(), d.getQualification(),
                d.getConsultFee(), d.getAvailableDays()
            });
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void loadDoctors() {
        try {
            List<Doctor> list = doctorDAO.getAllDoctors();
            docModel.setRowCount(0);
            for (Doctor d : list) docModel.addRow(new Object[]{
                d.getDoctor_Id(), d.getFullName(), d.getGender(),
                d.getSpecialization(), d.getQualification(),
                d.getConsultFee(), d.getAvailableDays()
            });
        } catch (Exception ex) { error("Could not load doctors: " + ex.getMessage()); }
    }

    private void fillDoctorForm() {
        int row = tblDoc.getSelectedRow(); if (row < 0) return;
        tfDName.setText(str(docModel.getValueAt(row, 1)));
        tfDGender.setText(str(docModel.getValueAt(row, 2)));
        tfDSpec.setText(str(docModel.getValueAt(row, 3)));
        tfDQual.setText(str(docModel.getValueAt(row, 4)));
        tfDFee.setText(str(docModel.getValueAt(row, 5)));
        tfDDays.setText(str(docModel.getValueAt(row, 6)));
    }

    private void clearDoctor() {
        for (JTextField f : new JTextField[]{
            tfDName,tfDGender,tfDDob,tfDPhone,tfDEmail,tfDAddr,
            tfDSpec,tfDQual,tfDFee,tfDDays,tfDSearch})
            f.setText("");
        tblDoc.clearSelection();
    }

    // ═════════════════════════════════════════════════════
    //  ── APPOINTMENT PANEL ──
    // ═════════════════════════════════════════════════════
    private JPanel buildAppointmentPanel() {
        JPanel root = rootPanel();
        JPanel left = leftPanel("Book Appointment");

        // Dropdowns to select patient and doctor
        cbPatient = new JComboBox<>();
        cbDoctor  = new JComboBox<>();
        styleCombo(cbPatient);
        styleCombo(cbDoctor);

        tfADate  = field(); tfADate.setText("YYYY-MM-DD");
        tfATime  = field(); tfATime.setText("HH:MM");
        tfANotes = field();

        // Use 5-row form grid for appointment
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 12));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        addField(form, "Patient",  cbPatient);
        addField(form, "Doctor",   cbDoctor);
        addField(form, "Date",     tfADate);
        addField(form, "Time",     tfATime);
        addField(form, "Notes",    tfANotes);
        left.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(2, 3, 8, 8));
        btns.setOpaque(false);
        btns.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));

        JButton btnBook    = btn("Book",            BLUE_DARK);
        JButton btnComplete= btn("Mark Completed",  new Color(0, 110, 60));
        JButton btnCancel  = btn("Cancel Appt",     RED);
        JButton btnDelete  = btn("Delete",          new Color(100, 40, 0));
        JButton btnToday   = btn("Today's Appts",   new Color(0, 110, 140));
        JButton btnRefresh = btn("Refresh",         TEXT_GRAY);

        btnBook.addActionListener(    e -> bookAppointment());
        btnComplete.addActionListener(e -> setApptStatus("COMPLETED"));
        btnCancel.addActionListener(  e -> setApptStatus("CANCELLED"));
        btnDelete.addActionListener(  e -> deleteAppointment());
        btnToday.addActionListener(   e -> loadTodayAppts());
        btnRefresh.addActionListener( e -> loadAppointments());

        btns.add(btnBook); btns.add(btnComplete); btns.add(btnCancel);
        btns.add(btnDelete); btns.add(btnToday); btns.add(btnRefresh);
        left.add(btns, BorderLayout.SOUTH);

        JPanel right = rightPanel("Appointment Records");
        apptModel = model(new String[]{
            "ID","Patient","Doctor","Date","Time","Status","Notes"
        });
        tblAppt = styledTable(apptModel);
        // Color-code the Status column (col 5)
        tblAppt.getColumnModel().getColumn(5).setCellRenderer(statusRenderer());
        right.add(scroll(tblAppt), BorderLayout.CENTER);

        // Small legend below the table
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        legend.setOpaque(false);
        legend.add(legendLabel("● Scheduled",  BLUE_MID));
        legend.add(legendLabel("● Completed",  GREEN));
        legend.add(legendLabel("● Cancelled",  RED));
        right.add(legend, BorderLayout.SOUTH);

        root.add(left,  BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        return root;
    }

    private void bookAppointment() {
        if (cbPatient.getSelectedItem() == null || cbDoctor.getSelectedItem() == null) {
            warn("Please select a patient and doctor!"); return;
        }
        try {
            // Extract ID from the combo item "1 - John Smith"
            int pid = Integer.parseInt(cbPatient.getSelectedItem().toString().split(" - ")[0].trim());
            int did = Integer.parseInt(cbDoctor.getSelectedItem().toString().split(" - ")[0].trim());

            Appointment a = new Appointment();
            a.setPatientId(pid);
            a.setDoctor_Id(did);
            a.setApptDate(tfADate.getText().trim());
            a.setApptTime(tfATime.getText().trim());
            a.setNotes(tfANotes.getText().trim());

            if (appointmentDAO.bookAppointment(a)) {
                success("Appointment booked!"); loadAppointments();
                tfADate.setText("YYYY-MM-DD"); tfATime.setText("HH:MM"); tfANotes.setText("");
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void setApptStatus(String status) {
        int row = tblAppt.getSelectedRow();
        if (row < 0) { warn("Select an appointment from the table!"); return; }
        try {
            appointmentDAO.updateStatus((int) apptModel.getValueAt(row, 0), status);
            success("Status updated to: " + status); loadAppointments();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void deleteAppointment() {
        int row = tblAppt.getSelectedRow();
        if (row < 0) { warn("Select an appointment!"); return; }
        if (confirm("Delete this appointment?")) {
            try {
                appointmentDAO.deleteAppointment((int) apptModel.getValueAt(row, 0));
                success("Appointment deleted!"); loadAppointments();
            } catch (Exception ex) { error(ex.getMessage()); }
        }
    }

    private void loadAppointments() {
        try {
            List<Appointment> list = appointmentDAO.getAllAppointments();
            apptModel.setRowCount(0);
            for (Appointment a : list) apptModel.addRow(new Object[]{
                a.getApptId(), a.getPatientName(), a.getDoctorName(),
                a.getApptDate(), a.getApptTime(), a.getStatus(), a.getNotes()
            });
        } catch (Exception ex) { error("Could not load appointments: " + ex.getMessage()); }
    }

    private void loadTodayAppts() {
        try {
            List<Appointment> list = appointmentDAO.getTodaysAppointments();
            apptModel.setRowCount(0);
            for (Appointment a : list) apptModel.addRow(new Object[]{
                a.getApptId(), a.getPatientName(), a.getDoctorName(),
                a.getApptDate(), a.getApptTime(), a.getStatus(), a.getNotes()
            });
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void loadCombos() {
        try {
            cbPatient.removeAllItems();
            for (Patient p : patientDAO.getAllPatients())
                cbPatient.addItem(p.getPatientId() + " - " + p.getFullName());

            cbDoctor.removeAllItems();
            for (Doctor d : doctorDAO.getAllDoctors())
                cbDoctor.addItem(d.getDoctor_Id() + " - " + d.getFullName()
                    + " (" + d.getSpecialization() + ")");
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    // ═════════════════════════════════════════════════════
    //  ── BILLING PANEL ──
    // ═════════════════════════════════════════════════════
    private JPanel buildBillingPanel() {
        JPanel root = rootPanel();
        JPanel left = leftPanel("Generate Bill");

        tfBApptId   = field();
        tfBConsult  = field();
        tfBMedicine = field();
        tfBTest     = field();

        // Auto-calculate total when user types in charge fields
        KeyAdapter calc = new KeyAdapter() {
            public void keyReleased(KeyEvent e) { updateTotal(); }
        };
        tfBConsult.addKeyListener(calc);
        tfBMedicine.addKeyListener(calc);
        tfBTest.addKeyListener(calc);

        // Total amount label (read-only display)
        lblTotal = new JLabel("Rs. 0.00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTotal.setForeground(BLUE_DARK);

        JPanel form = formGrid(5);
        addField(form, "Appointment ID",      tfBApptId);
        addField(form, "Consultation (Rs.)",  tfBConsult);
        addField(form, "Medicine (Rs.)",      tfBMedicine);
        addField(form, "Tests (Rs.)",         tfBTest);
        addField(form, "TOTAL AMOUNT",        lblTotal);
        left.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(2, 3, 8, 8));
        btns.setOpaque(false);
        btns.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));

        JButton btnGen     = btn("Generate Bill",  BLUE_DARK);
        JButton btnPaid    = btn("Mark as Paid",   new Color(0, 110, 60));
        JButton btnDelete  = btn("Delete Bill",    RED);
        JButton btnPending = btn("Pending Bills",  ORANGE);
        JButton btnRevenue = btn("Total Revenue",  new Color(0, 100, 80));
        JButton btnRefresh = btn("Refresh",        TEXT_GRAY);

        btnGen.addActionListener(    e -> generateBill());
        btnPaid.addActionListener(   e -> markPaid());
        btnDelete.addActionListener( e -> deleteBill());
        btnPending.addActionListener(e -> loadPendingBills());
        btnRevenue.addActionListener(e -> showRevenue());
        btnRefresh.addActionListener(e -> loadBills());

        btns.add(btnGen); btns.add(btnPaid); btns.add(btnDelete);
        btns.add(btnPending); btns.add(btnRevenue); btns.add(btnRefresh);
        left.add(btns, BorderLayout.SOUTH);

        JPanel right = rightPanel("Billing Records");
        billModel = model(new String[]{
            "Bill ID","Patient","Appt ID","Consult","Medicine","Test","Total","Date","Status"
        });
        tblBill = styledTable(billModel);
        // Color-code the payment Status column (col 8)
        tblBill.getColumnModel().getColumn(8).setCellRenderer(paidRenderer());
        right.add(scroll(tblBill), BorderLayout.CENTER);

        root.add(left,  BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        return root;
    }

    // Called on every key press in charge fields
    private void updateTotal() {
        double total = toDouble(tfBConsult.getText())
                     + toDouble(tfBMedicine.getText())
                     + toDouble(tfBTest.getText());
        lblTotal.setText(String.format("Rs. %.2f", total));
    }

    private void generateBill() {
        if (tfBApptId.getText().trim().isEmpty()) {
            warn("Please enter an Appointment ID!"); return;
        }
        try {
            int apptId = Integer.parseInt(tfBApptId.getText().trim());
            Appointment a = appointmentDAO.getAppointmentById(apptId);
            if (a == null) { error("Appointment ID not found!"); return; }
            if (billDAO.getBillByApptId(apptId) != null) {
                warn("A bill already exists for this appointment!"); return;
            }
            Bill b = new Bill();
            b.setApptId(apptId);
            b.setPatientId(a.getPatientId());
            b.setConsultCharnge(toDouble(tfBConsult.getText()));
            b.setMedicineCharge(toDouble(tfBMedicine.getText()));
            b.setTestCharge(toDouble(tfBTest.getText()));

            if (billDAO.generateBill(b)) {
                double total = b.getConsultCharnge() + b.getMedicineCharge() + b.getTestCharge();
                success("Bill generated!\nTotal Amount: Rs. " + String.format("%.2f", total));
                loadBills();
                tfBApptId.setText(""); tfBConsult.setText("");
                tfBMedicine.setText(""); tfBTest.setText("");
                lblTotal.setText("Rs. 0.00");
            }
        } catch (NumberFormatException e) {
            error("Please enter valid numbers in the charge fields!");
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void markPaid() {
        int row = tblBill.getSelectedRow();
        if (row < 0) { warn("Select a bill from the table!"); return; }
        if (confirm("Mark this bill as PAID?")) {
            try {
                billDAO.markAsPaid((int) billModel.getValueAt(row, 0));
                success("Payment marked successfully!"); loadBills();
            } catch (Exception ex) { error(ex.getMessage()); }
        }
    }

    private void deleteBill() {
        int row = tblBill.getSelectedRow();
        if (row < 0) { warn("Select a bill to delete!"); return; }
        if (confirm("Delete this bill?")) {
            try {
                billDAO.deleteBill((int) billModel.getValueAt(row, 0));
                success("Bill deleted!"); loadBills();
            } catch (Exception ex) { error(ex.getMessage()); }
        }
    }

    private void loadBills() {
        try {
            List<Bill> list = billDAO.getAllBills();
            billModel.setRowCount(0);
            for (Bill b : list) billModel.addRow(new Object[]{
                b.getBillId(), b.getPatientName(), b.getApptId(),
                "Rs." + b.getConsultCharnge(),
                "Rs." + b.getMedicineCharge(),
                "Rs." + b.getTestCharge(),
                String.format("Rs.%.2f", b.getTotalAmount()),
                b.getBillDate(), b.getPaymentStatus()
            });
        } catch (Exception ex) { error("Could not load bills: " + ex.getMessage()); }
    }

    private void loadPendingBills() {
        try {
            List<Bill> list = billDAO.getPendingBills();
            billModel.setRowCount(0);
            for (Bill b : list) billModel.addRow(new Object[]{
                b.getBillId(), b.getPatientName(), b.getApptId(),
                "Rs." + b.getConsultCharnge(),
                "Rs." + b.getMedicineCharge(),
                "Rs." + b.getTestCharge(),
                String.format("Rs.%.2f", b.getTotalAmount()),
                b.getBillDate(), b.getPaymentStatus()
            });
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void showRevenue() {
        try {
            double rev = billDAO.getTotalRevenue();
            JOptionPane.showMessageDialog(this,
                "Total Revenue from Paid Bills:\n\nRs. " + String.format("%,.2f", rev),
                "Revenue Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    // ═════════════════════════════════════════════════════
    //  ── MEDICINE PANEL ──
    //  (in-memory for now — add MedicineDAO to save to DB)
    // ═════════════════════════════════════════════════════
    private JPanel buildMedicinePanel() {
        JPanel root = rootPanel();
        JPanel left = leftPanel("Medicine Inventory");

        tfMName  = field();
        tfMDose  = field();
        tfMQty   = field();
        tfMPrice = field();

        JPanel form = formGrid(4);
        addField(form, "Medicine Name",      tfMName);
        addField(form, "Dosage / Form",      tfMDose);
        addField(form, "Quantity in Stock",  tfMQty);
        addField(form, "Price per Unit (Rs.)", tfMPrice);
        left.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(1, 3, 8, 8));
        btns.setOpaque(false);
        btns.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));

        JButton btnAdd    = btn("Add",    BLUE_DARK);
        JButton btnUpdate = btn("Update", new Color(0, 110, 60));
        JButton btnRemove = btn("Remove", RED);

        btnAdd.addActionListener(   e -> addMedicine());
        btnUpdate.addActionListener(e -> updateMedicine());
        btnRemove.addActionListener(e -> removeMedicine());

        btns.add(btnAdd); btns.add(btnUpdate); btns.add(btnRemove);
        left.add(btns, BorderLayout.SOUTH);

        // Note box explaining how to connect to DB
        JPanel note = new JPanel(new BorderLayout());
        note.setBackground(new Color(255, 248, 220));
        note.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ORANGE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JLabel noteLbl = new JLabel(
            "<html><b>Note:</b> This panel currently stores data in memory only.<br>" +
            "To save to Oracle DB, create a MedicineDAO class<br>" +
            "and a medicines table (similar to patients/doctors).</html>");
        noteLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        noteLbl.setForeground(new Color(100, 60, 0));
        note.add(noteLbl);

        JPanel leftWrap = new JPanel(new BorderLayout(0, 8));
        leftWrap.setOpaque(false);
        leftWrap.add(left, BorderLayout.CENTER);
        leftWrap.add(note, BorderLayout.SOUTH);

        JPanel right = rightPanel("Medicine Stock");
        medModel = model(new String[]{"#","Medicine Name","Dosage","Qty in Stock","Price (Rs.)","Stock Status"});
        tblMed = styledTable(medModel);
        // Color-code stock status column (col 5)
        tblMed.getColumnModel().getColumn(5).setCellRenderer(stockRenderer());
        tblMed.getSelectionModel().addListSelectionListener(e -> fillMedForm());
        right.add(scroll(tblMed), BorderLayout.CENTER);

        root.add(leftWrap, BorderLayout.WEST);
        root.add(right,    BorderLayout.CENTER);
        return root;
    }

    private void addMedicine() {
        if (tfMName.getText().trim().isEmpty()) { warn("Medicine name required!"); return; }
        String status = stockStatus(tfMQty.getText());
        medModel.addRow(new Object[]{
            medRowId++,
            tfMName.getText().trim(),
            tfMDose.getText().trim(),
            tfMQty.getText().trim(),
            "Rs." + tfMPrice.getText().trim(),
            status
        });
        success("Medicine added to inventory!");
        clearMed();
    }

    private void updateMedicine() {
        int row = tblMed.getSelectedRow();
        if (row < 0) { warn("Select a medicine to update!"); return; }
        medModel.setValueAt(tfMName.getText().trim(),  row, 1);
        medModel.setValueAt(tfMDose.getText().trim(),  row, 2);
        medModel.setValueAt(tfMQty.getText().trim(),   row, 3);
        medModel.setValueAt("Rs." + tfMPrice.getText().trim(), row, 4);
        medModel.setValueAt(stockStatus(tfMQty.getText()), row, 5);
        success("Medicine updated!");
    }

    private void removeMedicine() {
        int row = tblMed.getSelectedRow();
        if (row < 0) { warn("Select a medicine to remove!"); return; }
        if (confirm("Remove this medicine from inventory?")) {
            medModel.removeRow(row);
            success("Medicine removed!");
        }
    }

    private void fillMedForm() {
        int row = tblMed.getSelectedRow(); if (row < 0) return;
        tfMName.setText(str(medModel.getValueAt(row, 1)));
        tfMDose.setText(str(medModel.getValueAt(row, 2)));
        tfMQty.setText(str(medModel.getValueAt(row, 3)));
        tfMPrice.setText(str(medModel.getValueAt(row, 4)).replace("Rs.", ""));
    }

    private void clearMed() {
        for (JTextField f : new JTextField[]{tfMName, tfMDose, tfMQty, tfMPrice})
            f.setText("");
        tblMed.clearSelection();
    }

    // Returns stock status based on quantity
    private String stockStatus(String qtyStr) {
        try {
            int qty = Integer.parseInt(qtyStr.trim());
            if (qty == 0)   return "OUT OF STOCK";
            if (qty < 10)   return "LOW STOCK";
            return "IN STOCK";
        } catch (Exception e) { return "UNKNOWN"; }
    }

    // ═════════════════════════════════════════════════════
    //  LOAD ALL DATA ON STARTUP
    // ═════════════════════════════════════════════════════
    private void loadAll() {
        loadPatients();
        loadDoctors();
        loadCombos();       // fill appointment dropdowns
        loadAppointments();
        loadBills();
    }

    // ═════════════════════════════════════════════════════
    //  REUSABLE LAYOUT HELPERS
    //  These methods build common UI pieces so code is
    //  not repeated across all 5 panels
    // ═════════════════════════════════════════════════════

    /** Full tab root panel: white bg, padding */
    private JPanel rootPanel() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(GRAY_BG);
        p.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        return p;
    }

    /** Left section: white card, fixed width 310, titled border */
    private JPanel leftPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(GRAY_PANEL);
        p.setPreferredSize(new Dimension(310, 0));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GRAY_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(8, 10, 10, 10),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13), BLUE_DARK)));
        return p;
    }

    /** Right section: white card, titled border, fills remaining space */
    private JPanel rightPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(GRAY_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GRAY_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(8, 10, 10, 10),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13), BLUE_DARK)));
        return p;
    }

    /** Form grid: rows rows, 2 columns (label | field) */
    private JPanel formGrid(int rows) {
        JPanel p = new JPanel(new GridLayout(rows, 2, 8, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        return p;
    }

    /** Add a label + any component as one form row */
    private void addField(JPanel grid, String labelText, JComponent comp) {
        JLabel lbl = new JLabel(labelText + ":");
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(new Color(20, 40, 80));
        grid.add(lbl);
        grid.add(comp);
    }

    /** Standard text field with light border */
    private JTextField field() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_FIELD);
        tf.setForeground(Color.BLACK);
        tf.setBackground(new Color(245, 250, 255));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GRAY_BORDER, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        // Blue border on focus — feels professional
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BLUE_MID, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(GRAY_BORDER, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            }
        });
        return tf;
    }

    /** Styled button with solid background color */
    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
       // b.setForeground(Color.BLACK);
//        
        b.setForeground(Color.BLACK);              // black text
      //  b.setForeground(Color.YELLOW);             // yellow text  
       // b.setForeground(new Color(255, 220, 100)); // golden text
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Slightly darker on hover
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    /** Style a JComboBox to match the form fields */
    private void styleCombo(JComboBox<String> cb) {
        cb.setFont(FONT_FIELD);
        cb.setBackground(WHITE);
        cb.setForeground(TEXT_DARK);
        cb.setBorder(new LineBorder(GRAY_BORDER, 1));
    }

    /** Dark-header table with light alternating rows */
    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m) {
            // Alternating row colors
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(235, 245, 255) : new Color(215, 232, 250));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(100, 160, 220));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };
        t.setFont(FONT_TABLE);
        t.setRowHeight(28);
        t.setGridColor(new Color(220, 228, 240));
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Style the column header
        JTableHeader header = t.getTableHeader();
        header.setBackground(BLUE_DARK);
        header.setForeground(GREEN);
        header.setFont(FONT_TH);
        header.setPreferredSize(new Dimension(0, 34));
        header.setReorderingAllowed(false);
        return t;
    }

    /** Wrap table in a scroll pane */
    private JScrollPane scroll(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(new LineBorder(GRAY_BORDER, 1));
        sp.getViewport().setBackground(WHITE);
        return sp;
    }

    /** Non-editable table model */
    private DefaultTableModel model(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    // ═════════════════════════════════════════════════════
    //  CELL RENDERERS — color-code table columns
    // ═════════════════════════════════════════════════════

    /** Color-code appointment status: Scheduled/Completed/Cancelled */
    private TableCellRenderer statusRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v == null ? "" : v.toString();
                if      (val.equals("COMPLETED")) setForeground(GREEN);
                else if (val.equals("CANCELLED")) setForeground(RED);
                else                              setForeground(BLUE_MID);
                setFont(getFont().deriveFont(Font.BOLD));
                if (!sel) setBackground(r % 2 == 0 ? new Color(235, 245, 255) : new Color(215, 232, 250));
                return this;
            }
        };
    }

    /** Color-code bill payment status: PAID / PENDING */
    private TableCellRenderer paidRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v == null ? "" : v.toString();
                setForeground(val.equals("PAID") ? GREEN : ORANGE);
                setFont(getFont().deriveFont(Font.BOLD));
                if (!sel) setBackground(r % 2 == 0 ? new Color(235, 245, 255) : new Color(215, 232, 250));
                return this;
            }
        };
    }

    /** Color-code medicine stock status */
    private TableCellRenderer stockRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v == null ? "" : v.toString();
                if      (val.equals("IN STOCK"))     setForeground(GREEN);
                else if (val.equals("OUT OF STOCK")) setForeground(RED);
                else                                 setForeground(ORANGE);
                setFont(getFont().deriveFont(Font.BOLD));
                if (!sel) setBackground(r % 2 == 0 ? new Color(235, 245, 255) : new Color(215, 232, 250));
                return this;
            }
        };
    }

    // ═════════════════════════════════════════════════════
    //  SMALL HELPERS
    // ═════════════════════════════════════════════════════

    /** Legend label for appointment status key */
    private JLabel legendLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(color);
        return l;
    }

    /** Safely convert Object → String (avoids null crash) */
    private String str(Object o) { return o == null ? "" : o.toString(); }

    /** Safely convert String → double (returns 0 if invalid) */
    private double toDouble(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }

    // ── Dialog shortcuts ─────────────────────────────────
    private void success(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ═════════════════════════════════════════════════════
    //  MAIN — entry point of the application
    // ═════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use system look and feel (Windows/Mac native style)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new HMSMainFrame();
        });
    }
}