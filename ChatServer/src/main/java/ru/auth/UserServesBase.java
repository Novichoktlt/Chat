package ru.auth;

import java.sql.SQLException;

public class UserServesBase implements AuthService {
    private BaseUserService dbserves;


    @Override
    public void start() {dbserves = BaseUserService.getInstance();

    }

    @Override
    public void stop() {dbserves.closeConnection();

    }

    @Override
    public String getUsernameByLoginPass(String login, String pass) {
        return dbserves.getClientByLoginPass(login, pass);
    }

    @Override
    public String changeUsername(String oldName, String newName) {
        try {
            return dbserves.changeUsername(oldName, newName);
        } catch (SQLException throwables) {
            throw new RuntimeException("Username change unsuccessful");
        }
    }
}
