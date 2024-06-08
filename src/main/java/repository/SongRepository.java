/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package repository;

import domain.Artist;
import domain.Song;
import exception.ArtistNotFoundException;
import exception.InvalidInputException;
import exception.SongNotFoundException;
import java.util.stream.Stream;

/**
 *
 * @author User
 */
public interface SongRepository {
    public Song retrieve(String songId);
    public Song create(Artist artist, String songName) throws ArtistNotFoundException, InvalidInputException;
    public boolean update(Song song) throws SongNotFoundException;
    public boolean delete(Artist artist, Song song) throws SongNotFoundException, ArtistNotFoundException;
    public Stream<Song> stream();
}
