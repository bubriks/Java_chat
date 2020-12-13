package com.project.chat.dao.interfaces;

import com.project.chat.model.Chat;
import com.project.chat.model.Message;

import java.util.Optional;
import java.util.UUID;

public interface MessageDao {

    Optional<Message> selectMessageById(int id);

    void saveMessage(Message message);
}
