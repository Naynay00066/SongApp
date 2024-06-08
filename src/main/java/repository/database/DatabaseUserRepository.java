package repository.database;

import domain.Artist;
import domain.Playlist;
import domain.User;
import exception.InvalidInputException;
import exception.PlaylistNotFoundException;
import exception.UserNotFoundException;
import repository.UserRepository;
import java.sql.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class DatabaseUserRepository implements UserRepository {
    private long nextUserId = 1;
    Connection connect = DatabaseConnection.connect();


    @Override
    public User retrieve(String userId) {
        User user = null;
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM user WHERE userId=?")) {
            stmt.setString(1,userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                user = new User(userId, rs.getString("userName"));
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public User create(String userName) throws InvalidInputException {
        if (userName == null) throw new InvalidInputException();
        getIdFromDB();
        var id = String.format("U%011d", nextUserId);
        User user = new User(id, userName);
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO user (userId, userName) VALUES (?, ?)")) {
            stmt.setString(1, id);
            stmt.setString(2, userName);
            stmt.executeUpdate();
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(User user) throws UserNotFoundException {
        if (user == null) throw new UserNotFoundException("Can not find this user, please try again.");
        try (PreparedStatement stmt = connect.prepareStatement("UPDATE user SET userName=? WHERE userId=?")) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getId());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Stream<User> stream() {
        Map<String, User> repo = new TreeMap<>();
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM user")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getString("userId"), rs.getString("userName"));
                repo.put(rs.getString("userId"), user);
            }
            return repo.values().stream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getIdFromDB() {
        try (PreparedStatement stmt = connect.prepareStatement("SELECT MAX(userId) FROM user")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            String maxId = rs.getString(1);
            if(maxId == null){
                nextUserId = 1;
            }else{
            nextUserId = Long.parseLong(maxId.substring(1));
            ++nextUserId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

