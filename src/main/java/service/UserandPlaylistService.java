/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import domain.*;
import exception.*;

import java.util.Optional;
import java.util.stream.Stream;
import repository.PlaylistRepository;
import repository.UserRepository;

/**
 *
 * @author User
 */
public class UserandPlaylistService {

    private final UserRepository userRepo;
    private final PlaylistRepository playlistRepo;
    protected final SongService songService;

    public UserandPlaylistService(UserRepository userRepo, PlaylistRepository playlistRepo, SongService songService) {
        this.userRepo = userRepo;
        this.playlistRepo = playlistRepo;
        this.songService = songService;
    }

    public User signUpUser(String userName) throws InvalidInputException {
        return userRepo.create(userName);
    }

    public boolean rename(String userId, String newUserName) throws InvalidInputException, UserNotFoundException {
        var user = userRepo.retrieve(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        user.rename(newUserName);
        userRepo.update(user);
        return true;
    }

    //renameplaylist
    public Playlist createPlaylist(String userId, String playlistName) throws UserNotFoundException, InvalidInputException {
        var user = userRepo.retrieve(userId);
        if (user == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        return playlistRepo.create(user, playlistName);
    }

    public boolean deletePlaylist(String userId, Playlist playlist) throws UserNotFoundException, InvalidInputException, PlaylistNotFoundException {
        var user = userRepo.retrieve(userId);
        if (user == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        return playlistRepo.delete(user, playlist);
    }

    public Stream listAllPlaylistByUser(String userId) throws UserNotFoundException {
        if (userId == null || userRepo.retrieve(userId) == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        return playlistRepo.stream().filter(pl -> pl.getOwner().getId().equals(userId));
    }

    public long countAllPlaylistById(String userId) throws UserNotFoundException {
        if (userId == null || userRepo.retrieve(userId) == null) {
            throw new UserNotFoundException("Can not find this user, please try again.");
        }
        return playlistRepo.stream().filter(pl -> pl.getOwner().getId().equals(userId)).count();
    }

    public Song addSong(Song song, String playlistId) throws PlaylistNotFoundException, SongNotFoundException {
        var playlist = playlistRepo.retrieve(playlistId);
        if (playlist == null) {
            throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        }
        playlist.addSong(song);
        playlistRepo.update(playlist, song);
        return song;
    }

    public Song removeSong(int index, String playlistId) throws PlaylistNotFoundException, SongNotFoundException, IndexOutOfBoundsException {
        var playlist = playlistRepo.retrieve(playlistId);
        if (playlist == null) {
            throw new PlaylistNotFoundException("Can not find this playlist, please try again.");
        }
        var song = playlist.getSongByIndex(index);
        if (song == null) {
            throw new SongNotFoundException("Can not find this song, please try again.");
        }
        playlist.removeSong(song);
        playlistRepo.update(playlist, song);
        return song;
    }

    public Optional<Song> isExistedSong(Playlist playlist, Song selectedSong) {
        return playlist.getAllSongs().filter(s -> s.getSongId().equals(selectedSong.getSongId())).findFirst();
    }
    
    public Stream<User> getAllUsers(){
        return userRepo.stream();
    }
    
    public long countUsers(){
        return userRepo.stream().count();
    }

    public User getUserById(String userId) {
        return userRepo.retrieve(userId);
    }

    public Playlist getPlaylistById(String playlistId) {
        return playlistRepo.retrieve(playlistId);
    }

}
