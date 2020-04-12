/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 25/01/2020, 23:53
 *
 */

package com.dulvinw.springboot.webapp.io.repository;

import com.dulvinw.springboot.webapp.io.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findByEmailVerificationToken(String token);

    @Query(value = "select * from Users u where u.EMAIL_VERIFICATION_STATUS = true", nativeQuery = true,
    countQuery = "select count(*) from Users u where u.EMAIL_VERIFICATION_STATUS = true")
    Page<UserEntity> findAllUsersWithConfirmedEmailAddresses(Pageable pageableRequest);

    @Query(nativeQuery = true, value = "select * from Users u where u.FIRST_NAME = ?1")
    List<UserEntity> findUsersByFirstName(String firstName);

    @Query(nativeQuery = true, value = "select * from Users u where u.LAST_NAME = :lastName")
    List<UserEntity> findUsersByLastName(@Param("lastName") String lstName);

    @Query(nativeQuery = true, value = "select u.FIRST_NAME, u.LAST_NAME from Users u where u.LAST_NAME = ?1")
    List<Object[]> findUsersFirstNameAndLastNameGivenTheLastName(String lastName);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update Users u set u.EMAIL_VERIFICATION_STATUS = :status "
            + "where u.USER_ID = :userId")
    void updateEmailVerificationStatus(@Param("status") Boolean status, @Param("userId") String userId);

    @Query("select u from UserEntity u where u.userId = :userId")
    UserEntity findUserByUserId(@Param("userId") String userId);

    @Query("select u.firstName, u.lastName from UserEntity u where u.userId = ?1")
    List<Object[]> getFirstNameAndLastNameFromUserId(String userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.emailVerificationStatus = :status WHERE u.userId = :userId")
    void updateEmailVerificationStatusFromJPQL(@Param("status") Boolean status, @Param("userId") String userId);
}

