/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository.memory;

import domain.Artist;
import exception.ArtistNotFoundException;
import exception.InvalidInputException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import repository.ArtistRepository;

/**
 *
 * @author User
 */
public class MemoryArtistRepository implements ArtistRepository {

    private long nextArtistId = 1;
    private final Map<String, Artist> repo;

    public MemoryArtistRepository() {
        this.repo = new TreeMap<>();
    }

    @Override
    public Artist retrieve(String artistId) {
        return repo.get(artistId);
    }

    @Override
    public Artist create(String artistName) throws InvalidInputException {
        var id = String.format("A%011d", nextArtistId); 
        if (repo.containsKey(id)) {
            throw new InvalidInputException("Id already exsisted, please try again.");
        }
        var artist = new Artist(id, artistName);
        repo.put(id, artist);
        ++nextArtistId;
        return artist;
    }

    @Override
    public boolean update(Artist artist) throws ArtistNotFoundException{
        if (artist == null) {
            throw new ArtistNotFoundException("Can not find this artist, please try again.");
        }
        repo.replace(artist.getId(), artist);
        return true;
    }

    @Override
    public Stream<Artist> stream() {
        return repo.values().stream();
    }

}
