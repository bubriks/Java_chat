package com.project.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private Person sender;
    @NotBlank
    @NotNull
    private String text;

    public Message() {
    }

    public Message(@JsonProperty("id") int id,
                @JsonProperty("sender") Person sender,
                @JsonProperty("text") String text) {
        this.id = id;
        this.sender = sender;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public Person getSender() {
        return sender;
    }

    public void setSender(Person sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return text;
    }
}
