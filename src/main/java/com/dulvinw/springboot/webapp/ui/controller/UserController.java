/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 25/01/2020, 23:59
 *
 */

package com.dulvinw.springboot.webapp.ui.controller;

import com.dulvinw.springboot.webapp.exceptions.UserServiceException;
import com.dulvinw.springboot.webapp.service.UserService;
import com.dulvinw.springboot.webapp.shared.dto.UserDto;
import com.dulvinw.springboot.webapp.ui.model.request.UserDetailRequestModel;
import com.dulvinw.springboot.webapp.ui.model.response.ErrorMessages;
import com.dulvinw.springboot.webapp.ui.model.response.OperationStatusModel;
import com.dulvinw.springboot.webapp.ui.model.response.RequestOperationName;
import com.dulvinw.springboot.webapp.ui.model.response.RequestOperationStatus;
import com.dulvinw.springboot.webapp.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(path="/{id}")
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();
        UserDto user = userService.getUserByUserId(id);

        BeanUtils.copyProperties(user, returnValue);

        return returnValue;
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailRequestModel userDetail) throws UserServiceException {
        UserRest returnValue = new UserRest();

        if (userDetail.getFirstName().isEmpty()) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetail, userDto);

        UserDto createUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createUser, returnValue);

        return returnValue;
    }

    @PutMapping(path="/{id}")
    public UserRest updateUser(@RequestBody UserDetailRequestModel userDetails, @PathVariable String id) {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetails, userDto);
        userDto.setUserId(id);
        UserDto updateUserResponse = userService.updateUser(userDto);

        BeanUtils.copyProperties(updateUserResponse, returnValue);
        return returnValue;
    }

    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(id);
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @GetMapping
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);
        for (UserDto user: users) {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(user, userRest);
            returnValue.add(userRest);
        }
        return returnValue;
    }
}
