/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package repository;

import domain.Playlist;
import domain.Song;
import domain.User;
import exception.InvalidInputException;
import exception.PlaylistNotFoundException;
import exception.SongNotFoundException;
import exception.UserNotFoundException;
import java.util.stream.Stream;

/**
 *
 * @author User
 */
public interface PlaylistRepository {
    public Playlist retrieve(String playlistId);
    public Playlist create(User owner, String playlistName) throws InvalidInputException, UserNotFoundException;
    public boolean update(Playlist playlist, Song song ) throws PlaylistNotFoundException, SongNotFoundException;
    public boolean delete(User owner, Playlist playlist) throws UserNotFoundException, PlaylistNotFoundException;
    public Stream<Playlist> stream();
}
