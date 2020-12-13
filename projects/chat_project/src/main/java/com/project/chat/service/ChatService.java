package com.project.chat.service;

import com.google.gson.Gson;
import com.project.chat.dao.interfaces.ChatDao;
import com.project.chat.dao.interfaces.MessageDao;
import com.project.chat.model.Person;
import com.project.chat.model.Chat;
import com.project.chat.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.persistence.Persistence;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.*;

@Service
public class ChatService {

    private final ChatDao chatDao;
    private final MessageDao messageDao;
    private final UserService userService;

    @Autowired
    public ChatService(@Qualifier("chatDao") ChatDao chatDao, @Qualifier("messageDao") MessageDao messageDao, UserService userService) {
        this.chatDao = chatDao;
        this.messageDao = messageDao;
        this.userService = userService;
        new Thread(() -> startChat()).start();
    }

    public void addChat(UUID userId, Chat chat){
        Person person = userService.getUserByID(userId).orElseThrow();
        chat.addUser(person);
        chatDao.insertChat(chat);
    }

    public List<Chat> selectKnownChats(UUID id){
        return chatDao.selectKnownChats(id);
    }

    public Optional<Chat> getChatByID(UUID id){
        return chatDao.selectChatById(id);
    }

    public void deleteChat(UUID id){
        chatDao.deleteChatById(id);
    }

    public void updateChat(UUID id, Chat chat){
        chatDao.updateChat(id, chat);
    }

    public void addUserToChat(String username, UUID chatId){
        Person person = userService.getUserByUsername(username).orElseThrow();
        Chat chat = chatDao.selectChatById(chatId).orElseThrow();
        chat.addUser(person);
        chatDao.insertChat(chat);
    }

    public void removeUserFromChat(String username, UUID chatId){
        Person person = userService.getUserByUsername(username).orElseThrow();
        Chat chat = chatDao.selectChatById(chatId).orElseThrow();
        chat.removeUser(person);
        if(chat.getUsers().size() == 0){
            chatDao.deleteChatById(chatId);
        }
        else{
            chatDao.insertChat(chat);
        }
    }

    public void addMessageToChat(Message message, UUID chatId){
        Chat chat = chatDao.selectChatById(chatId).orElseThrow();
        chat.addMessage(message);
        chatDao.insertChat(chat);
    }

    public void removeMessageFromChat(int messageId, UUID chatId){
        Message message = messageDao.selectMessageById(messageId).orElseThrow();
        Chat chat = chatDao.selectChatById(chatId).orElseThrow();
        chat.removeMessage(message);
        chatDao.insertChat(chat);
    }

    private Map<UUID, Map<UUID, Socket>> chatRooms;

    public void startChat(){
        chatRooms = new HashMap<>();
        try {
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            InputStream is = getClass().getClassLoader().getResourceAsStream(".keystore");
            char[] pwd = "rootroot".toCharArray();
            ks.load(is,pwd);

            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, pwd);
            ctx.init(kmf.getKeyManagers(), null, null);
            SSLServerSocketFactory ssf = ctx.getServerSocketFactory();

            SSLServerSocket serverSocket = (SSLServerSocket)ssf.createServerSocket(6483);
            //ServerSocket serverSocket = new ServerSocket(6483);

            while (true) {
                Socket socket = serverSocket.accept();

                new Thread(() -> {
                    setUpConnection(socket);
                }).start();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpConnection(Socket socket){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String[] response = in.readLine().split("::");
            UUID chatId = UUID.fromString(response[0]);
            UUID clientId = UUID.fromString(response[1]);

            Chat chat = chatDao.selectChatById(chatId).orElseThrow();
            if(chat.getUsers().stream().anyMatch(person -> person.getId().equals(clientId))) {
                Map<UUID, Socket> chatUsers = chatRooms.get(chatId);
                if (chatUsers == null) {
                    chatUsers = new HashMap<>();
                    chatUsers.put(clientId, socket);
                    chatRooms.put(chatId, chatUsers);
                } else {
                    chatUsers.put(clientId, socket);
                }

                listen(socket, chatId, clientId);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listen(Socket socket, UUID chatId, UUID clientId){
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("file_message")){
                    String[] response = inputLine.split("::");

                    String directoryName = "C:/Users/bubri//OneDrive/Desktop/server/"+chatId;
                    File directory = new File(directoryName);
                    if (! directory.exists()){
                        directory.mkdirs();
                    }

                    File file = new File(directoryName + "/" + response[1]);
                    OutputStream out = new FileOutputStream(file.getPath());

                    byte[] bytes = new byte[16*1024];

                    int count = bytes.length;
                    while (count == bytes.length) {
                        count = inputStream.read(bytes);
                        out.write(bytes, 0, count);
                    }

                    out.close();
                }
                else {
                    Gson gson = new Gson();
                    Message message = gson.fromJson(inputLine, Message.class);
                    addMessageToChat(message, chatId);
                    Person person = userService.getUserByID(message.getSender().getId()).orElseThrow();
                    message.setSender(person);

                    for (Socket s : chatRooms.get(chatId).values()) { //send to all in the chat
                        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                        out.println(gson.toJson(message));
                    }
                }
            }
        }
        catch (Exception e) {//remove user from chat and if chat is empty remove chat
            e.printStackTrace();
            chatRooms.get(chatId).remove(clientId);
            Map<UUID, Socket> chatUsers = chatRooms.get(chatId);
            if(chatUsers.values().size() == 0){
                chatRooms.remove(chatId);
            }
        }
    }
}
