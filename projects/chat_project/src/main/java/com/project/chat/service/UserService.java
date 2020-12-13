package com.project.chat.service;

import com.project.chat.dao.interfaces.UserDao;
import com.project.chat.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(@Qualifier("userDao") UserDao userDao) {
        this.userDao = userDao;
    }

    public void addUser(Person person){
        userDao.insertUser(person);
    }

    public List<Person> getAllUsers(){
        return userDao.selectAllUsers();
    }

    public Optional<Person> getUserByID(UUID id){
        return userDao.selectUserById(id);
    }

    public Optional<Person> getUserByUsername(String username){
        return userDao.selectUserByUsername(username);
    }

    public void deleteUser(UUID id){
         userDao.deleteUserById(id);
    }

    public void updateUser(UUID id, Person person){
        userDao.updateUser(id, person);
    }

    public UUID login(Person person){
        return userDao.login(person);
    }
}
