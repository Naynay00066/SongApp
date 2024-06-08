package repository.file;

import domain.User;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import exception.UserNotFoundException;
import exception.InvalidInputException;
import repository.UserRepository;

public class FileUserRepository implements UserRepository{
    private String filename = "user.dat";
    private long nextUserId;
    private Map<String, User> repo;
    private File f = new File(filename);

    public FileUserRepository() {
        if (f.exists()) {
            try ( FileInputStream fi = new FileInputStream(f);
                  BufferedInputStream bfi = new BufferedInputStream(fi);
                  ObjectInputStream obi = new ObjectInputStream(bfi);) {

                    try {
                        this.nextUserId = obi.readLong();
                        this.repo = (Map<String, User>) obi.readObject();
                    }
                    catch (EOFException e) {
                        this.nextUserId = 1;
                        this.repo = new TreeMap<>();
                    }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.nextUserId = 1;
            this.repo = new TreeMap<>();
        }
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
        saveRepo();
        return user;
    }

    @Override
    public boolean update(User user) throws UserNotFoundException {
        if (user == null) throw new UserNotFoundException();
        repo.replace(user.getId(), user);
        saveRepo();
        return true;
    }

    @Override
    public Stream<User> stream() {
        return repo.values().stream();
    }

    private void saveRepo() {
        try ( FileOutputStream fi = new FileOutputStream(f);
              BufferedOutputStream bfi = new BufferedOutputStream(fi);
              ObjectOutputStream obi = new ObjectOutputStream(bfi);) {
            obi.writeLong(nextUserId);
            obi.writeObject(repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}