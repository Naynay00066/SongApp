/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

/**
 *
 * @author User
 */
public class ArtistNotFoundException extends Exception{
    public ArtistNotFoundException() {
        super();
    }

    public ArtistNotFoundException(String message) {
        super(message);
    }

    public ArtistNotFoundException(Throwable cause) {
        super(cause);
    }

    public ArtistNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
