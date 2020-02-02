/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 02/02/2020, 10:42
 *
 */

package com.dulvinw.springboot.webapp.io.repository;

import com.dulvinw.springboot.webapp.io.entity.AddressEntity;
import com.dulvinw.springboot.webapp.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUserDetails(UserEntity user);
    AddressEntity findByUserDetailsAndAddressId(UserEntity userDetails, String addressId);
}
