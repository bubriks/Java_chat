package com.project.chat.dao.repository;

import com.project.chat.model.Person;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<Person, UUID> {

    @Modifying
    @Transactional
    @Query("update Person a set a.username = :username, a.password = :password where a.id = :id")
    void update(@Param(value = "id") UUID id,
                @Param(value = "username") String username,
                @Param(value = "password")String password);

    @Transactional
    @Query("SELECT id from Person where username = :username and password = :password")
    UUID login(@Param(value = "username") String username,
                @Param(value = "password")String password);

    Optional<Person> findByUsername(String username);
}
