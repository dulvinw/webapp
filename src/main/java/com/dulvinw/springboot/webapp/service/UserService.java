/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 25/01/2020, 23:58
 *
 */

package com.dulvinw.springboot.webapp.service;

import com.dulvinw.springboot.webapp.shared.dto.AddressDto;
import com.dulvinw.springboot.webapp.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto getUser(String email);
    UserDto getUserByUserId(String id);
    UserDto updateUser(UserDto userDto);
    void deleteUser(String id);
    List<UserDto> getUsers(int page, int limit);
    boolean verifyEmailToken(String token);
}
