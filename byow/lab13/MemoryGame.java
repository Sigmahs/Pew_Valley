package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.*;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        //@geeksforgeeks
        rand = new Random();
        rand.setSeed(seed);
        System.out.println("nextint is " + rand.nextInt());
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        //inspiration from @Geeksforgeeks
        StringBuilder s = new StringBuilder();
        int i = 0;
        while (i < n) {
            s.append(CHARACTERS[rand.nextInt(26)]);
            i++;
        }
        return s.toString();
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(20, 20, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        //@https://www.javatpoint.com/java-char-to-string
        int tmp = letters.length();
        int i = 0;
        char[] s = letters.toCharArray();
        while (i < tmp){
            StdDraw.clear();
            StdDraw.pause(500);
            StdDraw.text(0, 0, String.valueOf(s[i]));
            StdDraw.pause(1000);
            StdDraw.clear();
            StdDraw.pause(500);
            i++;
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        StringBuilder s = new StringBuilder();
        int i = 0;
        while (i < n) {
            if (StdDraw.hasNextKeyTyped()) {
                s.append(StdDraw.nextKeyTyped());
                StdDraw.text(0, 0, s.toString());
                i++;
            }
        }
        return s.toString();
    }


    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        gameOver = false;
        //TODO: Establish Engine loop
        while (gameOver == false){
            drawFrame("Round: " + round);
            System.out.println("hi");
            StdDraw.pause(2000);
            StdDraw.clear();
            StdDraw.pause(500);
            String s = generateRandomString(round);
            flashSequence(s);
            String input = solicitNCharsInput(round);
            gameOver = !input.equals(s);
            round++;
        }
        round--;
        StdDraw.text(0,0, "Game Over! You made it to round: " + round);
    }

}
