import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Color;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

class Snake {
    private LinkedList<Cell> snake;
    private int direction;
    private Food food;
    private Poison poison;

    public Snake(int x, int y, int length, int direction) {
        snake = new LinkedList<>();
        for (int i = 0; i < length; i++) {
            snake.add(new Cell(x - i, y, GameSnake.CELL_SIZE, GameSnake.SNAKE_COLOR));
        }
        this.direction = direction;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public void setPoison(Poison poison) {
        this.poison = poison;
    }

    public int size() {
        return snake.size();
    }

    public void setDirection(int direction) {
        if ((direction >= GameSnake.KEY_LEFT) && (direction <= GameSnake.KEY_DOWN)) {
            if (Math.abs(this.direction - direction) != 2) {
                this.direction = direction;
            }
        }
    }

    public boolean isInSnake(int x, int y) {
        for (Cell cell : snake) {
            if ((cell.getX() == x) && (cell.getY() == y)) {
                return true;
            }
        }
        return false;
    }

    public void move() {
        int x = snake.getFirst().getX();
        int y = snake.getFirst().getY();
        switch (direction) {
            case GameSnake.KEY_LEFT: x--;
                if (x < 0)
                    x = GameSnake.CANVAS_WIDTH - 1;
                break;
            case GameSnake.KEY_RIGHT: x++;
                if (x == GameSnake.CANVAS_WIDTH)
                    x = 0;
                break;
            case GameSnake.KEY_UP: y--;
                if (y < 0)
                    y = GameSnake.CANVAS_HEIGHT - 1;
                break;
            case GameSnake.KEY_DOWN: y++;
                if (y == GameSnake.CANVAS_HEIGHT)
                    y = 0;
                break;
        }
        if (isInSnake(x, y) ||           // if the snake crosses itself
                poison.isPoison(x, y)) { // or if it eats poison
            GameSnake.gameOver = true;
            return;
        }
        snake.addFirst(new Cell(x, y, GameSnake.CELL_SIZE, GameSnake.SNAKE_COLOR)); // new head of snake
        if (food.isFood(x, y)) {
            food.eat();
        } else {
            snake.removeLast();
        }
    }

    public void paint(Graphics2D g) {
        for (Cell cell : snake) {
            cell.paint(g);
        }
    }
}
class Poison {
    private static List<Cell> poison;
    private Random random;
    private Snake snake;
    private Food food;

    public Poison(Snake snake) {
        poison = new ArrayList<>();
        random = new Random();
        this.snake = snake;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public boolean isPoison(int x, int y) {
        for (Cell cell : poison)
            if ((cell.getX() == x) && (cell.getY() == y))
                return true;
        return false;
    }

    public void add() {
        int x, y;
        do {
            x = random.nextInt(GameSnake.CANVAS_WIDTH);
            y = random.nextInt(GameSnake.CANVAS_HEIGHT);
        } while (isPoison(x, y) ||
                snake.isInSnake(x, y) ||
                food.isFood(x, y));
        poison.add(new Cell(x, y, GameSnake.CELL_SIZE, GameSnake.POISON_COLOR));
    }

    public void paint(Graphics2D g) {
        for (Cell cell : poison)
            cell.paint(g);
    }
}
public class GameSnake extends JFrame {

    final String TITLE_OF_PROGRAM = "Classic Game Snake";
    final String GAME_OVER_MSG = "GAME OVER";
    final static int CELL_SIZE = 20;           // size of cell in pix
    final static int CANVAS_WIDTH = 30;        // width in cells
    final static int CANVAS_HEIGHT = 25;       // height in cells
    final static Color SNAKE_COLOR = Color.darkGray;
    final static Color FOOD_COLOR = Color.green;
    final static Color POISON_COLOR = Color.red;
    final static int KEY_LEFT = 37;            // codes
    final static int KEY_UP = 38;              //   of
    final static int KEY_RIGHT = 39;           //   cursor
    final static int KEY_DOWN = 40;            //   keys
    final int START_SNAKE_SIZE = 5;            // initialization data
    final int START_SNAKE_X = CANVAS_WIDTH/2;  //   for
    final int START_SNAKE_Y = CANVAS_HEIGHT/2; //   snake
    final int SNAKE_DELAY = 150;               // snake delay in milliseconds
    int snakeSize = 0;                         // current snake size
    static boolean gameOver = false;           // a sign game is over or not

    Canvas canvas;                   // canvas object for rendering (drawing)
    Snake snake;                     // declare a snake object
    Food food;                       // declare a food object
    Poison poison;                   // declare a poison object

    public static void main(String[] args) {    // starting method
        new GameSnake().game();                 // create an object and call game()
    }

    public GameSnake() {
        setTitle(TITLE_OF_PROGRAM);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        canvas = new Canvas();
        canvas.setBackground(Color.white);
        canvas.setPreferredSize(new Dimension(
                CELL_SIZE * CANVAS_WIDTH - 10,
                CELL_SIZE * CANVAS_HEIGHT - 10));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                snake.setDirection(e.getKeyCode());
            }
        });
        add(canvas);                 // add a panel for rendering
        pack();                      // bringing the window to the required size
        setLocationRelativeTo(null); // the window will be in the center
        setResizable(false);         // prohibit window resizing
        setVisible(true);            // make the window visible
    }

    private void game() {            // method containing game cycle
        snake = new Snake(           // creating snake object
                START_SNAKE_X,
                START_SNAKE_Y,
                START_SNAKE_SIZE,
                KEY_RIGHT);
        food = new Food(snake);      // creating food object
        snake.setFood(food);

        poison = new Poison(snake);  // poison object
        poison.setFood(food);
        snake.setPoison(poison);
        food.setPoison(poison);

        while (!gameOver) {          // game cycle while NOT gameOver
            snake.move();            // snake move
            if (snake.size() > snakeSize) {
                snakeSize = snake.size();
                setTitle(TITLE_OF_PROGRAM + " : " + snakeSize);
            }
            if (food.isEaten()) {    // if the snake ate the food
                food.appear();       //   show food in new place
                poison.add();        //   add new poison point
            }
            canvas.repaint();        // repaint panel/window
            sleep(SNAKE_DELAY);      // to make delay in milliseconds
        }
        JOptionPane.showMessageDialog(this, GAME_OVER_MSG);
    }

    private void sleep(long ms) {    // method for suspending
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Canvas extends JPanel {    // class for rendering (drawing)
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2D = (Graphics2D) g;
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            snake.paint(g2D);
            food.paint(g2D);
            poison.paint(g2D);
        }
    }
}
class Food extends Cell {
    private Random random;
    private Snake snake;
    private Poison poison;

    public Food(Snake snake) { // constructor
        super(-1, -1, GameSnake.CELL_SIZE, GameSnake.FOOD_COLOR);
        random = new Random();
        this.snake = snake;
    }

    public void setPoison(Poison poison) {
        this.poison = poison;
    }

    public boolean isFood(int x, int y) {   // if food has x, y coordinate
        return (getX() == x) && (getY() == y);
    }

    public boolean isEaten() {              // if food is eaten or not
        return getX() == -1;
    }

    public void eat() {                     // virtual eating
        set(-1, -1);
    }

    public void appear() {                  // show food at new x,y coordinates
        int x, y;
        do {
            x = random.nextInt(GameSnake.CANVAS_WIDTH);
            y = random.nextInt(GameSnake.CANVAS_HEIGHT);
        } while (snake.isInSnake(x, y) || poison.isPoison(x, y));
        set(x, y);
    }
}
class Cell {
    private int x, y;                       // object coordinates
    private int size;                       // object size in px
    private Color color;                    // object color

    public Cell(int x, int y, int size, Color color) {
        set(x, y);                            // init coordinates
        this.size = size;                     // init size
        this.color = color;                   // init color
    }

    public void set(int x, int y) {           // set coordinates
        this.x = x;
        this.y = y;
    }

    public int getX() {                       // get the X coordinate
        return x;
    }

    public int getY() {                       // get the Y coordinate
        return y;
    }

    public void paint(Graphics2D g) {           // object rendering
        g.setColor(color);
        g.fillOval(x * size, y * size,        // upper left corner
                size, size);                  // width and height
    }
}