/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository.memory;

import domain.Playlist;
import domain.Song;
import domain.User;
import exception.InvalidInputException;
import exception.PlaylistNotFoundException;
import exception.SongNotFoundException;
import exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import repository.PlaylistRepository;

/**
 *
 * @author User
 */
public class MemoryPlaylistRepository implements PlaylistRepository {

    private long nextPlaylistId = 1;
    private final Map<String, Playlist> repo;

    public MemoryPlaylistRepository() {
        this.repo = new TreeMap<>();
    }

    @Override
    public Playlist retrieve(String playlistId) {
        return repo.get(playlistId);
    }

    @Override
    public Playlist create(User owner, String playlistName) throws InvalidInputException, UserNotFoundException {
        if (owner == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        var id = String.format("P%011d", nextPlaylistId);
        if (repo.containsKey(id)) {
            return null;
        }
        var playlist = new Playlist(owner, id, playlistName, new ArrayList<>());
        repo.put(id, playlist);
        ++nextPlaylistId;
        return playlist;
    }

    @Override
    public boolean delete(User owner, Playlist playlist) throws UserNotFoundException, PlaylistNotFoundException {
        if (owner == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        if (playlist == null) {
            throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        }
        return repo.remove(playlist.getPlaylistId(), playlist);
    }

    @Override
    public boolean update(Playlist playlist, Song song) throws PlaylistNotFoundException, SongNotFoundException {
        if (playlist == null) {
            throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        }
        if (song == null) {
            throw new SongNotFoundException("Can not find this song, please try again.");
        }
        repo.replace(playlist.getPlaylistId(), playlist);
        return true;
    }

    @Override
    public Stream<Playlist> stream() {
        return repo.values().stream();
    }

}
