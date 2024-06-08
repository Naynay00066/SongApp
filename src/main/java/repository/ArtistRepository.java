/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import domain.Artist;
import exception.ArtistNotFoundException;
import exception.InvalidInputException;
import java.util.stream.Stream;

/**
 *
 * @author User
 */
public interface ArtistRepository {
    public Artist retrieve(String artistId);
    public Artist create(String artistName) throws InvalidInputException;
    public boolean update(Artist artist)throws ArtistNotFoundException; 
    public Stream<Artist> stream();
}
