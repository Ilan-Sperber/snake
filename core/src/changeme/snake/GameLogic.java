package changeme.snake;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;

public class GameLogic {
    private static final Logger logger = LogManager.getLogger(GameLogic.class);

    private static final int FOOD_GENERATION_DISTANCE = 20; // food can generate this many squares away
    private static final int ADD_FOR_FOOD = 5;
    public static final int BOARD_HEIGHT = 20;
    public static final int BOARD_WIDTH = 40;
    public static final int TILE_SIZE = 64;

    private GridSquare[][] board;

    private Direction direction;
    private Coord head;
    private Coord tail;
    private ArrayList<Coord> tailBody;
    private Coord food;

    private int length;

    private boolean isPaused;
    private boolean isAlive;

    public GameLogic(Coord starting) {
        logger.info("GameLogic Init");

        board = new GridSquare[BOARD_WIDTH + 2][BOARD_HEIGHT + 2];
        for (int x = 0; x < BOARD_WIDTH; x++)
            for (int y = 0; y < BOARD_HEIGHT; y++)
                board[x][y] = (x == 0 || x == BOARD_WIDTH - 1 || y == 0 || y == BOARD_HEIGHT - 1) ? GridSquare.WALL : GridSquare.EMPTY;

        direction = Direction.EAST;
        head = starting;
        tail = new Coord(head.x() - 1, head.y());
        board[head.x()][head.y()] = GridSquare.HEAD;
        board[tail.x()][tail.y()] = GridSquare.TAIL;
        placeFood();

        tailBody = new ArrayList<Coord>();

        isPaused = false;
        isAlive = true;

        length = 1;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public GridSquare[][] getBoard() {
        return board;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public Coord getFood() {
        return food;
    }

    public Coord getHead() {
        return head;
    }

    public Coord getTail() {
        return tail;
    }

    public ArrayList<Coord> getTailBody() {
        return tailBody;
    }

    public int getLength() {
        return length;
    }

    public Coord[] tailPieceInfo(Coord tailPiece) {
        if (tailPiece.equals(tail)) {
//            if (tailBody.size() > 0) {
//                return new Coord[] {tailBody.get(tailBody.size() - 1), null};
//            }
            for (int i = tailBody.size() - 1; i > -1; i--) {
                if (!tail.equals(tailBody.get(i)))
                    return new Coord[] {tailBody.get(i), null};
            }
            return new Coord[] {head, null};
        }

        int index = -1;
        for (int i = 0; i < tailBody.size(); i++) {
            if (tailPiece.equals(tailBody.get(i))) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            logger.fatal("Tried to get tail info on non tail tile at " + tailPiece);
        }
        Coord front = index != 0 ? tailBody.get(index - 1) : head;
        Coord back = index != tailBody.size() - 1 ? tailBody.get(index + 1) : tail;
        return new Coord[] {front, back};
    }

    private void placeFood() {
        // get a random coordinate and place food
        int x, y;
        do {
            x = new Random().nextInt(board.length);
            y = new Random().nextInt(board[x].length);
        } while (board[x][y] != GridSquare.EMPTY
                 || Math.hypot(x - head.x(), y - head.y()) <= FOOD_GENERATION_DISTANCE);

        board[x][y] = GridSquare.FOOD;
        if (food == null) food = new Coord(x, y);
        else food.setCoord(x, y);
        logger.info("Placed food at " + food);
    }

    public void move() {
//        Coord toSquare = switch (direction) {
//            case NORTH -> new Coord(head.x(), head.y() + 1);
//            case SOUTH -> new Coord(head.x(), head.y() - 1);
//            case EAST -> new Coord(head.x() + 1, head.y());
//            case WEST -> new Coord(head.x() - 1, head.y());
//        };

        Coord toSquare;
        switch (direction) {
            case NORTH: toSquare = new Coord(head.x(), head.y() + 1); break;
            case SOUTH: toSquare = new Coord(head.x(), head.y() - 1); break;
            case EAST: toSquare = new Coord(head.x() + 1, head.y()); break;
            case WEST: toSquare =  new Coord(head.x() - 1, head.y()); break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        };

        Coord tailTo = tailBody.size() > 0 ? tailBody.get(tailBody.size() - 1).copy() : head.copy();

        Coord oldTail;
        switch (toSquare.at2dGrid(board)) {
            case FOOD:
                oldTail = tail.copy();

                board[head.x()][head.y()] = GridSquare.TAIL;

//                tail.setCoord(tailBody.remove(tailBody.size() - 1));
//                tailBody.add(0, head.copy());

                if (tailBody.size() > 0) {
                    tail.setCoord(tailBody.remove(tailBody.size() - 1));
                    tailBody.add(0, head.copy());
                } else {
                    tail.setCoord(head);
                }

                head.setCoord(toSquare);

                board[head.x()][head.y()] = GridSquare.HEAD;

                if (!tail.equals(oldTail) && oldTail.at2dGrid(board) == GridSquare.TAIL)
                    board[oldTail.x()][oldTail.y()] = GridSquare.EMPTY;

                length += ADD_FOR_FOOD;
                for (int i = 0; i < ADD_FOR_FOOD; i++) {
                    tailBody.add(tail.copy());
                }

                logger.info("Food eaten at " + toSquare);
                placeFood();
                break;
            case TAIL:
            case WALL:
                isAlive = false;
                logger.info("Player died going to " + toSquare);
                logger.info("Final length was " + length);
                break;
            case EMPTY:
                oldTail = tail.copy();

                board[head.x()][head.y()] = GridSquare.TAIL;

////                tail.setCoord(tailBody.remove(tailBody.size() - 1));
//                tail.setCoord(tailBody.size() > 0 ? tailBody.remove(tailBody.size() - 1) : head);
//                tailBody.add(0, head.copy());

                if (tailBody.size() > 0) {
                    tail.setCoord(tailBody.remove(tailBody.size() - 1));
                    tailBody.add(0, head.copy());
                } else {
                    tail.setCoord(head);
                }

                head.setCoord(toSquare);

                board[head.x()][head.y()] = GridSquare.HEAD;

                if (!tail.equals(oldTail) && oldTail.at2dGrid(board) == GridSquare.TAIL)
                    board[oldTail.x()][oldTail.y()] = GridSquare.EMPTY;
        }

    }

}
