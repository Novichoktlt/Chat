package ru;

import ru.auth.AuthService;
import ru.auth.UserServesBase;
import ru.messages.MessageDTO;
import ru.messages.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**

 * Собственно сервер
 * Создает ServerSocket и слушает указанный порт.
 * При подключении кого-либо, создает ClientHandler и отдает работу ему
 */
public class ChatServer {
    private static final int PORT = 65501;
    private List<ClientHandler> onlineClientsList;
    private AuthService authService;

    public ChatServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started");
            authService = new UserServesBase();
            authService.start();
            onlineClientsList = new LinkedList<>();
            while (true) {
                System.out.println("Waiting for connection...");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendPrivateMessage(MessageDTO dto) {
        for (ClientHandler clientHandler : onlineClientsList) {
            if (clientHandler.getCurrentUserName().equals(dto.getTo())) {
                clientHandler.sendMessage(dto);
                break;
            }
        }
    }

    public synchronized void broadcastOnlineClients() {
        MessageDTO dto = new MessageDTO();
        dto.setMessageType(MessageType.CLIENTS_LIST_MESSAGE);
        List<String> onlines = new LinkedList<>();
        for (ClientHandler clientHandler : onlineClientsList) {
            onlines.add(clientHandler.getCurrentUserName());
        }
        dto.setUsersOnline(onlines);
        broadcastMessage(dto);
    }

    public synchronized boolean isUserBusy(String username) {
        for (ClientHandler clientHandler : onlineClientsList) {
            if (clientHandler.getCurrentUserName().equals(username)) return true;
        }
        return false;
    }

    public synchronized void broadcastMessage(MessageDTO dto) {
        for (ClientHandler clientHandler : onlineClientsList) {
            clientHandler.sendMessage(dto);
        }
    }

    public synchronized void subscribe(ClientHandler c) {
        onlineClientsList.add(c);
        broadcastOnlineClients();
    }

    public synchronized void unsubscribe(ClientHandler c) {
        onlineClientsList.remove(c);
        broadcastOnlineClients();
    }

    public AuthService getAuthService() {
        return authService;
    }
}
