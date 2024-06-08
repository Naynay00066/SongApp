/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import domain.Artist;
import domain.Song;
import exception.InvalidInputException;
import exception.ArtistNotFoundException;
import exception.SongNotFoundException;
import java.util.Scanner;
import java.util.stream.Stream;
import repository.database.DatabaseArtistRepository;
import repository.file.FileArtistRepository;
import repository.memory.MemoryArtistRepository;
import service.ArtistService;
import service.SongService;

/**
 *
 * @author User
 */
public class ArtistUI extends StartUI {

    protected final ArtistService artistService;
    protected SongService songService;

    public ArtistUI(SongService songService, int repoType) {
        switch (repoType) {
            case 1 -> {
                artistService = new ArtistService(new MemoryArtistRepository(), songService);
            }
            case 2 -> {
                artistService = new ArtistService(new FileArtistRepository(), songService); //file
            }
            case 3 -> {
                artistService = new ArtistService(new DatabaseArtistRepository(), songService); //database
            }
            default -> {
                artistService = new ArtistService(new MemoryArtistRepository(), songService);
            }
        }
        this.songService = songService;
    }

    public void startArtistUI() {
        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\n");
        artistMainMenu(sc);
    }

    protected void artistMainMenu(Scanner sc) {
        String prompt = """
                    Hello, you are in artist mode. Please select:
                        1. Create account
                        2. Choose exsisted account
                        3. Return to main menu
                    Press [1|2|3]: """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        uiNewArtist(sc);
                    }
                    case 2 -> {
                        ChooseArtistMenu(sc);
                    }
                    case 3 -> {
                        return;
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    protected void uiNewArtist(Scanner sc) {
        String prompt = """
                You are in artist sign up process. 
                Please enter your artist name or press q to return to artist main menu: """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("q")) {
                artistMainMenu(sc);
                break;
            } else if (!input.isBlank()) {
                signUpProcess(sc, input);
                break;
            } else {
                System.out.println("Invalid input, please try again");
                System.out.print(prompt);
            }
        }
    }

    protected void signUpProcess(Scanner sc, String artistName) {
        Artist artist = null;
        try {
            artist = artistService.signUpArtist(artistName);
            System.out.println("Sign up success!");
            System.out.println("Your artist name is: " + artistName);
        } catch (InvalidInputException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        viewAccountOrQuit(sc, artist);
    }

    private void ChooseArtistMenu(Scanner sc) {
        System.out.println("Total exsisted artist account: " + artistService.countAllArtist());
        if(artistService.countAllArtist() != 0) artistService.getArtists().forEach(System.out::println);
        String prompt = """
                        You are in selecting account process.
                        Please type artist id that shown above or press[q] to return to artist main menu: 
                        """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Artist selectedArtist = artistService.getArtistById(input);
            if(selectedArtist != null){
                viewAccountOrQuit(sc, selectedArtist);
                break;
            }else if(input.equalsIgnoreCase("q")){
                artistMainMenu(sc);
                break;
            }else{
                System.out.println("Can not find this account, please try again.");
                System.out.print(prompt);
            }
        }
    }

    protected void viewAccountOrQuit(Scanner sc, Artist artist) { //can be merge with signUpProcess()
        String prompt = "Please press [v] to view your account or [q] to return to the artist main menu: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("v")) {
                uiViewArtist(sc, artist);
                break;
            } else if (input.equalsIgnoreCase("q")) {
                artistMainMenu(sc);
                break;
            } else {
                System.out.println("Invalid input, please try again");
                System.out.println(prompt);
            }
        }
    }

    protected void uiViewArtist(Scanner sc, Artist artist) {
        System.out.println("You are in view artist page");
        System.out.println("Artist id: " + artist.getId());
        System.out.println("Artist name:" + artist.getName());
        int totalSong = 0;
        try {
            totalSong = (int) artistService.countAllSongById(artist.getId());
        } catch (ArtistNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Your total song: " + totalSong);
        if (totalSong != 0) {
            System.out.println("Your song:");
            songService.searchSongByArtistId(artist.getId()).forEach(System.out::println);
        }
        uiSongMenu(sc, artist, totalSong);
    }

    private void uiSongMenu(Scanner sc, Artist artist, int totalSong) {
        String prompt = "Please select:\n1. Publish new song\n2. Select song\n3. Return to artist main menu\nPress [1|2|3]: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        publishSong(sc, artist);
                    }
                    case 2 -> {
                        handleSelectedSong(sc, artist);
                    }
                    case 3 -> {
                        artistMainMenu(sc);
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    private void publishSong(Scanner sc, Artist artist) {
        Song song;
        String prompt = """
                        You are in publishing song process.
                        Please type your song name or press [q] to return to view artist menu: 
                        """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("q")) {
                uiViewArtist(sc, artist);
            } else {
                try {
                    song = artistService.publishSong(artist.getId(), input);
                    System.out.println("Publish song success!");
                    System.out.println("This is your new song: " + song.getTitle());
                    viewSongOrQuit(sc, song, artist);
                } catch (ArtistNotFoundException | InvalidInputException ex) {
                    System.out.println(ex.getMessage());
                    return;
                }
            }
            break;
        }
    }

    protected void viewSongOrQuit(Scanner sc, Song song, Artist artist) { //can be merge with createSong()
        String prompt = "Please press [v] to view your song or [q] to return to view artist menu: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("v")) {
                uiViewSong(sc, song, artist);
                break;
            } else if (input.equalsIgnoreCase("q")) {
                uiViewArtist(sc, artist);
                break;
            } else {
                System.out.println("Invalid input, please try again");
                System.out.println(prompt);
            }
        }
    }

    private void uiViewSong(Scanner sc, Song song, Artist artist) {
        String prompt = String.format("Song id: %s\nSong name: %s\nYou are in view song menu.\nPlease select:\n1. change title\n2. delete song\n3. return to view artist menu\nPress [1|2|3]:", song.getSongId(), song.getTitle());
        System.out.println(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        changeTitle(sc, song, artist);
                    }
                    case 2 -> {
                        deleteSong(sc, song, artist);
                    }
                    case 3 -> {
                        uiViewArtist(sc, artist);
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    private void changeTitle(Scanner sc, Song song, Artist artist) {
        System.out.println("You are in changing title process.");
        System.out.println("Your current title: " + song.getTitle());
        String prompt = "Please type your new title or press[q] to return to view song menu: ";
        System.out.println(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("q")) {
                uiViewSong(sc, song, artist);
                break;
            } else {
                try {
                    artistService.ChangeTitle(input, song.getSongId());
                    System.out.println("Change title success!");
                    System.out.println("This is your new title: " + song.getTitle());
                    uiViewSong(sc, song, artist);
                    break;
                } catch (InvalidInputException | SongNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    private void deleteSong(Scanner sc, Song song, Artist artist) {
        String prompt = String.format("Are you sure you want to delete this song?\n%s\nPress[y|n]: ", song.toString());
        System.out.println(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("y")) {
                try {
                    artistService.deleteSong(artist.getId(), song);
                    System.out.println("Delete song success!");
                    uiViewArtist(sc, artist);
                    break;
                } catch (SongNotFoundException | ArtistNotFoundException ex) {
                    System.out.println(ex.getMessage());
                    uiViewArtist(sc, artist);
                }
                break;
            } else if (input.equalsIgnoreCase("n")) {
                uiViewSong(sc, song, artist);
                break;
            } else {
                System.out.println("Invalid input, please try again.");
                System.out.println(prompt);
            }
        }
    }

    private Song selectSong(Scanner sc, Artist artist) throws ArtistNotFoundException {
        Stream songs = songService.searchSongByArtistId(artist.getId());
        ;
        if (artistService.countAllSongById(artist.getId()) != 0) {
            songs.forEach(System.out::println);
            String prompt = """
                            You are in selecting song process. 
                            Please type your song id to perform next action,
                            or press[q] to return to view artist menu:""";
            System.out.print(prompt);
            while (true) {
                String input = getInput(sc, prompt);
                Song selectedSong = songService.getSongById(input);
                if (selectedSong != null) {
                    return selectedSong;
                } else if (input.equalsIgnoreCase("q")) {
                    return null;
                } else {
                    System.out.println("Can not find this song, please try again.");
                    System.out.println(prompt);
                }

            }
        }
        System.out.println("Can not find your song, please try again.");
        return null;
    }

    private void handleSelectedSong(Scanner sc, Artist artist) {
        Song selectedSong = null;
        try {
            selectedSong = selectSong(sc, artist);

        } catch (ArtistNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        if (selectedSong != null) {
            viewSongOrQuit(sc, selectedSong, artist);
        }else{
            uiViewArtist(sc, artist);
        }
    }

}
