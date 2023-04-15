package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.*;
import java.util.Random;
import java.io.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static Random random;
    private static long seed;
    private boolean gameOver;
    private Player player;
    private static String savefile;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        StdDraw.setCanvasSize(WIDTH, HEIGHT);
        Font font = new Font("Monaco", Font.BOLD, 30);
        edu.princeton.cs.introcs.StdDraw.setFont(font);
        edu.princeton.cs.introcs.StdDraw.setXscale(0, WIDTH);
        edu.princeton.cs.introcs.StdDraw.setYscale(0, HEIGHT);
        edu.princeton.cs.introcs.StdDraw.clear(Color.BLACK);
        edu.princeton.cs.introcs.StdDraw.enableDoubleBuffering();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        Character type = Character.toUpperCase(input.charAt(0));
        if (type == 'L') {
            String restOfInput = input.substring(1);
            input = load();
            if (input == null) {
                return null;
            }
            input += restOfInput;
        } else {
            input = input.substring(1);
        }
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        input = getSeed(input);
        fillWorld(finalWorldFrame);
        int x = RandomUtils.uniform(random, WIDTH - 10) + 5;
        int y = RandomUtils.uniform(random, HEIGHT - 10) + 5;
        // roomAmount is the amount of rooms created. Doesn't mean you'll end up with that
        // many rooms because of overlap.
        int roomAmount = RandomUtils.uniform(random, 15) + 10;
        for (int j = 0; j < roomAmount; j += 1) {
            // upper left coordinate of neighboring room randomized.
            int neighborX = RandomUtils.uniform(random, WIDTH - 10) + 5;
            int neighborY = RandomUtils.uniform(random, HEIGHT - 10) + 5;
            createRoomAndHallway(finalWorldFrame, x, y, neighborX, neighborY);
            // new x and y are for creating the neighboring room.
            x = neighborX;
            y = neighborY;
        }
        // walls are created after floor has been placed.
        topBottomWall(finalWorldFrame);
        leftRightWall(finalWorldFrame);
        int startingX = 0;
        int startingY = 0;
        while (finalWorldFrame[startingX][startingY] != Tileset.FLOOR) {
            startingX = RandomUtils.uniform(random, WIDTH);
            startingY = RandomUtils.uniform(random, HEIGHT);
        }
        player = new Player(startingX, startingY);
        finalWorldFrame[startingX][startingY] = Tileset.AVATAR;
        int i = 0;
        Character tmp = ' ';
        while (i < input.length()) {
            tmp = Character.toUpperCase(input.charAt(i));
            if (tmp == 'W') {
                player.move(1, finalWorldFrame);
            }
            if (tmp == 'S') {
                player.move(-1, finalWorldFrame);
            }
            if (tmp == 'A') {
                player.move(-2, finalWorldFrame);
            }
            if (tmp == 'D') {
                player.move(2, finalWorldFrame);
            }
            if (tmp == ':') {
                if (i + 1 < input.length() && Character.toUpperCase(input.charAt(i + 1)) == 'Q') {
                    quit();
                }
            }
            savefile += tmp;
            i += 1;
        }
        // soliciting player input
        return finalWorldFrame;
    }
        /*startingX = 0;
        startingY = 0;
        while (finalWorldFrame[startingX][startingY] != Tileset.NOTHING) {
            startingX++;
            if (finalWorldFrame[startingX][startingY] != Tileset.NOTHING) {
                startingY++;
            }
        }
        player player = new player(startingX, startingY);
        finalWorldFrame[startingX][startingY] = Tileset.AVATAR;
        gameOver = false;
        StringBuilder s = new StringBuilder();
        while (gameOver == false) {
            THIS IS WHERE IT ERRORS ITS LIKE AN INFINITE LOOP OR SOMETHING
            if (StdDraw.hasNextKeyTyped()) {
                char tmp = StdDraw.nextKeyTyped();
                s.append(tmp);
                if (tmp == 'w') {
                    player.move(1, finalWorldFrame);
                }
                if (tmp == 's') {
                    player.move(-1, finalWorldFrame);
                }
                if (tmp == 'a') {
                    player.move(-2, finalWorldFrame);
                }
                if (tmp == 'd') {
                    player.move(2, finalWorldFrame);
                }
                if (tmp == ':') {
                    tmp = StdDraw.nextKeyTyped();
                    s.append(tmp)
                    if (tmp == 'Q') {
                        savefile = s.toString();
                        quit();
                    }
                    if (tmp == 'L') {
                        load();
                    }
                if (!player.isAlive) {
                    gameOver = true;
                }
            }
        }
        savefile = s.toString();
        return finalWorldFrame;
    }*/
    /**
     * Parses through the string input to get the seed, and initializes the variable.
     * @param input
     */
    private static String getSeed(String input) {
        int i = 0;
        // stores seed
        String randomSeed = "";
        while (i < input.length()) {
            // once character is S stop adding numbers to randomSeed.
            if (Character.toUpperCase(input.charAt(i)) == 'S') {
                input = input.substring(i + 1);
                i = input.length();
            } else {
                randomSeed += input.charAt(i);
                i += 1;
            }
        }
        seed = Long.parseLong(randomSeed);
        savefile = randomSeed + 'S';
        return input;
    }
    /**
     * Creates a room with a maximum height and width of 6,
     * and connects it to the next room to be built.
     * @param tiles represents the world.
     * @param xCoord the upper left x coordinate of the room.
     * @param yCoord the upper left y coordinate of the room.
     * @param neighborX the upper left x coordinate of the new room.
     * @param neighborY the upper left y coordinate of the new room.
     */
    private static void createRoomAndHallway(TETile[][] tiles, int xCoord,
                                             int yCoord, int neighborX, int neighborY) {
        // randomizes width and height of the new room.
        int roomWidth = RandomUtils.uniform(random, 6) + 1;
        int roomHeight = RandomUtils.uniform(random, 6) + 1;
        // checks to see if width and height will go past HEIGHT and WIDTH
        if (roomWidth + xCoord > WIDTH - 2) {
            roomWidth = roomWidth - (xCoord + roomWidth - WIDTH + 3);
        }
        if (roomHeight + yCoord > HEIGHT - 2) {
            roomHeight = roomHeight - (yCoord + roomHeight - HEIGHT + 3);
        }
        // fills in floor tiles for the new room.
        for (int x = xCoord; x <= xCoord + roomWidth; x += 1) {
            for (int y = yCoord; y <= yCoord + roomHeight; y += 1) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
        // gets the midpoint of the room
        int midRoomX = xCoord + roomWidth / 2;
        int midRoomY = yCoord + roomHeight / 2;
        //connects the neighboring room to this room.
        connect(tiles, midRoomX, midRoomY, neighborX, neighborY);
    }
    /**
     * Connects neighboring room with current room by using the middle tile.
     * @param tiles represents the world.
     * @param midX the horizontal midpoint of the room.
     * @param midY the vertical midpoint of the room.
     * @param nX the upper left x coordinate of the new room.
     * @param nY the upper left y coordinate of the new room.
     */
    private static void connect(TETile[][] tiles, int midX, int midY, int nX, int nY) {
        int largeX = Math.max(midX, nX);
        int smallX = Math.min(midX, nX);
        int largeY = Math.max(midY, nY);
        int smallY = Math.min(midY, nY);
        for (int x = smallX; x <= largeX; x++) {
            tiles[x][midY] = Tileset.FLOOR;
        }
        for (int y = smallY; y <= largeY; y++) {
            tiles[nX][y] = Tileset.FLOOR;
        }
    }
    /**
     * Fills the given 2D array of tiles with NOTHING tiles and initializes Random.
     * @param tiles
     */
    private static void fillWorld(TETile[][] tiles) {
        random = new Random(seed);
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }
    /**
     * Creates the top and bottom walls.
     * @param tiles
     */
    private static void topBottomWall(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT - 1; y += 1) {
                if (tiles[x][y] == Tileset.NOTHING && tiles[x][y + 1] == Tileset.FLOOR) {
                    tiles[x][y] = Tileset.WALL;
                } else if (tiles[x][y] == Tileset.FLOOR && tiles[x][y + 1] == Tileset.NOTHING) {
                    tiles[x][y + 1] = Tileset.WALL;
                }
            }
        }
    }
    /**
     * Creates the left and right walls. Does not add corners (but that's ok according to the spec)
     * @param tiles
     */
    private static void leftRightWall(TETile[][] tiles) {
        for (int y = 0; y < HEIGHT; y += 1) {
            for (int x = 0; x < WIDTH - 1; x += 1) {
                if (tiles[x][y] == Tileset.NOTHING && (tiles[x + 1][y] == Tileset.FLOOR
                        || tiles[x + 1][y] == Tileset.WALL)) {
                    tiles[x][y] = Tileset.WALL;
                } else if (tiles[x][y] == Tileset.FLOOR && tiles[x + 1][y] == Tileset.NOTHING) {
                    tiles[x + 1][y] = Tileset.WALL;
                }
            }
        }
    }

    private static void quit() { //edit savefile.txt with string, or create new
        //referenced stack overflow regarding java.io methods and
        //common implementations
        File file = new File("savefile.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(savefile);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String load() {
        File file = new File("savefile.txt");
        if (!file.exists()) {
            return null;
        }
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class Player {
        private int posX;
        private int posY;
        private boolean isAlive;
        public Player(int x, int y) {
            posX = x;
            posY = y;
            isAlive = true;
        }
        private void move(int direction, TETile[][] tileset) {
            tileset[posX][posY] = Tileset.FLOOR;
            if (direction == 1) {
                posY++;
                if (tileset[posX][posY] == Tileset.WALL) {
                    posY--;
                }
            }
            if (direction == -1) {
                posY--;
                if (tileset[posX][posY] == Tileset.WALL) {
                    posY++;
                }
            }
            if (direction == 2) {
                posX++;
                if (tileset[posX][posY] == Tileset.WALL) {
                    posX--;
                }
            }
            if (direction == -2) {
                posX--;
                if (tileset[posX][posY] == Tileset.WALL) {
                    posX++;
                }
            }
            tileset[posX][posY] = Tileset.AVATAR;

        }
    }
    /**
     * Main method for testing.
     */
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Engine engine = new Engine();
        TETile[][] world = engine.interactWithInputString("LADADADAD:Q");
        ter.renderFrame(world);
    }
}
