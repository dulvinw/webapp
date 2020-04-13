/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 25/01/2020, 23:53
 *
 */

package com.dulvinw.springboot.webapp.service.impl;

import com.dulvinw.springboot.webapp.io.entity.AddressEntity;
import com.dulvinw.springboot.webapp.io.entity.RoleEntity;
import com.dulvinw.springboot.webapp.io.repository.RoleRepository;
import com.dulvinw.springboot.webapp.io.repository.UserRepository;
import com.dulvinw.springboot.webapp.io.entity.UserEntity;
import com.dulvinw.springboot.webapp.security.UserPrincipal;
import com.dulvinw.springboot.webapp.service.UserService;
import com.dulvinw.springboot.webapp.shared.AmazonSES;
import com.dulvinw.springboot.webapp.shared.Utils;
import com.dulvinw.springboot.webapp.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    AmazonSES amazonSES;

    @Override
    public UserDto createUser(UserDto user) {
        UserEntity alreadyCreatedUserByEmail = userRepository.findByEmail(user.getEmail());
        if (alreadyCreatedUserByEmail != null) {
            throw new RuntimeException("Record already exist");
        }

        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        for (AddressEntity userAddress:
             userEntity.getAddresses()) {
            String publicAddressId = utils.generateAddressId(30);
            userAddress.setAddressId(publicAddressId);
            userAddress.setUserDetails(userEntity);
        }

        RoleEntity userRole = roleRepository.findByName("ROLE_USER");
        userEntity.setRoles(Arrays.asList(userRole));

        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));

        UserEntity responseFromRepo = userRepository.save(userEntity);

        UserDto returnResults = modelMapper.map(responseFromRepo, UserDto.class);

//        amazonSES.verifyEmail(returnResults);

        return returnResults;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new UserPrincipal(userEntity);
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String id) {
        UserDto returnValue = new UserDto();
        UserEntity user = userRepository.findByUserId(id);

        if (user == null) {
            throw new UsernameNotFoundException(id);
        }

        BeanUtils.copyProperties(user, returnValue);
        return returnValue;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto returnValue = new UserDto();
        UserEntity user = userRepository.findByUserId(userDto.getUserId());

        if (user == null) {
            throw new UsernameNotFoundException(userDto.getUserId());
        }

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        UserEntity updatedUser = userRepository.save(user);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String id) {
        UserEntity user = userRepository.findByUserId(id);
        if (user == null) {
            throw new UsernameNotFoundException(id);
        }
        userRepository.delete(user);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> userPage = userRepository.findAll(pageableRequest);

        List<UserEntity> users = userPage.getContent();
        for (UserEntity userEntity: users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;

        UserEntity userEntity = userRepository.findByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hasExpired = Utils.hasTokenExpired(token);
            if (!hasExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }
}
