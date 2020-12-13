package com.project.chat.dao;

import com.project.chat.dao.interfaces.UserDao;
import com.project.chat.dao.repository.UserRepository;
import com.project.chat.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("userDao")
public class UserDataAccessService implements UserDao {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void insertUser(Person person) {
        userRepository.save(person);
    }

    @Override
    public List<Person> selectAllUsers() {
        List<Person> people = new ArrayList<>();
        userRepository.findAll().forEach(people::add);
        return people;
    }

    @Override
    public Optional<Person> selectUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<Person> selectUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void deleteUserById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateUser(UUID id, Person person) {
        userRepository.update(id, person.getUsername(), person.getPassword());
    }

    @Override
    public UUID login(Person person) {
        return userRepository.login(person.getUsername(), person.getPassword());
    }
}
