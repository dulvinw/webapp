/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 11/03/2020, 19:01
 *
 */

import com.dulvinw.springboot.webapp.io.entity.AddressEntity;
import com.dulvinw.springboot.webapp.io.entity.UserEntity;
import com.dulvinw.springboot.webapp.io.repository.UserRepository;
import com.dulvinw.springboot.webapp.service.impl.UserServiceImpl;
import com.dulvinw.springboot.webapp.shared.AmazonSES;
import com.dulvinw.springboot.webapp.shared.Utils;
import com.dulvinw.springboot.webapp.shared.dto.AddressDto;
import com.dulvinw.springboot.webapp.shared.dto.UserDto;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private static final String PASSWORD = "dul123fixM";

    private static final String USER_ID = "abc123fix";

    private static final String ADDRESS_ID = "abc123fixAd";

    private static final String EMAIL_VERIFICATION_TOKEN = "abc123fixEVT";

    public static final String EMAIL = "dulvinw@gmail.com";

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    Utils utils;

    @Mock
    AmazonSES amazonSES;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetUser() {
        UserEntity entity = getMockEntity();
        when( userRepository.findByEmail(anyString())).thenReturn(entity);


        UserDto user = userServiceImpl.getUser("test@test.com");

        assertNotNull(user);
        assertEquals("abc123fix", user.getUserId());
    }

    @Test
    public void testGetUserNull() {
        when( userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userServiceImpl.getUser("test@test.com");
        });
    }

    @Test
    public void testCreateUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateUserId(anyInt())).thenReturn(USER_ID);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(PASSWORD);
        when(utils.generateAddressId(anyInt())).thenReturn(ADDRESS_ID);
        when(utils.generateEmailVerificationToken(anyString())).thenReturn(EMAIL_VERIFICATION_TOKEN);
        when(userRepository.save(any(UserEntity.class))).thenReturn(getMockEntity());
        doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

        UserDto response = userServiceImpl.createUser(getMockDTO());
        Assert.assertEquals(USER_ID, response.getUserId());
        Assert.assertEquals(EMAIL, response.getEmail());
        Assert.assertEquals(PASSWORD, response.getEncryptedPassword());
        Assert.assertEquals(getAddressDtos().size(), response.getAddresses().size());

        verify(utils, times(2)).generateAddressId(anyInt());
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
    }

    @Test
    public void testCreateUser_AlreadyCreatedUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(getMockEntity());

        assertThrows(RuntimeException.class, () -> {
            userServiceImpl.createUser(getMockDTO());
        });
    }

    private UserEntity getMockEntity() {
        UserEntity entity = new UserEntity();
        entity.setUserId(USER_ID);
        entity.setEmail(EMAIL);
        entity.setEncryptedPassword(PASSWORD);
        entity.setAddresses(getAddressEntities());
        entity.setEmailVerificationToken(EMAIL_VERIFICATION_TOKEN);

        return entity;
    }

    private UserDto getMockDTO() {
        UserDto dto = new UserDto();
        dto.setEmail(EMAIL);
        dto.setAddresses(getAddressDtos());
        dto.setUserId(USER_ID);
        dto.setPassword(PASSWORD);

        return dto;
    }

    private List<AddressDto> getAddressDtos() {
        List<AddressDto> addresses = new LinkedList<>();

        AddressDto address1 = new AddressDto();
        address1.setAddressId("1");
        address1.setCity("city1");
        address1.setCountry("country1");
        address1.setPostalCode("1");
        address1.setStreetName("street1");
        address1.setType("home");

        AddressDto address2 = new AddressDto();
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

    private List<AddressEntity> getAddressEntities() {
        List<AddressDto> addressDtos = getAddressDtos();

        ModelMapper modelMapper = new ModelMapper();

        Type listType = new TypeToken<List<AddressEntity>>() {}.getType();


        List<AddressEntity> addressEntities = modelMapper.map(addressDtos, listType);
        return addressEntities;
    }
}
