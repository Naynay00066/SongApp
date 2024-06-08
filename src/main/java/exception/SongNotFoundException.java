/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

/**
 *
 * @author User
 */
public class SongNotFoundException extends Exception {
    public SongNotFoundException() {
        super();
    }

    public SongNotFoundException(String message) {
        super(message);
    }

    public SongNotFoundException(Throwable cause) {
        super(cause);
    }

    public SongNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
