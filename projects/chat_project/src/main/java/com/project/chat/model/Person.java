package com.project.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
public class Person {

    @Id
    @GeneratedValue
    private UUID id;
    @NotBlank
    @NotNull
    @Column(unique=true)
    private String username;
    @NotBlank
    @NotNull
    private String password;

    public Person() {
    }

    public Person(@JsonProperty("id") UUID id,
                  @JsonProperty("username") String username,
                  @JsonProperty("password")String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }
}
