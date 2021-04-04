package ru;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.messages.MessageDTO;
import ru.messages.MessageType;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Project java_core_l2
 *
 * @Author Alexander Grigorev
 * Created 26.02.2021
 * v1.0
 * Контроллер, управляющий логикой визульного отображения
 * реализует Initializable из пакета javaFX, это позволяет при создании объекта выполнить метод initialize, что в нашем случае удобно для инициализации некоторых полей
 * MessageProcessor - интерфейс, что мы создали в пакете Network, чтобы MessageService знал, кому отдавать полученные от сервака сообщения.
 */
public class ChatController implements Initializable, MessageProcessor {

    /**
     * тут поля нашего контроллера, @FXML помечены поля являющие собой элементы интерфейса, с которыми нам надо как-то
     * программно повоздействовать.
     */
    private final String ALL = "SEND TO ALL";

    @FXML
    public TextArea chatArea;
    @FXML
    public ListView onlineUsers;
    @FXML
    public Button btnSendMessage;
    @FXML
    public TextField input;
    @FXML
    public HBox chatBox;
    @FXML
    public HBox inputBox;
    @FXML
    public MenuBar menuBar;
    @FXML
    public HBox authPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passField;
    @FXML
    public HBox changeNickname;
    @FXML
    public TextField newNickname;

    private MessageService messageService;


    /**
     * Собственно метод, который благодаря Initializable вызывается при создании контроллера.
     * В нашем случае сразу коннектится к серверу и делает залипушный список контактов
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageService = new ChatMessageService("localhost", 65500, this);

    }

    /**
     * Просто от нечего делать
     * Метод по соответствующему событию ведет нас на URL в браузере
     *
     * @param actionEvent
     * @throws URISyntaxException
     * @throws IOException
     */
    public void showHelp(ActionEvent actionEvent) throws URISyntaxException, IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(new URI("https://docs.google.com/document/d/1wr0YEtIc5yZtKFu-KITqYnBtp8KC28v2FEYUANL0YAM/edit#"));
    }

    /**
     * Закрытие приложения по кнопке
     *
     * @param actionEvent
     */
    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }


    /**
     * Просто залипуха, можно будет что-то навесить
     *
     * @param actionEvent
     */
    public void mockAction(ActionEvent actionEvent) {
        try {
            throw new RuntimeException("AAAAAAAAAAAAAAAAAAAA!!!!!!!");
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    private void refreshUserList(MessageDTO dto) {
        dto.getUsersOnline().add(0, ALL);
        onlineUsers.setItems(FXCollections.observableArrayList(dto.getUsersOnline()));
        onlineUsers.getSelectionModel().selectFirst();
    }

    /**
     * событие на нажатие enter когда активно поле ввода
     *
     * @param actionEvent
     */
    public void pressEnter(ActionEvent actionEvent) {
        sendMessage();
    }

    /**
     * Событие нажатия на кнопку отправки сообщения
     *
     * @param actionEvent
     */
    public void btnSend(ActionEvent actionEvent) {
        sendMessage();
    }

    /**
     * Метод, который шлет сообщение серваку
     */
    private void sendMessage() {
        String msg = input.getText();
        if (msg.length() == 0) return;

        MessageDTO dto = new MessageDTO();
        String selected = (String) onlineUsers.getSelectionModel().getSelectedItem();
        if (selected.equals(ALL)) dto.setMessageType(MessageType.PUBLIC_MESSAGE);
        else {
            dto.setMessageType(MessageType.PRIVATE_MESSAGE);
            dto.setTo(selected);
            dto.setBody(msg); //*выводим сообщение PRIVATE_MESSAGE в chatArea отправителя
            dto.setFrom(selected); //*передаем кому отправлено PRIVATE_MESSAGE сообщение
            showMessage(dto);


        }

        dto.setBody(msg);
        messageService.sendMessage(dto.convertToJson());
        input.clear();

    }

    /**
     * Выводит сообщение в окно чата
     *
     * @param message
     */
    private void showMessage(MessageDTO message) {
        String msg = String.format("[%s] [%s] -> %s\n", message.getMessageType(), message.getFrom(), message.getBody());

        chatArea.appendText(msg);
        fileEntry(msg);
    }

    private void fileEntry(String msg) {

        File file = new File("message/" + ChatApp.getStage().getTitle() + ".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fw = new FileWriter(file, true)) {
            fw.append(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


//

    public void addMessages(MessageDTO message) throws IOException {

        int n = 1; //*Корректировка количества выводимых строк
        String name = message.getFrom();
        StringBuilder builder = new StringBuilder();
        File file = new File("message/" + name + ".txt");
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        int count = 0;
        int end = n + 8;
        long length = file.length();
        raf.seek(length);
        for (long x = length; x >= 0; x--){
            if (x != 0) {  //*если строк больше выводимых
                raf.seek(x);
                char symbol = (char) raf.read();
                if (symbol == '\n') {
                    count++;
                }
            }else {//*если строк меньше выводимых
                end = count;}
                if (count == end) {
                    for (long i = x; i < length; i++) {
                        raf.seek(i);
                        char symbolArea = (char) raf.read();
                        builder.append(symbolArea);
                        if (symbolArea == '\n') {
                            chatArea.appendText(builder.toString());
                            count--;
                            builder = null;
                            builder = new StringBuilder();
                            if (count == 0) {
                                raf.close();
                                return;
                            }

                        }

                    }
                }




        }


    }


    /**
     * обработчик входящих сообщений, где мы пропишем действия на каждый вид сообщений
     *
     * @param msg
     */
    @Override
    public void processMessage(String msg) {

        Platform.runLater(() -> {
            MessageDTO dto = MessageDTO.convertFromJson(msg);
            System.out.println("Received message");

            switch (dto.getMessageType()) {
                case PUBLIC_MESSAGE, PRIVATE_MESSAGE -> showMessage(dto);
                case CLIENTS_LIST_MESSAGE -> refreshUserList(dto);
                /**
                 * Тут при получении подтверждения авторизации скрываются панели авторизации и показывается основной интерфейс
                 */
                case AUTH_CONFIRM -> {
                    ChatApp.getStage().setTitle(dto.getBody());
                    dto.setFrom(dto.getBody());
                    try {
                        addMessages(dto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    authPanel.setVisible(false);
                    chatBox.setVisible(true);
                    inputBox.setVisible(true);
                    menuBar.setVisible(true);

                }

                case ERROR_MESSAGE -> showError(dto);

            }
        });
    }


    public void aboutTheProgram(ActionEvent actionEvent) {

    }

    public void textSize(ActionEvent actionEvent) {

    }

    public void minimizeToTrade(ActionEvent actionEvent) {

    }

    public void fullScreen(ActionEvent actionEvent) {

    }

    public void topWindows(ActionEvent actionEvent) {

    }

    public void changeNewNickname(ActionEvent actionEvent) {
        chatBox.setVisible(false);
        inputBox.setVisible(false);
        menuBar.setVisible(false);
        changeNickname.setVisible(true);

    }

    public void createNewNickname(ActionEvent actionEvent) {
        if (newNickname.getText().length() < 2) return;
        MessageDTO dto = new MessageDTO();
        dto.setMessageType(MessageType.NEW_NICKNAME);
        dto.setBody(newNickname.getText());
        messageService.sendMessage(dto.convertToJson());
        ChatApp.getStage().setTitle(dto.getBody());
        cancelNewNickname(actionEvent);

    }

    public void cancelNewNickname(ActionEvent actionEvent) {
        chatBox.setVisible(true);
        inputBox.setVisible(true);
        menuBar.setVisible(true);
        changeNickname.setVisible(false);
    }


    public void onlineStatus(ActionEvent actionEvent) {

    }

    public void profile(ActionEvent actionEvent) {

    }

    /**
     * метод отправляющий сообщение с данными для авторизации
     *
     * @param actionEvent
     */
    public void sendAuth(ActionEvent actionEvent) {
        String log = loginField.getText();
        String pass = passField.getText();
        if (log.equals("") || pass.equals("")) return;
        MessageDTO dto = new MessageDTO();
        dto.setLogin(log);
        dto.setPassword(pass);
        dto.setMessageType(MessageType.SEND_AUTH_MESSAGE);
        messageService.sendMessage(dto.convertToJson());

        System.out.println("Sent " + log + " " + pass);
    }

    /**
     * метод, который показывает ошибки в графическом интерфейсе
     *
     * @param e
     */
    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong!");
        alert.setHeaderText(e.getMessage());
        VBox dialog = new VBox();
        Label label = new Label("Trace:");
        TextArea textArea = new TextArea();
        //TODO
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement el : e.getStackTrace()) {
            builder.append(el).append(System.lineSeparator());
        }
        textArea.setText(builder.toString());
        dialog.getChildren().addAll(label, textArea);
        alert.getDialogPane().setContent(dialog);
        alert.showAndWait();
    }

    /**
     * перегрузка предыдущего, будет показывать ошибки, прилетающие с сервера
     *
     * @param dto
     */
    private void showError(MessageDTO dto) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong!");
        alert.setHeaderText(dto.getMessageType().toString());
        VBox dialog = new VBox();
        Label label = new Label("Trace:");
        TextArea textArea = new TextArea();
        textArea.setText(dto.getBody());
        dialog.getChildren().addAll(label, textArea);
        alert.getDialogPane().setContent(dialog);
        alert.showAndWait();
    }

}
