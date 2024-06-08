/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository.memory;

import domain.User;
import exception.InvalidInputException;
import exception.UserNotFoundException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import repository.UserRepository;

/**
 *
 * @author User
 */
public class MemoryUserRepository implements UserRepository{
    
    private long nextUserId = 1;
    private final Map<String,User> repo;

    public MemoryUserRepository() {
        this.repo = new TreeMap<>();
    }
    
    @Override
    public User retrieve(String userId) {
        return repo.get(userId);
    }

    @Override
    public User create(String userName) throws InvalidInputException{
        var id = String.format("U%011d", nextUserId);
        if (repo.containsKey(id)) throw new InvalidInputException("Id already exsisted, please try again.");
        var user = new User(id,userName);
        repo.put(id, user);
        ++nextUserId;
        return user;
    }

    @Override
    public boolean update(User user) throws UserNotFoundException {
        if (user == null) throw new UserNotFoundException();
        repo.replace(user.getId(), user);
        return true;
    }

    @Override
    public Stream<User> stream() {
        return repo.values().stream();
    }
    
}
