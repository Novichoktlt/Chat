package ru;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.messages.MessageDTO;
import ru.messages.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**

 * Обработчик клиентов. Создается сервером на каждое подключение и получает свой сокет.
 * Работает с одним сокетом/клиентом
 * обрабатывает отправку сообщений данному конкретному клиенту и обработку сообщений, поступивших от клиента
 */
public class ClientHandler {
    public static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private ChatServer chatServer;
    private String currentUserName;



    public ClientHandler(Socket socket, ChatServer chatServer) {
        try {
            this.chatServer = chatServer;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            LOGGER.info("CH created!");
            /**
             * в отдельном потоке запускается авторизация, которая при успехе завершается и запускается бесконечный цикл чтения сообщений
             */
            chatServer.getExecutorService().execute(() ->{
                try {
                    authenticate();
                    readMessages();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            });
        } catch (IOException e) {
            LOGGER.error(e);

        }

    }


    /**
     * метод отправляет сообщение клиенту, который привязан к этому обработчику
     *
     * @param dto
     */
    public void sendMessage(MessageDTO dto) {
        try {
            outputStream.writeUTF(dto.convertToJson());
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Цикл чтения и обработки сообщений
     */
    private void readMessages() throws IOException {
        try {
            while (!Thread.currentThread().isInterrupted() || socket.isConnected()) {
                String msg = inputStream.readUTF();
                MessageDTO dto = MessageDTO.convertFromJson(msg);
                dto.setFrom(currentUserName);
                LOGGER.info("Клиент прислал сообщение");
                switch (dto.getMessageType()) {
                    case PUBLIC_MESSAGE -> chatServer.broadcastMessage(dto);
                    case PRIVATE_MESSAGE -> chatServer.sendPrivateMessage(dto);
                    case NEW_NICKNAME -> changeUsername(dto);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
            Thread.currentThread().interrupt();
        } finally {

            closeHandler();
        }
    }

    private void changeUsername(MessageDTO dto) {
        String s = chatServer.getAuthService().changeUsername(currentUserName, dto.getBody());
        MessageDTO response = new MessageDTO();
        if (!s.equals(currentUserName) && !s.equals(" ")){
            response.setMessageType(MessageType.CHANGE_NICKNAME_SUCCESS);
            response.setBody(s);
            currentUserName = s;
            chatServer.broadcastOnlineClients();

        }else{
            response.setMessageType(MessageType.ERROR_MESSAGE);
            LOGGER.info("Error changing username");
        }
        sendMessage(response);

    }

    /**
     * цикл аутентификации
     * Сейчас завершится только после успеха
     */
    private void authenticate() {
        Timer timer = new Timer();
        LOGGER.info("Authenticate started!");
        try {
            while (true) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            socket.close();
                           } catch (IOException e) {
                            LOGGER.error(e);

                        }

                    }
                }, 120000);

                String authMessage = inputStream.readUTF();
                if (timer != null) timer.cancel();
                LOGGER.info("received msg ");
                MessageDTO dto = MessageDTO.convertFromJson(authMessage);
                String username = chatServer.getAuthService().getUsernameByLoginPass(dto.getLogin(), dto.getPassword());
                MessageDTO response = new MessageDTO();
                if (username == null) {
                    response.setMessageType(MessageType.ERROR_MESSAGE);
                    response.setBody("Wrong login or pass!");
                    LOGGER.info("Wrong auth");
                } else if (chatServer.isUserBusy(username)) {
                    response.setMessageType(MessageType.ERROR_MESSAGE);
                    response.setBody("U're clone!!!");
                    LOGGER.info("Clone");
                } else {
                    response.setMessageType(MessageType.AUTH_CONFIRM);
                    response.setBody(username);
                    currentUserName = username;
                    chatServer.subscribe(this);
                    LOGGER.info("Subscribed");
                    sendMessage(response);
                    break;
                }
                sendMessage(response);

            }
        } catch (IOException e) {
            LOGGER.error(e);
            closeHandler();

        }
    }

    /**
     * метод закрытия, пока не использовали
     */
    public void closeHandler() {
        try {
            chatServer.unsubscribe(this);
            socket.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public String getCurrentUserName() {
        return currentUserName;
    }
}
