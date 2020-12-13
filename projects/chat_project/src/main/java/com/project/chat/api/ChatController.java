package com.project.chat.api;

import com.project.chat.model.Chat;
import com.project.chat.model.Message;
import com.project.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@RequestMapping("api/chat")
@RestController
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public void addChat(@RequestParam UUID userId, @Valid @NotNull @RequestBody Chat chat){
        //can create profiles if provided
        chatService.addChat(userId, chat);
    }

    @GetMapping(params = "userId")
    public List<Chat> selectKnownChats(@RequestParam UUID userId){
        return chatService.selectKnownChats(userId);
    }

    @GetMapping(params = "chatId")
    public Chat getChatByID(@RequestParam UUID chatId){
        return chatService.getChatByID(chatId).orElse(null);
    }

    @DeleteMapping(path = "{id}")
    public void deleteChat(@PathVariable("id") UUID id){
        chatService.deleteChat(id);
    }

    @PutMapping(path = "{id}")
    public void updateUser(@PathVariable("id") UUID id, @Valid @NotNull @RequestBody Chat chat){
        chatService.updateChat(id, chat);
    }

    @PutMapping(path = "/user/add")
    public void addUserToChat(@RequestParam String username, @RequestParam UUID chatId){
        chatService.addUserToChat(username, chatId);
    }

    @PutMapping(path = "/user/remove")
    public void removeUserFromChat(@RequestParam String username, @RequestParam UUID chatId){
        chatService.removeUserFromChat(username, chatId);
    }

    @PutMapping(path = "/message/add")
    public void addMessageToChat(@Valid @NotNull @RequestBody Message message, @RequestParam UUID chatId){
        chatService.addMessageToChat(message, chatId);
    }

    @PutMapping(path = "/message/remove")
    public void removeMessageFromChat(@RequestParam int messageId, @RequestParam UUID chatId){
        chatService.removeMessageFromChat(messageId, chatId);
    }
}
