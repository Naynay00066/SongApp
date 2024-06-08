/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

/**
 *
 * @author User
 */
public class PlaylistNotFoundException extends Exception {
    public PlaylistNotFoundException() {
        super();
    }

    public PlaylistNotFoundException(String message) {
        super(message);
    }

    public PlaylistNotFoundException(Throwable cause) {
        super(cause);
    }

    public PlaylistNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

