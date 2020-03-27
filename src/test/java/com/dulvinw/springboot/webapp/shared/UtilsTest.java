/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 27/03/2020, 21:50
 *
 */

package com.dulvinw.springboot.webapp.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UtilsTest {

    private static final String DUMMY_USER_ID = "dummyUserID";

    @Autowired
    Utils utils;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testGenerateUserId() {
        String userId1 = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);

        assertNotNull(userId1);
        assertNotNull(userId2);
        assertNotEquals(userId1, userId2);
        assertEquals(30, userId1.length());
        assertEquals(30, userId2.length());
    }

    @Test
    public void testHasTokenExpired() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkdW1teVVzZXJJRCIsImV4cCI6MTU4NTMyNzExM30."
                + "U5bHaJOY8PhItD2tH3524JcAXQTbAa4CtbbtZ45bE08TDHZHbgNEodwlTQaviejiE2ZhAVmnY67rThSQ8G7MKQ";

        boolean isExpired = Utils.hasTokenExpired(token);
        assertTrue(isExpired);
    }

    @Test
    public void testHasTokenNotExpired() {
        String token = utils.generateEmailVerificationToken(DUMMY_USER_ID);

        boolean isExpired = Utils.hasTokenExpired(token);
        assertFalse(isExpired);
    }
}
