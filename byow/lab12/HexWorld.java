package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public static class Position {
        private int x;
        private int y;
        public Position(int x_coord, int y_coord) {
            x = x_coord;
            y = y_coord;
        }
    }
    public static void addHexagon(TETile[][] tiles, int s, TETile t, Position p) {
        int length = s;
        int startingX = p.x;
        int level = p.y;
        addTop(tiles, s, length, startingX, level, t);
        addBottom(tiles, s, length + 2 * (length - 1), startingX - length + 1, level - s, t);
    }
    private static void addTop(TETile[][] tiles, int s, int length, int startingX, int level, TETile t) {
        int movingX = startingX;
        for (int i = 0; i < s; i++) {
            movingX = startingX;
            for (int l = 0; l < length; l++) {
                tiles[movingX][level] = t;
                movingX += 1;
            }
            level -= 1;
            startingX -= 1;
            length += 2;
        }
    }
    private static void addBottom(TETile[][] tiles, int s, int length, int startingX, int level, TETile t) {
        int movingX = startingX;
        for (int i = 0; i < s; i++) {
            movingX = startingX;
            for (int l = 0; l < length; l++) {
                tiles[movingX][level] = t;
                movingX += 1;
            }
            level -= 1;
            startingX += 1;
            length -= 2;
        }
    }
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        tessalate(world, 20, 30, 3);
        ter.renderFrame(world);
    }
    public static void tessalate(TETile[][] tiles, int x, int y, int hexSize) {
        HexWorld.addHexagon(tiles, hexSize, randomTile(), new HexWorld.Position(x, y));
        HexWorld.addHexagon(tiles, hexSize, randomTile(), new HexWorld.Position(x, y - hexSize * 8));
        middle(tiles, x, y, hexSize);
    }
    private static void middle(TETile[][] tiles, int x, int y, int hexSize) {
        int topX = x - 2 * (2 * hexSize - 1);
        int topY = y - 2 * hexSize;
        int currentX = topX;
        int currentY = topY;
        for (int i = 0; i < 3; i++) {
            currentY = topY;
            for (int j = 0; j < 3; j++) {
                HexWorld.addHexagon(tiles, hexSize, randomTile(), new HexWorld.Position(currentX, currentY));
                currentY -= 2 * hexSize;
            }
            currentY = topY + hexSize;
            currentX += hexSize * 2 - 1;
            if (i != 2) {
                for (int n = 0; n < 4; n++) {
                    HexWorld.addHexagon(tiles, hexSize, randomTile(), new HexWorld.Position(currentX, currentY));
                    currentY -= 2 * hexSize;
                }
            }
            currentX += hexSize * 2 - 1;
        }
    }
    /**
     * @author hug
     * @return random tile
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(4);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.MOUNTAIN;
            case 3: return Tileset.GRASS;
            default: return Tileset.GRASS;
        }
    }

}
