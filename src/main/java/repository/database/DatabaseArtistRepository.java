package repository.database;

import domain.Artist;
import domain.User;
import exception.ArtistNotFoundException;
import exception.InvalidInputException;
import exception.UserNotFoundException;
import repository.ArtistRepository;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class DatabaseArtistRepository implements ArtistRepository {
    private long nextArtistId = 1 ;
    Connection connect = DatabaseConnection.connect();

    @Override
    public Artist retrieve(String artistId) {
        Artist artist = null;
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM artist WHERE artistId=?")) {
            stmt.setString(1,artistId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                artist = new Artist(artistId, rs.getString("artistName"));
            }
            return artist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Artist create(String artistName) throws InvalidInputException {
        if (artistName == null) throw new InvalidInputException();
        getIdFromDB();
        var id = String.format("A%011d", nextArtistId);
        Artist artist = new Artist(id, artistName);
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO artist (artistId, artistName) VALUES (?, ?)")) {
            stmt.setString(1, id);
            stmt.setString(2, artistName);
            stmt.executeUpdate();
            return artist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Artist artist) throws ArtistNotFoundException {
        if (artist == null) throw new ArtistNotFoundException("Can not find this artist, please try again.");
        try (PreparedStatement stmt = connect.prepareStatement("UPDATE artist SET artistName=? WHERE artistId=?")) {
            stmt.setString(1, artist.getName());
            stmt.setString(2, artist.getId());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Stream<Artist> stream() {
        Map<String, Artist> repo = new TreeMap<>();
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM artist")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Artist artist = new Artist(rs.getString("artistId"), rs.getString("artistName"));
                repo.put(rs.getString("artistId"), artist);
            }
            return repo.values().stream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean getIdFromDB() {
        try (PreparedStatement stmt = connect.prepareStatement("SELECT MAX(artistId) FROM artist")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            String maxId = rs.getString(1);
            if(maxId == null){
                nextArtistId = 1;
            }else{
            nextArtistId = Long.parseLong(maxId.substring(1));
            ++nextArtistId;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}