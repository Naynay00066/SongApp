/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import domain.User;
import exception.InvalidInputException;
import exception.UserNotFoundException;
import java.util.stream.Stream;

/**
 *
 * @author User
 */
public interface UserRepository {
    public User retrieve(String userId);
    public User create(String userName) throws InvalidInputException;
    public boolean update(User user) throws UserNotFoundException;
    public Stream<User> stream();
}
