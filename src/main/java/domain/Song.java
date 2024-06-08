/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

import exception.InvalidInputException;
import java.io.Serializable;

/**
 *
 * @author User
 */

//add exception
public class Song implements Comparable<Song>, Serializable {

    private final String songId;
    private String title;
    private final Artist artist;

    public Song(String songId, String title, Artist artist) throws InvalidInputException {
        if(songId == null || title == null || title.isBlank() || artist == null) {
            throw new InvalidInputException();
        }
        this.songId = songId;
        this.title = title;
        this.artist = artist;
    }

    public String getSongId() {
        return songId;
    }

    public String getTitle() {
        return title;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setTitle(String title) throws InvalidInputException {
         if(title == null || title.isBlank()) {
            throw new InvalidInputException();
        }
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("Song id %s(title: %s by %s)", songId, title, artist.getName());
    }

    @Override
    public int compareTo(Song song) {
        return songId.compareTo(song.songId);
    }

}
