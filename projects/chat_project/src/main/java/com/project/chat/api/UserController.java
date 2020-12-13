package com.project.chat.api;

import com.project.chat.model.Person;
import com.project.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@RequestMapping("api/user")
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void addUser(@Valid @NotNull @RequestBody Person person){
        userService.addUser(person);
    }

    @GetMapping
    public List<Person> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping(path = "{id}")
    public Person getUserById(@PathVariable("id") UUID id){
        return userService.getUserByID(id).orElse(null);
    }

    @DeleteMapping(path = "{id}")
    public void deleteUserById(@PathVariable("id") UUID id){
        userService.deleteUser(id);
    }

    @PutMapping(path = "{id}")
    public void updateUser(@PathVariable("id") UUID id, @Valid @NotNull @RequestBody Person person){
        userService.updateUser(id, person);
    }

    @PutMapping(path = "/login")
    public UUID login(@Valid @NotNull @RequestBody Person person){
        return userService.login(person);
    }
}
