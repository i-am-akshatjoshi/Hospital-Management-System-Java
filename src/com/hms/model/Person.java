package com.hms.model;

// ✅ Abstract base class for Doctor and Patient
// All common fields live here — Patient and Doctor inherit them
public abstract class Person {

    private int    personId;
    private String fullName, gender, dob, phone, email, address, personType;

    // ✅ No-arg constructor — required by Patient() and Doctor()
    public Person() {}

    // Parameterized constructor
    public Person(int personId, String fullName, String gender,
                  String dob, String phone, String email,
                  String address, String personType) {
        this.personId   = personId;
        this.fullName   = fullName;
        this.gender     = gender;
        this.dob        = dob;
        this.phone      = phone;
        this.email      = email;
        this.address    = address;
        this.personType = personType;
    }

    // Abstract method — every subclass must implement this
    public abstract String getRoleInfo();

    // ── Getters & Setters ────────────────────────────────
    public int    getPersonId()               { return personId; }
    public void   setPersonId(int personId)   { this.personId = personId; }

    public String getFullName()               { return fullName; }
    public void   setFullName(String fullName){ this.fullName = fullName; }

    public String getGender()                 { return gender; }
    public void   setGender(String gender)    { this.gender = gender; }

    public String getDob()                    { return dob; }
    public void   setDob(String dob)          { this.dob = dob; }

    public String getPhone()                  { return phone; }
    public void   setPhone(String phone)      { this.phone = phone; }

    public String getEmail()                  { return email; }
    public void   setEmail(String email)      { this.email = email; }

    public String getAddress()                { return address; }
    public void   setAddress(String address)  { this.address = address; }

    public String getPersonType()                   { return personType; }
    public void   setPersonType(String personType)  { this.personType = personType; }
}