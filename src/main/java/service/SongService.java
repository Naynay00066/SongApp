/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import domain.Artist;
import domain.Song;
import exception.ArtistNotFoundException;
import exception.InvalidInputException;
import exception.SongNotFoundException;
import java.util.stream.Stream;
import repository.SongRepository;

/**
 *
 * @author User
 */
public class SongService {

    private final SongRepository songRepo;

    public SongService(SongRepository songRepo) {
        this.songRepo = songRepo;
    }

    public Song createSong(Artist artist, String songName) throws InvalidInputException, ArtistNotFoundException {
        return songRepo.create(artist, songName);
    }

    public boolean deleteSong(Artist artist, Song song) throws SongNotFoundException, ArtistNotFoundException {
        return songRepo.delete(artist, song);
    }

    public String ChangeTitle(String newTitle, String songId) throws InvalidInputException, SongNotFoundException {
        var song = songRepo.retrieve(songId);
        song.setTitle(newTitle);
        songRepo.update(song);
        return newTitle;
    }

    public Song getSongById(String songId) {
        return songRepo.retrieve(songId);
    }

    public Stream<Song> getSongs() {
        return songRepo.stream();
    }

    public Stream searchSongByTitle(String title) {
        return songRepo.stream().filter(s -> s.getTitle().equals(title));
    }

    public Stream searchSongByArtistName(String artistName) {
        return songRepo.stream().filter(s -> s.getArtist().getName().equals(artistName));
    }

    public Stream searchSongByArtistId(String artistId){
        return songRepo.stream().filter(s -> s.getArtist().getId().equals(artistId));
    }

}
