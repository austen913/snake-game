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
        private List<Point> snake;
        private Point food;
        private int direction; // 0=right, 1=down, 2=left, 3=up
        private int nextDirection;
        private Timer timer;
        private int score;
        private boolean gameOver;
        private Random random;

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
            timer = new Timer(150, e -> {
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
            
            spawnFood();
            if (timer != null && !timer.isRunning()) {
                timer.start();
            }
        }

        private void spawnFood() {
            List<Point> emptyCells = new ArrayList<>();
            for (int x = 0; x < GRID_SIZE; x++) {
                for (int y = 0; y < GRID_SIZE; y++) {
                    Point p = new Point(x, y);
                    if (!snake.contains(p)) {
                        emptyCells.add(p);
                    }
                }
            }
            if (!emptyCells.isEmpty()) {
                food = emptyCells.get(random.nextInt(emptyCells.size()));
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
                timer.stop();
                repaint();
                return;
            }

            // Check self collision
            if (snake.contains(newHead)) {
                gameOver = true;
                timer.stop();
                repaint();
                return;
            }

            snake.add(0, newHead);

            // Check food collision
            if (newHead.equals(food)) {
                score++;
                spawnFood();
            } else {
                snake.remove(snake.size() - 1);
            }
        }

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
            if (food != null) {
                g.setColor(Color.RED);
                g.fillRect(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
            
            // Draw snake
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
            
            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 25);
            
            // Draw game over
            if (gameOver) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                FontMetrics fm = g.getFontMetrics();
                String gameOverText = "Game Over";
                String scoreText = "Final Score: " + score;
                String restartText = "Press R to Restart";
                int x1 = (GRID_SIZE * CELL_SIZE - fm.stringWidth(gameOverText)) / 2;
                int x2 = (GRID_SIZE * CELL_SIZE - fm.stringWidth(scoreText)) / 2;
                int x3 = (GRID_SIZE * CELL_SIZE - fm.stringWidth(restartText)) / 2;
                g.drawString(gameOverText, x1, GRID_SIZE * CELL_SIZE / 2 - 20);
                
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString(scoreText, x2, GRID_SIZE * CELL_SIZE / 2 + 20);
                g.drawString(restartText, x3, GRID_SIZE * CELL_SIZE / 2 + 50);
            }
        }
    }
}