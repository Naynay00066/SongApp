package repository.database;

import domain.Playlist;
import domain.Song;
import domain.User;
import exception.InvalidInputException;
import exception.PlaylistNotFoundException;
import exception.SongNotFoundException;
import exception.UserNotFoundException;
import repository.PlaylistRepository;
import repository.SongRepository;
import repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class DatabasePlaylistRepository implements PlaylistRepository {
    private long nextPlaylistId = 1;
    Connection connect = DatabaseConnection.connect();
    UserRepository userRepository = new DatabaseUserRepository();
    SongRepository songRepository = new DatabaseSongRepository();

    @Override
    public Playlist retrieve(String playlistId) {
        Playlist playlist = null;
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM playlist WHERE playlistId=?")) {
            stmt.setString(1, playlistId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = userRepository.retrieve(rs.getString("ownerId"));
                ArrayList<Song> songs = addSongFromDBToPlaylist(playlistId);
                playlist = new Playlist(user, rs.getString("playlistId"), rs.getString("playlistName"), songs);
            }
            return playlist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Playlist create(User owner, String playlistName) throws InvalidInputException, UserNotFoundException {
        if (playlistName == null) throw new InvalidInputException();
        if (owner == null) throw new UserNotFoundException("Can not find this user, please try again.");
        getIdFromDB();
        var id = String.format("P%011d", nextPlaylistId);
        Playlist playlist = new Playlist(owner, id, playlistName, new ArrayList<>());
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO playlist (playlistId, playlistName, ownerId) VALUES (?, ?, ?)")) {
            stmt.setString(1, id);
            stmt.setString(2, playlistName);
            stmt.setString(3, owner.getId());
            stmt.executeUpdate();
            return playlist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Playlist playlist, Song song) throws PlaylistNotFoundException, SongNotFoundException {
        if (playlist == null) throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        if (song == null) throw new SongNotFoundException("Can not find this song, please try again.");
        int beforeUpdate = 0;
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM playlist WHERE playlistId=?")) {
            stmt.setString(1, playlist.getPlaylistId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) beforeUpdate = rs.getInt("totalSong");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try (PreparedStatement stmt = connect.prepareStatement("UPDATE playlist SET playlistName=?, totalSong=? WHERE playlistId=?")) {
            stmt.setString(1, playlist.getPlaylistName());
            stmt.setInt(2, playlist.getCount());
            stmt.setString(3, playlist.getPlaylistId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (beforeUpdate < playlist.getCount()) {
            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO playlist_song (playlistId, songId) VALUES (?, ?)")) {
                String songId = playlist.getSongs().get(playlist.getCount() - 1).getSongId();
                stmt.setString(1, playlist.getPlaylistId());
                stmt.setString(2, songId);
                stmt.executeUpdate();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else if (beforeUpdate == playlist.getCount()) {
            return true;
        } else {
            try (PreparedStatement stmt = connect.prepareStatement("DELETE FROM playlist_song WHERE playlistId=? AND songId=?")) {
                stmt.setString(1, playlist.getPlaylistId());
                stmt.setString(2, song.getSongId());
                stmt.executeUpdate();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean delete(User owner, Playlist playlist) throws UserNotFoundException, PlaylistNotFoundException {
        if (owner == null) throw new UserNotFoundException("Can not find this user, please try again.");
        if (playlist == null) throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        try (PreparedStatement stmt = connect.prepareStatement("DELETE FROM playlist WHERE playlistId=?")) {
            deleteSongsFromPlaylist(playlist.getPlaylistId());
            stmt.setString(1, playlist.getPlaylistId());
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Stream<Playlist> stream() {
        Map<String, Playlist> repo = new TreeMap<>();
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM playlist")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = userRepository.retrieve(rs.getString("ownerId"));
                ArrayList<Song> songs = addSongFromDBToPlaylist(rs.getString("playlistId"));
                Playlist playlist = new Playlist(user, rs.getString("playlistId"), rs.getString("playlistName"), songs);
                repo.put(playlist.getPlaylistId(), playlist);
            }
            return repo.values().stream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<Song> addSongFromDBToPlaylist(String playlistId) {
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM playlist_song WHERE playlistId=?")) {
            stmt.setString(1, playlistId);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Song> songs = new ArrayList<>();
            while (rs.next()) {
                Song song = songRepository.retrieve(rs.getString("songId"));
                songs.add(song);
            }
            return songs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void deleteSongsFromPlaylist(String playlistId) { //have to delete song before delete entire pl
        try (PreparedStatement stmt = connect.prepareStatement("DELETE FROM playlist_song WHERE playlistId=?")) {
            stmt.setString(1, playlistId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIdFromDB() {
        try (PreparedStatement stmt = connect.prepareStatement("SELECT MAX(playlistId) FROM playlist")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            String maxId = rs.getString(1);
            if (maxId == null) {
                nextPlaylistId = 1;
            } else {
                nextPlaylistId = Long.parseLong(maxId.substring(1));
                ++nextPlaylistId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
