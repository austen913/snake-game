import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame {
    private static final int CELL_SIZE = 30;
    private static final int GRID_SIZE = 20;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    static class GamePanel extends JPanel {
        private static int highScore = 0;
        private List<Point> snake;
        private List<Point> foods;
        private List<Color> foodColors;
        private static final int MAX_FOODS = 5;
        private static final Color[] FRUIT_COLORS = {
            Color.RED, Color.BLUE, Color.YELLOW, new Color(128, 0, 128), Color.WHITE
        };
        private static final int BASE_DELAY = 150;
        private static final int FAST_DELAY = 80;
        private static final int SLOW_DELAY = 250;
        private int direction; // 0=right, 1=down, 2=left, 3=up
        private int nextDirection;
        private Timer timer;
        private int score;
        private boolean gameOver;
        private Random random;
        private int currentDelay = BASE_DELAY;

        public GamePanel() {
            setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
            setFocusable(true);
            random = new Random();
            
            initGame();

            // Add key listener for controls
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (gameOver) {
                        if (e.getKeyCode() == KeyEvent.VK_R) {
                            initGame();
                        }
                        return;
                    }
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_RIGHT:
                            if (direction != 2) nextDirection = 0;
                            break;
                        case KeyEvent.VK_DOWN:
                            if (direction != 3) nextDirection = 1;
                            break;
                        case KeyEvent.VK_LEFT:
                            if (direction != 0) nextDirection = 2;
                            break;
                        case KeyEvent.VK_UP:
                            if (direction != 1) nextDirection = 3;
                            break;
                    }
                }
            });

            // Timer ticks every 150ms
            timer = new Timer(currentDelay, e -> {
                direction = nextDirection;
                moveSnake();
                repaint();
            });
            timer.start();
        }

        private void initGame() {
            snake = new ArrayList<>();
            int startX = GRID_SIZE / 2;
            int startY = GRID_SIZE / 2;
            snake.add(new Point(startX, startY));
            snake.add(new Point(startX - 1, startY));
            snake.add(new Point(startX - 2, startY));
            
            direction = 0; // right
            nextDirection = 0;
            score = 0;
            gameOver = false;
            foods = new ArrayList<>();
            foodColors = new ArrayList<>();
            currentDelay = BASE_DELAY;
            
            spawnFood();
            if (timer != null) {
                timer.setDelay(currentDelay);
                if (!timer.isRunning()) {
                    timer.start();
                }
            }
        }

        private void spawnFood() {
            List<Point> emptyCells = new ArrayList<>();
            for (int x = 0; x < GRID_SIZE; x++) {
                for (int y = 0; y < GRID_SIZE; y++) {
                    Point p = new Point(x, y);
                    if (!snake.contains(p) && !foods.contains(p)) {
                        emptyCells.add(p);
                    }
                }
            }
            while (foods.size() < MAX_FOODS && !emptyCells.isEmpty()) {
                int idx = random.nextInt(emptyCells.size());
                Point p = emptyCells.remove(idx);
                foods.add(p);
                foodColors.add(FRUIT_COLORS[random.nextInt(FRUIT_COLORS.length)]);
            }
        }

        private void moveSnake() {
            Point head = snake.get(0);
            Point newHead = new Point(head.x, head.y);

            switch (direction) {
                case 0: newHead.x++; break;  // right
                case 1: newHead.y++; break;  // down
                case 2: newHead.x--; break;  // left
                case 3: newHead.y--; break;  // up
            }

            // Check wall collision
            if (newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE) {
                gameOver = true;
                if (score > highScore) highScore = score;
                timer.stop();
                repaint();
                return;
            }

            // Check self collision
            if (snake.contains(newHead)) {
                gameOver = true;
                if (score > highScore) highScore = score;
                timer.stop();
                repaint();
                return;
            }

            snake.add(0, newHead);

            // Check food collision
            int foodIndex = foods.indexOf(newHead);
            if (foodIndex >= 0) {
                Color eatenColor = foodColors.get(foodIndex);
                applyFruitEffect(eatenColor);
                foods.remove(foodIndex);
                foodColors.remove(foodIndex);
                spawnFood();
            } else {
                snake.remove(snake.size() - 1);
            }
        }

        private void applyFruitEffect(Color color) {
            score++;
            lastFruitColor = color; // Update the last fruit color

            if (color.equals(Color.RED)) {
                // Normal - just add score and returns to base speed
                currentDelay = BASE_DELAY;
                timer.setDelay(BASE_DELAY);
            } else if (color.equals(Color.YELLOW)) {
                // Faster
                currentDelay = FAST_DELAY;
                timer.setDelay(FAST_DELAY);
            } else if (color.equals(Color.BLUE)) {
                // Slower
                currentDelay = SLOW_DELAY;
                timer.setDelay(SLOW_DELAY);
            } else if (color.equals(new Color(128, 0, 128))) {
                // Purple loses 2 segments and if snake size is less than 1 game over
                if (snake.size() > 2) {
                    snake.remove(snake.size() - 1);
                    snake.remove(snake.size() - 1);
                } else {
                    gameOver = true;
                    if (score > highScore) highScore = score;
                    timer.stop();
                    repaint();
                }
            } else if (color.equals(Color.WHITE)) {
                // Random effect
                int effect = random.nextInt(4);
                switch (effect) {
                    case 0: // Normal
                        break;
                    case 1: // Faster
                        currentDelay = FAST_DELAY;
                        timer.setDelay(FAST_DELAY);
                        break;
                    case 2: // Slower
                        currentDelay = SLOW_DELAY;
                        timer.setDelay(SLOW_DELAY);
                        break;
                    case 3: // Lose segment
                        if (snake.size() > 2) {
                    snake.remove(snake.size() - 1);
                    snake.remove(snake.size() - 1);
                } else {
                    gameOver = true;
                    if (score > highScore) highScore = score;
                    timer.stop();
                    repaint();
                        break;
                }
                }
            }
        }
       // the snake should change color based on the last fruit eaten
            private Color lastFruitColor = Color.GREEN; // default snake color


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw dark gray background
            g.setColor(new Color(40, 44, 52));
            g.fillRect(0, 0, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
            
            // Draw grid lines
            g.setColor(new Color(60, 64, 72));
            for (int i = 0; i <= GRID_SIZE; i++) {
                g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE);
                g.drawLine(0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE, i * CELL_SIZE);
            }
            
            // Draw food
            for (int i = 0; i < foods.size(); i++) {
                g.setColor(foodColors.get(i));
                Point f = foods.get(i);
                g.fillRect(f.x * CELL_SIZE, f.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
            
            // Draw snake
            g.setColor(lastFruitColor);
            for (Point p : snake) {
                g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
            
            // Draw score and high score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 25);
            g.drawString("High Score: " + highScore, 10, 45);
            
            // Draw game over
            if (gameOver) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                FontMetrics fm = g.getFontMetrics();
                String gameOverText = "Game Over";
                String scoreText = "Final Score: " + score;
                String highScoreText = "High Score: " + highScore;
                String restartText = "Press R to Restart";
                int x1 = (GRID_SIZE * CELL_SIZE - fm.stringWidth(gameOverText)) / 2;
                int x2 = (GRID_SIZE * CELL_SIZE - fm.stringWidth(scoreText)) / 2;
                int x3 = (GRID_SIZE * CELL_SIZE - fm.stringWidth(highScoreText)) / 2;
                int x4 = (GRID_SIZE * CELL_SIZE - fm.stringWidth(restartText)) / 2;
                g.drawString(gameOverText, x1, GRID_SIZE * CELL_SIZE / 2 - 40);
                
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString(scoreText, x2, GRID_SIZE * CELL_SIZE / 2);
                g.drawString(highScoreText, x3, GRID_SIZE * CELL_SIZE / 2 + 30);
                g.drawString(restartText, x4, GRID_SIZE * CELL_SIZE / 2 + 60);
            }
        }
    }
}