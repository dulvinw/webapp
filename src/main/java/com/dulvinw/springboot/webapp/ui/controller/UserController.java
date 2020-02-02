/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 25/01/2020, 23:59
 *
 */

package com.dulvinw.springboot.webapp.ui.controller;

import com.dulvinw.springboot.webapp.exceptions.UserServiceException;
import com.dulvinw.springboot.webapp.service.AddressesService;
import com.dulvinw.springboot.webapp.service.UserService;
import com.dulvinw.springboot.webapp.shared.dto.AddressDto;
import com.dulvinw.springboot.webapp.shared.dto.UserDto;
import com.dulvinw.springboot.webapp.ui.model.request.UserDetailRequestModel;
import com.dulvinw.springboot.webapp.ui.model.response.AddressResponseModel;
import com.dulvinw.springboot.webapp.ui.model.response.ErrorMessages;
import com.dulvinw.springboot.webapp.ui.model.response.OperationStatusModel;
import com.dulvinw.springboot.webapp.ui.model.response.RequestOperationName;
import com.dulvinw.springboot.webapp.ui.model.response.RequestOperationStatus;
import com.dulvinw.springboot.webapp.ui.model.response.UserRest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AddressesService addressesService;

    @GetMapping(path="/{id}")
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();
        UserDto user = userService.getUserByUserId(id);

        BeanUtils.copyProperties(user, returnValue);

        return returnValue;
    }

    @GetMapping(path="/{id}/addresses")
    public CollectionModel<AddressResponseModel> getUserAddresses(@PathVariable String id) {
        ModelMapper modelMapper = new ModelMapper();
        List<AddressResponseModel> returnValue = new ArrayList<>();
        Type listType = new TypeToken<List<AddressResponseModel>>() {}.getType();

        List<AddressDto> addresses = addressesService.getAddresses(id);


        if (addresses != null && !addresses.isEmpty()) {
            returnValue = modelMapper.map(addresses, listType);
        }

        //Add links
        for (AddressResponseModel address:
             returnValue) {
            Link addressLink = WebMvcLinkBuilder.linkTo
                    (WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id, address.getAddressId()))
                    .withSelfRel();
            Link userLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(UserController.class).getUser(id)).withRel("user");

            address.add(addressLink).add(userLink);
        }

        return new CollectionModel<>(returnValue);
    }

    @GetMapping(path="/{userId}/addresses/{addressId}")
    public EntityModel<AddressResponseModel> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        ModelMapper modelMapper = new ModelMapper();

        AddressDto address = addressesService.getAddress(userId, addressId);


        if (address != null ) {
            AddressResponseModel returnValue = modelMapper.map(address, AddressResponseModel.class);

            //Add addresses to the link
            Link addressLink = WebMvcLinkBuilder.linkTo
                    (WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
                    .withSelfRel();
            Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
            Link allAddresses = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).slash("addresses")
                    .withRel("allAddresses");
            returnValue.add(addressLink);
            returnValue.add(userLink);
            returnValue.add(allAddresses);

            return new EntityModel<>(returnValue);
        }


        return new EntityModel<>(new AddressResponseModel());
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailRequestModel userDetail) throws UserServiceException {
        if (userDetail.getFirstName().isEmpty()) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetail, UserDto.class);

        UserDto createUser = userService.createUser(userDto);
        UserRest returnValue = modelMapper.map(createUser, UserRest.class);

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
