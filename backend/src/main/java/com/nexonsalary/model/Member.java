package com.nexonsalary.model;

import jakarta.persistence.*;

@Entity
@Table(name = "members")
public class Member extends BaseEntity {

    @Column(name = "national_id", nullable = false, unique = true, length = 20)
    private String nationalId;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    public Member() {
    }

    public Member(String nationalId, String fullName) {
        this.nationalId = nationalId;
        this.fullName = fullName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}