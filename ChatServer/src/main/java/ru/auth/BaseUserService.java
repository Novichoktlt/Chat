package ru.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class BaseUserService {
    public static final Logger LOGGER = LogManager.getLogger(BaseUserService.class);
    private static BaseUserService instance;
    private final String DRIVER = "org.sqlite.JDBC";
    private final String CONNECTION = "jdbc:sqlite:ChatServer/db/client.db";
    private final String GET_USERNAME = "select username from clients where login = ? AND password = ?;";
    private final String CHANGE_USERNAME = "update clients set username = ? where username = ?;";
    private final String CREATE_DB = "create table if not exists clients (id integer primary key autoincrement, login text unique not null, password text unique not null, username text unique not null);";

    private Connection connection;

    private BaseUserService() {
        try {
            connect();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
//        createDb();
    }

    public static BaseUserService getInstance(){
        if (instance != null) return  instance;
        instance = new BaseUserService();
        return instance;
    }
    public String changeUsername(String oldName, String newName) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(CHANGE_USERNAME)){
            ps.setString(1, newName);
            ps.setString(2, oldName);
            if (ps.executeUpdate() > 0) return newName;
        }
        return oldName;
    }
    public String getClientByLoginPass(String login, String pass) {
        try (PreparedStatement ps = connection.prepareStatement(GET_USERNAME)){
            ps.setString(1, login);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String result = rs.getString("username");
                rs.close();
                return result;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return " ";
    }
    private void createDb(){
        try {
            Statement st = connection.createStatement();
            st.execute(CREATE_DB);
            st.execute("insert into clients (login, password, username) values ('log1', 'pass1', 'user1'), ('log2', 'pass2', 'user2'), ('log3', 'pass3', 'user3');");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        connection = DriverManager.getConnection(CONNECTION);
        LOGGER.info("Connected to DB");
    }
    public void closeConnection(){

        try {
            if(connection != null)connection.close();
            LOGGER.info("Disconnected from DB");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
