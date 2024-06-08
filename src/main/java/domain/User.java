/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

import java.io.Serializable;
import exception.InvalidInputException;

/**
 *
 * @author User
 */
public class User implements Comparable<User>, Serializable {

    private String userId;
    private String userName;

    public User(String userId, String userName) throws InvalidInputException {
        if (userId == null || userId.isBlank() || userName == null || userId.isBlank()) {
            throw new InvalidInputException("User name ore User id is wrong, please try again.");
        }
        this.userId = userId;
        this.userName = userName;
    }

    public String getId() {
        return userId;
    }

    public String getName() {
        return userName;
    }

    public void rename(String userName) throws InvalidInputException {
         if (userName == null || userId.isBlank()) {
            throw new InvalidInputException();
        }
        this.userName = userName;
    }

    @Override
    public String toString() {
        return String.format("User(Id: %s, name: %s)", userId, userName);
    }

    @Override
    public int compareTo(User user) {
        return userId.compareTo(user.userId);
    }

}
