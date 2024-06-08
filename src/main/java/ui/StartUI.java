/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import java.io.Console;
import java.util.Scanner;
import repository.database.DatabaseSongRepository;
import repository.file.FileSongRepository;
import repository.memory.MemoryArtistRepository;
import repository.memory.MemorySongRepository;
import service.ArtistService;
import service.SongService;

/**
 *
 * @author User
 */
public class StartUI {

    UserUI userUi;
    ArtistUI artistUi;
    SongService songService;

    // -> add password fields
    public StartUI() {
    }

    public void start() {

        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\n");
        uiReadPassword(sc);
        int repoType = uiSelectRepotype(sc);
        switch (repoType) {
            case 1 -> {
                songService = new SongService(new MemorySongRepository());
            }
            case 2 -> {
                songService = new SongService(new FileSongRepository()); //file
            }
            case 3 -> {
                songService = new SongService(new DatabaseSongRepository()); //database
            }
            default -> {
                songService = new SongService(new MemorySongRepository());
            }
        }
        userUi = new UserUI(songService, repoType);
        artistUi = new ArtistUI(songService, repoType);
        uiMainMenu(sc);
    }

    public void uiReadPassword(Scanner sc) {
        System.out.println("Welcome! This is creating password process");
        String prompt = "Please type your password: ";
        String password;
        Console c = System.console();
        if (c != null) {
            System.out.println("You have a real console.");
            System.out.println("So, your password will not appear while typing.");
            System.out.print(prompt);
            password = new String(c.readPassword());
        } else {
            System.out.println("You do not have a real console.");
            System.out.println("So, your password will appear while typing.");
            System.out.print(prompt);
            password = sc.nextLine();
        }
        showPassword(sc, password);
    }

    private void showPassword(Scanner sc, String password) {
        String prompt = "Do you want to see what you have just typed in [y/n]: ";
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            if (input.equalsIgnoreCase("y")) {
                System.out.printf("You type: [%s]%n", password);
                break;
            } else if (input.equalsIgnoreCase("n")) {
                System.out.println("Got it! Next, please select Data managing method.");
                break;
            } else {
                System.out.println("Invalid input, please try again.");
                System.out.print(prompt);
            }
        }
    }

    protected int uiSelectRepotype(Scanner sc) {
        String prompt = """
                    Hello, welcome to our application. Please select Data managing method:
                        1. In memory
                        2. In file
                        3. In database
                    Press [1|2|3]: """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int userChoice = ans.nextInt();
                return userChoice;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    protected void uiMainMenu(Scanner sc) {
        String prompt = """
                    You are in main menu page, please select your account's type:
                        1. Normal user account
                        2. Artist account(for publish your song)
                        3. Quit
                    Press [1|2|3]: """;
        System.out.print(prompt);
        while (true) {
            String input = getInput(sc, prompt);
            Scanner ans = new Scanner(input);
            if (ans.hasNext("[1|2|3]")) {
                int i = ans.nextInt();
                switch (i) {
                    case 1 -> {
                        userUi.startUserUI();
                        System.out.print(prompt);
                        continue;
                    }
                    case 2 -> {
                        artistUi.startArtistUI();
                        System.out.print(prompt);
                        continue;
                    }
                    case 3 -> {
                        System.out.println("Have a nice day!");
                    }
                }
                break;
            } else {
                System.out.println("Invalid input");
                System.out.print(prompt);
            }
        }
    }

    public String getInput(Scanner sc, String prompt) {
        String input = "";
        while (true) {
            input = sc.nextLine().trim();
            if (input.isBlank()) {
                System.out.println("Invalid input, please try again");
                System.out.print(prompt);
            } else {
                break;
            }
        }
        return input;
    }
}
