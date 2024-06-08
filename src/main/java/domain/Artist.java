/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

import exception.InvalidInputException;

/**
 *
 * @author User
 */

//add exception
public class Artist extends User {

    public Artist(String artistId, String userName) throws InvalidInputException {
        super(artistId, userName);
    }
    
    @Override
    public String toString() {
        return String.format("Artist(id: %s, name: %s)", super.getId(),super.getName());
    } 
}
