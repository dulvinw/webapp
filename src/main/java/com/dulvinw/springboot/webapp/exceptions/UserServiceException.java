/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 27/01/2020, 18:26
 *
 */

package com.dulvinw.springboot.webapp.exceptions;

public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = 5219066035600211559L;

    public UserServiceException(String message) {
        super(message);
    }
}
