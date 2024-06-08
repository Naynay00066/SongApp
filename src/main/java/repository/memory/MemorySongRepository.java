/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository.memory;

import domain.Artist;
import domain.Song;
import exception.ArtistNotFoundException;
import exception.InvalidInputException;
import exception.SongNotFoundException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import repository.SongRepository;

/**
 *
 * @author User
 */
public class MemorySongRepository implements SongRepository {

    private long nextSongId = 1;
    private final Map<String, Song> repo;

    public MemorySongRepository() {
        repo = new TreeMap<>();
    }

    @Override
    public Song retrieve(String songId) {
        return repo.get(songId);
    }

    @Override
    public Song create(Artist artist, String title) throws ArtistNotFoundException, InvalidInputException {
        if (artist == null) {
            throw new ArtistNotFoundException();
        }
        var id = String.format("S%011d", nextSongId);
        if (repo.containsKey(id)) {
            return null;
        }
        var song = new Song(id, title, artist);
        repo.put(id, song);
        ++nextSongId;
        return song;
    }

    @Override
    public boolean delete(Artist artist, Song song) throws SongNotFoundException, ArtistNotFoundException {
        if (artist == null) {
            throw new ArtistNotFoundException("Can not find this artist, please try again.");
        }
        if (song == null) {
            throw new SongNotFoundException("Can not find this song, please try again.");
        }
        return repo.remove(song.getSongId(), song);
    }

    @Override
    public boolean update(Song song) throws SongNotFoundException {
        if (song == null) {
            throw new SongNotFoundException("Can not find this song, please try again.");
        }
        repo.replace(song.getSongId(), song);
        return true;
    }

    @Override
    public Stream<Song> stream() {
        return repo.values().stream();
    }

}
