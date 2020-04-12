/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 12/04/2020, 17:55
 *
 */

package com.dulvinw.springboot.webapp.io.repository;

import com.dulvinw.springboot.webapp.io.entity.AddressEntity;
import com.dulvinw.springboot.webapp.io.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    private static final String EMAIL = "dulvin@gmail.com";

    private static final String USER_ID = "dulvinw";

    private static final String PASSWORD = "mit123";

    private static final String FIRST_NAME = "Dulvin";

    private static final String LAST_NAME = "Witharane";

    private static final String EMAIL2 = "dulvin2@gmail.com";

    private static final String USER_ID2 = "dulvinw2";

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        List<UserEntity> users = getMockEntity();
        for (UserEntity user : users) {
            userRepository.save(user);
        }
    }

    @Test
    public void testGetVerifiedUser() {
        Pageable request = PageRequest.of(0, 1);
        Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddresses(request);
        assertNotNull(pages);
        List<UserEntity> userList = pages.getContent();
        assertEquals(1, userList.size());
    }

    @Test
    public void testGetUserFromUserName() {
        List<UserEntity> users = userRepository.findUsersByFirstName(FIRST_NAME);
        assertNotNull(users);

        assertEquals(2, users.size());
    }

    @Test
    public void testGetUserFromLastName() {
        List<UserEntity> users = userRepository.findUsersByLastName(LAST_NAME);
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    public void testGetFirstNameLastNameOfUserFromLastName() {
        List<Object[]> objects = userRepository.findUsersFirstNameAndLastNameGivenTheLastName(LAST_NAME);
        assertNotNull(objects);
        assertEquals(2, objects.size());
        assertEquals(FIRST_NAME, String.valueOf(objects.get(0)[0]));
    }

    @Test
    public void testUpdateEmailVerificationStatus() {
        userRepository.updateEmailVerificationStatus(false, USER_ID2);
        Pageable request = PageRequest.of(0, 2);
        Page<UserEntity> users = userRepository.findAllUsersWithConfirmedEmailAddresses(request);
        assertNotNull(users);
        List<UserEntity> userList = users.getContent();
        assertEquals(1, userList.size());
        assertEquals(USER_ID, userList.get(0).getUserId());
    }

    @Test
    public void testGetUserFromUserId() {
        UserEntity user = userRepository.findUserByUserId(USER_ID);
        assertNotNull(user);
        assertEquals(USER_ID, user.getUserId());
    }

    private List<UserEntity> getMockEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(EMAIL);
        userEntity.setAddresses(getAddressEntities());
        userEntity.setUserId(USER_ID);
        userEntity.setEncryptedPassword(PASSWORD);
        userEntity.setFirstName(FIRST_NAME);
        userEntity.setLastName(LAST_NAME);
        userEntity.setEmailVerificationStatus(true);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setEmail(EMAIL2);
        userEntity2.setAddresses(getAddressEntities());
        userEntity2.setUserId(USER_ID2);
        userEntity2.setEncryptedPassword(PASSWORD);
        userEntity2.setFirstName(FIRST_NAME);
        userEntity2.setLastName(LAST_NAME);
        userEntity2.setEmailVerificationStatus(true);

        List<UserEntity> users = new ArrayList<>();
        users.add(userEntity);
        users.add(userEntity2);

        return users;
    }

    private List<AddressEntity> getAddressEntities() {
        List<AddressEntity> addresses = new LinkedList<>();

        AddressEntity address1 = new AddressEntity();
        address1.setAddressId("1");
        address1.setCity("city1");
        address1.setCountry("country1");
        address1.setPostalCode("1");
        address1.setStreetName("street1");
        address1.setType("home");

        AddressEntity address2 = new AddressEntity();
        address2.setAddressId("2");
        address2.setCity("city2");
        address2.setCountry("country2");
        address2.setPostalCode("2");
        address2.setStreetName("street2");
        address2.setType("billing");

        addresses.add(address1);
        addresses.add(address2);

        return addresses;
    }
}
