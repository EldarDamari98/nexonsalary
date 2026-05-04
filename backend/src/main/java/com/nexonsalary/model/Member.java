package com.nexonsalary.model;

import jakarta.persistence.*;

/**
 * Represents a real person (client) in the system.
 *
 * A Member is identified by their Israeli national ID number and full name.
 * One member can own multiple MemberAccounts (e.g. a pension account AND
 * a life insurance account). The Member record itself just holds who the person is —
 * the financial details live in MemberAccount and MonthlyMemberBalance.
 *
 * Extends BaseEntity to inherit the id and createdAt fields.
 */
@Entity
@Table(name = "members")
public class Member extends BaseEntity {

    /** The Israeli national ID number — unique per person across the entire system. */
    @Column(name = "national_id", nullable = false, unique = true, length = 20)
    private String nationalId;

    /** The person's full name as it appears in the Excel import files. */
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    /** Required by Hibernate — do not use directly. */
    public Member() {
    }

    /**
     * Creates a new member with the given national ID and name.
     *
     * @param nationalId the Israeli national ID number (must be unique)
     * @param fullName   the person's full name
     */
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
