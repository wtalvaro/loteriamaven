package com.wta.loteriamaven.restful;

import java.util.ArrayList;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person {
    private Long id;
    private String firstname;
    private String lastname;
    private Date hiredate;    
    private Long addressId;
    
    public Person() {
        super();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setHiredate(Date hiredate) {
        this.hiredate = hiredate;
    }

    public Date getHiredate() {
        return hiredate;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getAddressId() {
        return addressId;
    }
}
