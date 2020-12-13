package com.project.chat.dao.repository;

import com.project.chat.model.Chat;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ChatRepository extends CrudRepository<Chat, UUID> {

    List<Chat> findByUsersId(UUID id);

    /*
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO chat_users(users_id, chat_id) VALUES (:userId,:chatId)",
            nativeQuery = true)
    void addUserToChat(@Param(value = "userId") UUID userId,
                @Param(value = "chatId") UUID chatId);
    */

    @Modifying
    @Transactional
    @Query("update Chat c set c.name = :name where c.id = :id")
    void update(@Param(value = "id") UUID id,
                @Param(value = "name") String name);
}
