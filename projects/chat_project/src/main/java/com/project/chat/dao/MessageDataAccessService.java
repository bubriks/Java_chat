package com.project.chat.dao;

import com.project.chat.dao.interfaces.MessageDao;
import com.project.chat.dao.repository.MessageRepository;
import com.project.chat.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("messageDao")
public class MessageDataAccessService implements MessageDao {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Optional<Message> selectMessageById(int id) {
        return messageRepository.findById(id);
    }

    @Override
    public void saveMessage(Message message) {
        messageRepository.save(message);
    }
}
