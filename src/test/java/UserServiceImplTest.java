/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 11/03/2020, 19:01
 *
 */

import com.dulvinw.springboot.webapp.io.entity.UserEntity;
import com.dulvinw.springboot.webapp.io.repository.UserRepository;
import com.dulvinw.springboot.webapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    UserService userServiceImpl;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getUser() {
        UserEntity entity = getMockEntity();
        when( userRepository.findByEmail(anyString())).thenReturn(entity);
    }

    private UserEntity getMockEntity() {
        UserEntity entity = new UserEntity();
        entity.setUserId("asd123");
        entity.setEmail("dulvinw@gmail.com");
        entity.setEncryptedPassword("asd123lsc");

        return entity;
    }
}
