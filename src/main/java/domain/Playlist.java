/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Stream;
import exception.*;

/**
 *
 * @author User
 */
public class Playlist implements Comparable<Playlist>, Serializable {

    private final String playlistId;
    private String playlistName;
    private final User owner;
    private final ArrayList<Song> songs;
    private int count;

    public Playlist(User owner, String playlistId, String playlistName, ArrayList<Song> songs) throws InvalidInputException {
        if (playlistId == null || playlistId.isBlank() || playlistName == null || playlistName.isBlank()) {
            throw new InvalidInputException();
        }
        this.owner = owner;
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songs = songs;
        this.count = songs.size();
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public int getCount() {
        return count;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void rename(String playlistName) throws InvalidInputException {
        if (playlistName == null || playlistName.isBlank()) {
            throw new InvalidInputException();
        }
        this.playlistName = playlistName;
    }

    public User getOwner() {
        return owner;
    }

    public Song addSong(Song song) {
        if (!songs.contains(song)) {
            songs.add(song);
            count++;
            return song;
        } else {
            return null;
        }
    }

    public void removeSong(Song song) {
        songs.remove(song);
        count--;
    }

    public Song getSongByIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > count) {
            throw new IndexOutOfBoundsException("Can not find song number: " + (index + 1));
        } else {
            return songs.get(index);
        }
    }

    @Override
    public String toString() {
        return String.format("Playlist id %s(name: %s, total song: %d, created by %s)", playlistId, playlistName, count, owner.getName());
    }

    public void getAllSongWithIndex() {
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + songs.get(i).toString());
        }
    }
    
    public Stream<Song> getAllSongs(){
        return songs.stream();
    }

    @Override
    public int compareTo(Playlist playlist) {
        return playlistId.compareTo(playlist.playlistId);
    }
}
