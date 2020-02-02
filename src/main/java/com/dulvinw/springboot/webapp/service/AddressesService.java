/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 02/02/2020, 10:31
 *
 */

package com.dulvinw.springboot.webapp.service;

import com.dulvinw.springboot.webapp.shared.dto.AddressDto;

import java.util.List;

public interface AddressesService {

    List<AddressDto> getAddresses(String id);

    AddressDto getAddress(String userId, String addressId);
}
