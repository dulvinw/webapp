/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 26/01/2020, 00:00
 *
 */

package com.dulvinw.springboot.webapp.ui.model.response;

import java.util.List;

public class UserRest {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<AddressResponseModel> addresses;

    public List<AddressResponseModel> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressResponseModel> addresses) {
        this.addresses = addresses;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
