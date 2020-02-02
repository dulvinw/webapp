/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 02/02/2020, 10:32
 *
 */

package com.dulvinw.springboot.webapp.service.impl;

import com.dulvinw.springboot.webapp.io.entity.AddressEntity;
import com.dulvinw.springboot.webapp.io.entity.UserEntity;
import com.dulvinw.springboot.webapp.io.repository.AddressRepository;
import com.dulvinw.springboot.webapp.io.repository.UserRepository;
import com.dulvinw.springboot.webapp.service.AddressesService;
import com.dulvinw.springboot.webapp.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressesServiceImpl implements AddressesService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String id) {
        List<AddressDto> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        UserEntity user = userRepository.findByUserId(id);

        if (user == null) {
            return returnValue;
        }

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(user);
        for (AddressEntity address:
             addresses) {
            AddressDto addressDto = modelMapper.map(address, AddressDto.class);
            returnValue.add(addressDto);
        }

        return returnValue;
    }

    @Override
    public AddressDto getAddress(String userId, String addressId) {
        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null) {
            throw new UsernameNotFoundException(userId);
        }

        AddressEntity addressEntity = addressRepository.findByUserDetailsAndAddressId(userEntity, addressId);
        AddressDto returnValue = modelMapper.map(addressEntity, AddressDto.class);

        return returnValue;
    }
}

