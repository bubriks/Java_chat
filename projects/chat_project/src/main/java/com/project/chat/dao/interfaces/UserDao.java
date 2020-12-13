package com.project.chat.dao.interfaces;

import com.project.chat.model.Person;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {

    void insertUser(Person person);

    //default int insertUser(AppUser appUser){
    //    UUID id = UUID.randomUUID();
    //    return insertUser(id, appUser);
    //}

    List<Person> selectAllUsers();

    Optional<Person> selectUserById(UUID id);

    Optional<Person> selectUserByUsername(String username);

    void deleteUserById(UUID id);

    void updateUser(UUID id, Person person);

    UUID login(Person person);
}
