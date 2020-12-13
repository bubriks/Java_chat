package com.project.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
public class Chat {

    @Id
    @GeneratedValue
    private UUID id;
    @NotBlank
    @NotNull
    @Column(unique=true)
    private String name;
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Set<Person> users;
    @OneToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Set<Message> messages;

    public Chat() {
    }

    public Chat(@JsonProperty("id") UUID id,
                @JsonProperty("name") String name,
                @JsonProperty("users") Set<Person> users,
                @JsonProperty("messages") Set<Message> messages) {
        this.id = id;
        this.name = name;
        this.users = (users == null ? new HashSet<>() : users);
        this.messages = (messages == null ? new HashSet<>() : messages);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Person> getUsers() {
        return users;
    }

    public void addUser(Person person){
        users.add(person);
    }

    public void removeUser(Person person){
        users.remove(person);
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public void removeMessage(Message message){
        messages.remove(message);
    }
}
