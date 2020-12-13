package com.project.chat.dao.interfaces;

import com.project.chat.model.Person;
import com.project.chat.model.Chat;
import com.project.chat.model.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatDao {

    void insertChat(Chat chat);

    List<Chat> selectKnownChats(UUID id);

    Optional<Chat> selectChatById(UUID id);

    void deleteChatById(UUID id);

    void updateChat(UUID id, Chat chat);
}
