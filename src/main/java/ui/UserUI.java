package ui;

import domain.Playlist;
import domain.Song;
import domain.User;
import exception.InvalidInputException;
import exception.PlaylistNotFoundException;
import exception.SongNotFoundException;
import exception.UserNotFoundException;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;
import repository.database.DatabasePlaylistRepository;
import repository.database.DatabaseUserRepository;
import repository.file.FilePlaylistRepository;
import repository.file.FileUserRepository;
import service.*;
import repository.memory.*;

public class UserUI extends StartUI {

    protected final UserandPlaylistService userService;
    protected SongService songService;

    public UserUI(SongService songService, int repoType) {
        switch (repoType) {
            case 1 -> {
                userService = new UserandPlaylistService(new MemoryUserRepository(), new MemoryPlaylistRepository(), songService);
            }
            case 2 -> {
                userService = new UserandPlaylistService(new FileUserRepository(), new FilePlaylistRepository(), songService);
            }
            case 3 -> {
                userService = new UserandPlaylistService(new DatabaseUserRepository(), new DatabasePlaylistRepository(), songService);
            }
            default -> {
                userService = new UserandPlaylistService(new MemoryUserRepository(), new MemoryPlaylistRepository(), songService);
            }
        }
        this.songService = songService;
    }

    public void startUserUI() {
        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\n");
        userMainMenu(sc);
    }

    protected void userMainMenu(Scanner sc) {
        String prompt = """
                    Hello, you are in normal user mode. Please select:
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
                        uiNewUser(sc);
                    }
                    case 2 -> {
                        ChooseUserMenu(sc);
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

    protected void uiNewUser(Scanner sc) {
        String prompt = """
                You are in normal account sign up process. 
                Please enter your user name or press q to return to user menu: """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("q")) {
                userMainMenu(sc);
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

    protected void signUpProcess(Scanner sc, String userName) {
        User user = null;
        try {
            user = userService.signUpUser(userName);
            System.out.println("Sign up success!");
            System.out.println("Your username is: " + userName);
        } catch (InvalidInputException ex) {
            System.out.println(ex.getMessage());
            userMainMenu(sc);
        }
        viewAccountOrQuit(sc, user);
    }

    private void ChooseUserMenu(Scanner sc) {
        System.out.println("Total exsisted user account: " + userService.countUsers());
        if (userService.countUsers() != 0) {
            userService.getAllUsers().forEach(System.out::println);
        }
        String prompt = """
                        You are in selecting account process.
                        Please type user id that shown above or press[q] to return to user main menu: 
                        """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            User selectedUser = userService.getUserById(input);
            if (selectedUser != null) {
                viewAccountOrQuit(sc, selectedUser);
                break;
            } else if (input.equalsIgnoreCase("q")) {
                userMainMenu(sc);
                break;
            } else {
                System.out.println("Can not find this account, please try again.");
                System.out.print(prompt);
            }
        }
    }
    

    protected void viewAccountOrQuit(Scanner sc, User user) { //can be merge with signUpProcess()
        String prompt = "Please press [v] to view your account or [q] to return to the user menu: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("v")) {
                uiViewUser(sc, user);
                break;
            } else if (input.equalsIgnoreCase("q")) {
                userMainMenu(sc);
                break;
            } else {
                System.out.println("Invalid input, please try again");
                System.out.println(prompt);
            }
        }
    }

    protected void uiViewUser(Scanner sc, User user) {
        System.out.println("Yoy are in view user page");
        System.out.println("User id: " + user.getId());
        System.out.println("Username:" + user.getName());
        int totalPlaylist = 0;
        try {
            totalPlaylist = (int) userService.countAllPlaylistById(user.getId());
        } catch (UserNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Your total playlist: " + totalPlaylist);
        if (totalPlaylist != 0) {
            try {
                userService.listAllPlaylistByUser(user.getId()).forEach(System.out::println);
            } catch (UserNotFoundException ex) {
                System.out.println(ex.getMessage());
                userMainMenu(sc);
            }
        }
        uiPlaylistMenu(sc, user, totalPlaylist);
    }

    protected void uiPlaylistMenu(Scanner sc, User user, int totalPlaylist) {
        String prompt = "Please select:\n1. Create playlist\n2. Delete playlist\n3. Select playlist\n4. Return to user menu\nPress [1|2|3|4]: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3|4]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        createPlaylist(sc, user);
                    }
                    case 2 -> {
                        deletePlaylist(sc, user);
                    }
                    case 3 -> {
                        handleSelectedPlaylist(sc, user);
                    }
                    case 4 -> {
                        userMainMenu(sc);
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    private void createPlaylist(Scanner sc, User user) {
        Playlist playlist;
        String prompt = """
                        You are in creating playlist process.
                        Please type your playlist name or press [q] to return to view user menu: 
                        """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("q")) {
                uiViewUser(sc, user);
            } else {
                try {
                    playlist = userService.createPlaylist(user.getId(), input);
                    System.out.println("Create playlist success!");
                    System.out.println("This is your new playlist: " + playlist.getPlaylistName());
                    viewPlaylistOrQuit(sc, playlist, user);
                } catch (UserNotFoundException | InvalidInputException ex) {
                    System.out.println(ex.getMessage());
                    uiViewUser(sc, user);
                }
            }
            break;
        }
    }

    protected void viewPlaylistOrQuit(Scanner sc, Playlist playlist, User user) { //can be merge with createPlaylist()
        String prompt = "Please press [v] to view your playlist or [q] to return to view user menu: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("v")) {
                uiViewPlaylist(sc, playlist, user);
                break;
            } else if (input.equalsIgnoreCase("q")) {
                uiViewUser(sc, user);
                break;
            } else {
                System.out.println("Invalid input, please try again");
                System.out.println(prompt);
            }
        }
    }

    private void uiViewPlaylist(Scanner sc, Playlist playlist, User user) {
        System.out.println("Playlist id: " + playlist.getPlaylistId());
        System.out.println("Playlist name: " + playlist.getPlaylistName());
        System.out.println("Total song: " + playlist.getCount());
        if (playlist.getCount() != 0) {
            playlist.getAllSongWithIndex();
        }
        String prompt = """
                        Please select:
                        1. add song
                        2. delete song
                        3. return to view user menu
                        Press [1|2|3]:""";
        System.out.println(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        addSongMenu(sc, user, playlist);
                    }
                    case 2 -> {
                        deleteSong(sc, playlist, user);
                    }
                    case 3 -> {
                        uiViewUser(sc, user);
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    private void deletePlaylist(Scanner sc, User user) {
        Playlist selectedPlaylist = null;
        try {
            selectedPlaylist = selectPlaylist(sc, user);
        } catch (PlaylistNotFoundException | UserNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        if (selectedPlaylist != null) {
            try {
                userService.deletePlaylist(user.getId(), selectedPlaylist);
                System.out.println("Delete playlist success!");
                uiViewUser(sc, user);
            } catch (UserNotFoundException | InvalidInputException | PlaylistNotFoundException ex) {
                System.out.println(ex.getMessage());
                userMainMenu(sc);
            }
        } else {
            uiViewUser(sc, user);
        }
    }

    private Playlist selectPlaylist(Scanner sc, User user) throws PlaylistNotFoundException, UserNotFoundException {
        Stream playlists = null;
        try {
            playlists = userService.listAllPlaylistByUser(user.getId());
        } catch (UserNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        if (userService.countAllPlaylistById(user.getId()) != 0) {
            playlists.forEach(System.out::println);
            String prompt = """
                            You are in selecting playlist process. 
                            Please type your playlist id to perform next action,
                            or press[q] to return to view user menu:""";
            System.out.print(prompt);
            while (true) {
                String input = getInput(sc, prompt);
                Playlist selectedPlaylist = userService.getPlaylistById(input);
                if (selectedPlaylist != null) {
                    return selectedPlaylist;
                } else if (input.equalsIgnoreCase("q")) {
                    return null;
                } else {
                    System.out.println("Can not find this playlist, please try again.");
                    System.out.println(prompt);
                }
            }
        }
        System.out.println("Can not find your playlist, please try again.");
        return null;
    }

    private void handleSelectedPlaylist(Scanner sc, User user) {
        Playlist selectedPlaylist = null;
        try {
            selectedPlaylist = selectPlaylist(sc, user);
        } catch (PlaylistNotFoundException | UserNotFoundException ex) {
            System.out.println(ex.getMessage());
            userMainMenu(sc);
        }
        if (selectedPlaylist != null) {
            viewPlaylistOrQuit(sc, selectedPlaylist, user);
        }else{
            uiViewUser(sc, user);
        }
    }

    private void addSongMenu(Scanner sc, User user, Playlist playlist) {
        String prompt = """
                        You are in adding song to your playlist process.
                        Please select:
                        1. list all song in our program
                        2. search song by title
                        3. search song by artist
                        4. return to view playlist menu
                        Press [1|2|3|4]:""";
        System.out.println(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3|4]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        songService.getSongs().forEach(System.out::println);
                        addSongProcess(sc, user, playlist);
                    }
                    case 2 -> {
                        searchSongByTitle(sc, user, playlist);
                    }
                    case 3 -> {
                        searchSongByArtist(sc, user, playlist);
                    }
                    case 4 -> {
                        uiViewPlaylist(sc, playlist, user);
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    private void searchSongByTitle(Scanner sc, User user, Playlist playlist) {
        String prompt = "Please enter song title or [q] to get back to add song process: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("q")) {
                addSongMenu(sc, user, playlist);
                break;
            } else {
                long totalSongFound = songService.searchSongByTitle(input).count();
                if (totalSongFound != 0) {
                    songService.searchSongByTitle(input).forEach(System.out::println);
                    addSongProcess(sc, user, playlist);
                    break;
                }
                System.out.println("Can not find this song, pls try again");
                System.out.print(prompt);
            }
        }
    }

    private void searchSongByArtist(Scanner sc, User user, Playlist playlist) {
        String prompt = "Please enter artist name or [q] to get back to add song process: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("q")) {
                addSongMenu(sc, user, playlist);
                break;
            } else {
                long totalSongFound = songService.searchSongByArtistName(input).count();
                if (totalSongFound != 0) {
                    songService.searchSongByArtistName(input).forEach(System.out::println);
                    addSongProcess(sc, user, playlist);
                    break;
                }
                System.out.println("Can not find songs by this artist, pls try again");
                System.out.print(prompt);
            }
        }
    }

    private void addSongProcess(Scanner sc, User user, Playlist playlist) {
        String prompt = "Please enter id of song that you want to add or [q] to get back to add song menu: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Song selectedSong = songService.getSongById(input);
            if (selectedSong != null) {
                if (userService.isExistedSong(playlist, selectedSong).isPresent()) {
                    System.out.println("This song is already in your playlist.");
                    addSongMenu(sc, user, playlist);
                    break;
                }
                try {
                    userService.addSong(selectedSong, playlist.getPlaylistId());
                    System.out.println("Add song success!");
                    playlist = userService.getPlaylistById(playlist.getPlaylistId());
                    uiViewPlaylist(sc, playlist, user);
                    break;
                } catch (PlaylistNotFoundException | SongNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (input.equalsIgnoreCase("q")) {
                addSongMenu(sc, user, playlist);
                break;
            } else {
                System.out.println("Can not fing this song, please try again.");
                System.out.println(prompt);
            }
        }
    }

    private void deleteSong(Scanner sc, Playlist playlist, User user) {
        int index;
        System.out.println("Total song in this playlist: " + playlist.getCount());
        if (playlist.getCount() != 0) {
            playlist.getAllSongWithIndex();
        }
        String prompt = "Please enter number of song that you want to delete or [q] to get back to view playlist menu: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNextInt()) {
                try {
                    index = ans.nextInt() - 1;
                    userService.removeSong(index, playlist.getPlaylistId());
                    System.out.println("Remove song success!");
                    playlist = userService.getPlaylistById(playlist.getPlaylistId());
                    uiViewPlaylist(sc, playlist, user);
                    break;
                } catch (SongNotFoundException | PlaylistNotFoundException | IndexOutOfBoundsException ex) {
                    System.out.println(ex.getMessage());
                    System.out.print(prompt);
                }
            } else if (ans.hasNext("[q|Q]")) {
                uiViewPlaylist(sc, playlist, user);
                break;
            } else {
                System.out.println("Invalid input, please try again");
                System.out.print(prompt);
            }
        }
    }
}
