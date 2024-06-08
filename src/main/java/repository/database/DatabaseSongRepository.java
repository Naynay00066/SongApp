package repository.database;

import domain.Artist;
import domain.Playlist;
import domain.Song;
import domain.User;
import exception.*;
import repository.ArtistRepository;
import repository.SongRepository;
import service.ArtistService;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

public class DatabaseSongRepository implements SongRepository {
    private long nextSongId = 1;
    Connection connect = DatabaseConnection.connect();
    ArtistRepository artistRepository = new DatabaseArtistRepository();

    @Override
    public Song retrieve(String songId) {
        this.artistRepository = new DatabaseArtistRepository();
        Song song = null;
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM song WHERE songId=?")) {
            stmt.setString(1,songId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Artist artist = artistRepository.retrieve(rs.getString("artistId"));
                song = new Song(rs.getString("songId"), rs.getString("title"), artist);
            }
            return song;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Song create(Artist artist, String songName) throws ArtistNotFoundException, InvalidInputException {
        if (songName == null) throw new InvalidInputException();
        if (artist == null) throw new ArtistNotFoundException("Can not find this artist, please try again.");
        getIdFromDB();
        var id = String.format("S%011d", nextSongId);
        Song song = new Song(id, songName, artist);
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO song (songId, title, artistId) VALUES (?, ?, ?)")) {
            stmt.setString(1, id);
            stmt.setString(2, songName);
            stmt.setString(3, artist.getId());
            stmt.executeUpdate();
            return song;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Song song) throws SongNotFoundException {
        if (song == null) throw new SongNotFoundException("Can not find this song, please try again.");
        try (PreparedStatement stmt = connect.prepareStatement("UPDATE song SET title=? WHERE songId=?")) {
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getSongId());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Artist artist, Song song) throws SongNotFoundException, ArtistNotFoundException {
        if (artist == null) throw new ArtistNotFoundException("Can not find this artist, please try again.");
        if (song == null) throw new SongNotFoundException("Can not find this song, please try again.");
        try (PreparedStatement stmt = connect.prepareStatement("DELETE FROM song WHERE songId=?")) {
            stmt.setString(1, song.getSongId());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Stream<Song> stream() {
        Map<String, Song> repo = new TreeMap<>();
        this.artistRepository = new DatabaseArtistRepository();
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM song")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Artist artist = artistRepository.retrieve(rs.getString("artistId"));
                Song song = new Song(rs.getString("songId"), rs.getString("title"), artist);
                repo.put(rs.getString("songId"), song);
            }
            return repo.values().stream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getIdFromDB() {
        try (PreparedStatement stmt = connect.prepareStatement("SELECT MAX(songId) FROM song")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            String maxId = rs.getString(1);
            if(maxId == null){
                nextSongId = 1;
            }else{
            nextSongId = Long.parseLong(maxId.substring(1));
            ++nextSongId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}