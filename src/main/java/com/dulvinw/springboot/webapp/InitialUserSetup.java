/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 13/04/2020, 21:17
 *
 */

package com.dulvinw.springboot.webapp;

import com.dulvinw.springboot.webapp.io.entity.AuthorityEntity;
import com.dulvinw.springboot.webapp.io.entity.RoleEntity;
import com.dulvinw.springboot.webapp.io.entity.UserEntity;
import com.dulvinw.springboot.webapp.io.repository.AuthorityRepository;
import com.dulvinw.springboot.webapp.io.repository.RoleRepository;
import com.dulvinw.springboot.webapp.io.repository.UserRepository;
import com.dulvinw.springboot.webapp.shared.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;

@Component
public class InitialUserSetup {

    private static final String READ_AUTHORITY = "READ_AUTHORITY";

    private static final String WRITE_AUTHORITY = "WRITE_AUTHORITY";

    private static final String DELETE_AUTHORITY = "DELETE_AUTHORITY";

    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    private static final String USER_ROLE = "ROLE_USER";

    private static final String FIRST_NAME = "Dulvin";

    private static final String LAST_NAME = "Witharane";

    private static final String EMAIL = "dulvinw@gmail.com";

    private static final String PASSWORD = "mit123";

    private static final boolean EMAIL_VERIFICATION_STATUS = true;

    @Autowired
    AuthorityRepository authorityRepository;
    
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        AuthorityEntity readAuthority = createAuthority(READ_AUTHORITY);
        AuthorityEntity writeAuthority = createAuthority(WRITE_AUTHORITY);
        AuthorityEntity deleteAuthority = createAuthority(DELETE_AUTHORITY);
        
        RoleEntity adminRole = createRole(ADMIN_ROLE, Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
        RoleEntity userRole = createRole(USER_ROLE, Arrays.asList(readAuthority, writeAuthority));

        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName(FIRST_NAME);
        adminUser.setLastName(LAST_NAME);
        adminUser.setEmail(EMAIL);
        adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode(PASSWORD));
        adminUser.setEmailVerificationStatus(EMAIL_VERIFICATION_STATUS);
        adminUser.setRoles(Arrays.asList(adminRole));
        adminUser.setUserId(utils.generateUserId(30));

        userRepository.save(adminUser);
    }

    @Transactional
    private AuthorityEntity createAuthority(String name) {
        AuthorityEntity authorityEntity = authorityRepository.findByName(name);

        if (authorityEntity == null) {
            authorityEntity = new AuthorityEntity();
            authorityEntity.setName(name);
            authorityRepository.save(authorityEntity);
        }

        return authorityEntity;
    }

    @Transactional
    private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        RoleEntity role = roleRepository.findByName(name);

        if (role == null) {
            role = new RoleEntity();
            role.setName(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        
        return role;
    }
}
