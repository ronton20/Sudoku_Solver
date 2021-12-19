
import javax.swing.JFrame;

public class SudokuSolver extends JFrame{

    public static void main(String[] args) {
        new SudokuSolver();
    }
    final int WIDTH = 800;
    final int HEIGHT = 800;

    public SudokuSolver() {
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(true);
        SudokuPanel panel = new SudokuPanel();
        add(panel);
        setVisible(true);
    }
    
}
