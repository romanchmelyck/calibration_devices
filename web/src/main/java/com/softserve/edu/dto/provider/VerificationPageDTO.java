package com.softserve.edu.dto.provider;

import com.softserve.edu.entity.util.ReadStatus;
import com.softserve.edu.entity.util.Status;

import java.util.Date;

public class VerificationPageDTO {
    private String id;
    private Date initialDate;
    private String surname;
    private String street;
   private Status status;
    private ReadStatus readStatus;
    public VerificationPageDTO() {}

    public VerificationPageDTO(String id, Date initialDate, String surname, String street,
    		Status status, ReadStatus readStatus) {
        this.id = id;
        this.initialDate = initialDate;
        this.surname = surname;
        this.street = street;
        this.status = status;
        this.readStatus = readStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ReadStatus getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(ReadStatus readStatus) {
		this.readStatus = readStatus;
	}
	
    @Override
    public String toString() {
        return "VerificationPageDTO{" +
                "id='" + id + '\'' +
                ", initialDate=" + initialDate +
                ", surname='" + surname + '\'' +
                ", street='" + street + '\'' +
                ", status=" + status +
                '}';
    }
}


