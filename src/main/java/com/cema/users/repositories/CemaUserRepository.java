package com.cema.users.repositories;

import com.cema.users.entities.CemaUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CemaUserRepository extends CrudRepository<CemaUser, Long> {

    CemaUser findCemaUserByUserName(String userName);
}
