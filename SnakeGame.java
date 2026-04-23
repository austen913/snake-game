import javax.swing.*;
import java.awt.*;

public class SnakeGame {
    private static final int CELL_SIZE = 30;
    private static final int GRID_SIZE = 20;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GamePanel());
        frame.setVisible(true);
    }

    static class GamePanel extends JPanel {
        public GamePanel() {
            setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
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
            
            // Draw 3-segment snake near center, facing right
            g.setColor(Color.GREEN);
            int startX = (GRID_SIZE / 2) * CELL_SIZE;
            int startY = (GRID_SIZE / 2) * CELL_SIZE;
            for (int i = 0; i < 3; i++) {
                g.fillRect(startX + i * CELL_SIZE, startY, CELL_SIZE, CELL_SIZE);
            }
        }
    }
}