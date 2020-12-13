package com.project.chat.dao;

import com.project.chat.dao.interfaces.ChatDao;
import com.project.chat.dao.repository.ChatRepository;
import com.project.chat.model.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("chatDao")
public class ChatDataAccessService implements ChatDao {

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public void insertChat(Chat chat) {
        chatRepository.save(chat);
    }

    @Override
    public List<Chat> selectKnownChats(UUID id) {
        return chatRepository.findByUsersId(id);
    }

    @Override
    public Optional<Chat> selectChatById(UUID id) {
        return chatRepository.findById(id);
    }

    @Override
    public void deleteChatById(UUID id) {
        chatRepository.deleteById(id);
    }

    @Override
    public void updateChat(UUID id, Chat chat){
        chatRepository.update(id,chat.getName());
    }
}
