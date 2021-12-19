import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;

public class SudokuPanel extends JPanel{
    
    private final int SUDOKU_SIZE = 16;      //determines how big the sudoku will be, default: 9x9

    private JLabel[][] outputTable;

    private JTextField[][] inputTable;
    private JButton btnSolve;
    private JButton btnRestart;
    private JLabel lblSolving;

    private int [][] board;

    private int cellSize;
    private int tableSize;
    private int x, y;

    Font normalFont = new Font("Helvetica", Font.PLAIN, 20);
    Font lblFont = new Font("Helvetica", Font.PLAIN, 15);

    private boolean solving;
    private boolean btnExists;
    private boolean btnRestartExists;
    private boolean lblExists;

    private Timer timer;
    private ActionListener action;
    private int timerCounter;

    //initializing the panel
    public SudokuPanel() {
        inputTable = new JTextField[SUDOKU_SIZE][SUDOKU_SIZE];
        solving = false;
        btnExists = false;
        btnRestartExists = false;
        lblExists = false;

        //-----> "Solving..." label <-----
        lblSolving = new JLabel("Solving...");
        lblSolving.setBackground(Color.black);
        lblSolving.setForeground(Color.white);
        lblSolving.setHorizontalAlignment(JLabel.CENTER);
        lblSolving.setFont(lblFont);

        //-----> "Solve" button <-----
        btnSolve = new JButton("Solve");
        btnSolve.setBackground(Color.black);
        btnSolve.setForeground(Color.white);
        btnSolve.setOpaque(true);
        btnSolve.setBorderPainted(true);
        btnSolve.setBorder(BorderFactory.createLineBorder(Color.white, 3));
        btnSolve.setFont(normalFont);
        //Start solving when button is pressed
        btnSolve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generate();
            }
        });
        btnSolve.addMouseListener(new MouseInputAdapter(){
            public void mouseEntered(MouseEvent e){
                btnSolve.setBackground(Color.GRAY);
                btnSolve.setForeground(Color.black);
                repaint();
            }

            public void mouseExited(MouseEvent e){
                btnSolve.setBackground(Color.black);
                btnSolve.setForeground(Color.white);
                repaint();
            }
            
        });

        btnRestart = new JButton("Another One");
        btnRestart.setBackground(Color.black);
        btnRestart.setForeground(Color.white);
        btnRestart.setOpaque(true);
        btnRestart.setBorderPainted(true);
        btnRestart.setBorder(BorderFactory.createLineBorder(Color.white, 3));
        btnRestart.setFont(normalFont);
        //Start solving when button is pressed
        btnRestart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restart();
            }
        });
        btnRestart.addMouseListener(new MouseInputAdapter(){
            public void mouseEntered(MouseEvent e){
                btnRestart.setBackground(Color.GRAY);
                btnRestart.setForeground(Color.black);
                repaint();
            }

            public void mouseExited(MouseEvent e){
                btnRestart.setBackground(Color.black);
                btnRestart.setForeground(Color.white);
                repaint();
            }
            
        });

        outputTable = new JLabel[SUDOKU_SIZE][SUDOKU_SIZE];

        //creating the labels for outputting sudokus
        for(int i = 0; i < SUDOKU_SIZE; i++) {
            for(int j = 0; j < SUDOKU_SIZE; j++) {
                outputTable[i][j] = new JLabel();
                outputTable[i][j].setHorizontalAlignment(JTextField.CENTER);
                outputTable[i][j].setFont(normalFont);
                outputTable[i][j].setVisible(false);
                this.add(outputTable[i][j]);
            }
        }

        //creating the text fields for inputting sudokus
        for(int i = 0; i < SUDOKU_SIZE; i++) {
            for(int j = 0; j < SUDOKU_SIZE; j++) {
                inputTable[i][j] = new JTextField();
                inputTable[i][j].setHorizontalAlignment(JTextField.CENTER);
                inputTable[i][j].setFont(normalFont);
                this.add(inputTable[i][j]);
            }
        }

        action = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(timerCounter == 10)
                    timer.stop();
                timerCounter++;    
            }
        };

        timer = new Timer(10, action);
    }

    //method for getting all the input from the user and creating a sudoku out of it
    private void generate() {
        board = new int[SUDOKU_SIZE][SUDOKU_SIZE];

        String str;
        int temp;

        int row = 0;
        int col = 0;
        for(JTextField[] txtArr : inputTable) {
            for(JTextField txt : txtArr) {
                str = txt.getText();
                if(str.isEmpty() || str == "") {
                    board[row][col] = 0;
                    col++;
                }
                else {
                    temp = Integer.parseInt(str);
                    if(temp >= 1 && temp <= SUDOKU_SIZE) {
                        board[row][col] = temp;
                        outputTable[row][col].setText("" + temp);
                    }
                    else board[row][col] = 0;
                    col++;
                }
            }
            row++;
            col = 0;
        }

        for(int i = 0; i < SUDOKU_SIZE; i++){
            for(int j = 0; j < SUDOKU_SIZE; j++) {
                inputTable[i][j].setVisible(false);
            }
        }
        solving = true;
        solve();
    }

    //method for initializing the solving algorithm
    private void solve() {
        solving = true;
        this.remove(btnSolve);
        if(!isBoardValid()) {
            lblSolving.setForeground(Color.RED);
            lblSolving.setText("Invalid Sudoku");
            repaint();
            return;
        }
        
        if(theSolver()) {
            lblSolving.setForeground(Color.GREEN);
            lblSolving.setText("Solved!");
        }
        else {
            lblSolving.setForeground(Color.RED);
            lblSolving.setText("Couldn't solve");
        }
        repaint();
    }
    
    //the method that solves the sudoku
    private boolean theSolver() {
        for(int row = 0; row < SUDOKU_SIZE; row++) {
            for(int col = 0; col < SUDOKU_SIZE; col++) {
                if(board[row][col] == 0) {
                    for(int numToCheck = 1; numToCheck <= SUDOKU_SIZE; numToCheck++) {
                        if(isPlacementValid(numToCheck, row, col)) {
                            board[row][col] = numToCheck;
                            outputTable[row][col].setForeground(Color.BLUE);
                            outputTable[row][col].setText("" + numToCheck);

                            if(theSolver()) return true;
                            else {
                                board[row][col] = 0;
                                outputTable[row][col].setText("");
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
    
    //checks if a number is in the specified row
    private boolean isNumInRow(int num, int row) {
        for(int i = 0; i < SUDOKU_SIZE; i++) {
            if(board[row][i] == num) return true;
        }
        return false;
    }
    
    //checks if a number is in the specified column
    private boolean isNumInCol(int num, int col) {
        for(int i = 0; i < SUDOKU_SIZE; i++) {
            if(board[i][col] == num) return true;
        }
        return false;
    }
    
    //checks if a number is in the specified box
    private boolean isNumInBox(int num, int row, int col) {
        int boxRow = row - (row % (int)Math.sqrt(SUDOKU_SIZE));
        int boxCol = col - (col % (int)Math.sqrt(SUDOKU_SIZE));

        for(int i = boxRow; i < boxRow + (int)Math.sqrt(SUDOKU_SIZE); i++) {
            for(int j = boxCol; j < boxCol + (int)Math.sqrt(SUDOKU_SIZE); j++) {
                if(board[i][j] == num) return true;
            }
        }
        return false;
    }
    
    //checks if a placement is valid
    private boolean isPlacementValid(int num, int row, int col) {
        return !isNumInRow(num, row) && !isNumInCol(num, col) && !isNumInBox(num, row, col);
    }

    //checks if the current board is valid
    private boolean isBoardValid() {
        for(int row = 0; row < SUDOKU_SIZE; row++) {
            for(int col = 0; col < SUDOKU_SIZE; col++) {
                if(board[row][col] != 0) {
                    if(!isAloneInRow(board[row][col], row, col) || !isAloneInCol(board[row][col], row, col) || !isAloneInBox(board[row][col], row, col))
                        return false;
                }
            }
        }
        return true;
    }

    //checks if a number is alone in a given row
    private boolean isAloneInRow(int num, int row, int col) {
        for(int i = 0; i < SUDOKU_SIZE; i++) {
            if(i != col)
                if(board[row][i] == num) return false;
        }
        return true;
    }

    //checks if a number is alone in a given column
    private boolean isAloneInCol(int num, int row, int col) {
        for(int i = 0; i < SUDOKU_SIZE; i++) {
            if(i != row)
                if(board[i][col] == num) return false;
        }
        return true;
    }

    //checks if a number is alone in the given box
    private boolean isAloneInBox(int num, int row, int col) {
        int boxRow = row - (row % (int)Math.sqrt(SUDOKU_SIZE));
        int boxCol = col - (col % (int)Math.sqrt(SUDOKU_SIZE));

        for(int i = boxRow; i < boxRow + (int)Math.sqrt(SUDOKU_SIZE); i++) {
            for(int j = boxCol; j < boxCol + (int)Math.sqrt(SUDOKU_SIZE); j++) {
                if(!(i == row && j == col))
                    if(board[i][j] == num) return false;
            }
        }
        return true;
    }

    //restarting the program to enter a new sudoku
    private void restart() {
        solving = false;
        btnExists = false;
        btnRestartExists = false;
        lblExists = false;
        //btnRestart.setVisible(false);
        remove(btnRestart);
        remove(lblSolving);

        //creating the text fields for inputting sudokus
        for(int i = 0; i < SUDOKU_SIZE; i++) {
            for(int j = 0; j < SUDOKU_SIZE; j++) {
                inputTable[i][j] = new JTextField();
                inputTable[i][j].setHorizontalAlignment(JTextField.CENTER);
                inputTable[i][j].setFont(normalFont);
                this.add(inputTable[i][j]);
            }
        }

        //restting the output labels and the board
        for(int row = 0; row < SUDOKU_SIZE; row++) {
            for(int col = 0; col < SUDOKU_SIZE; col++) {
                board[row][col] = 0;
                outputTable[row][col].setText("");
            }
        }
        repaint();
    }

    //method for drawing the input table with the text fields
    private void drawInputTable(Graphics g) {
        tableSize = Math.min(getWidth(), getHeight()) / 8 * 6;
        cellSize = tableSize / SUDOKU_SIZE;
        x = getWidth() / 2 - tableSize / 2;
        y = getHeight() / 2 - tableSize / 2;

        g.setColor(Color.WHITE);
        g.fillRect(x, y, tableSize, tableSize);

        //drawing the numbers
        for(int i = 0; i < SUDOKU_SIZE; i ++) {
            for(int j = 0; j < SUDOKU_SIZE; j++) {
                inputTable[i][j].setBounds(x + (j * cellSize), y + (i * cellSize), cellSize, cellSize);
            }
        }
        //drawing the grid
        for(int i = 0; i <= SUDOKU_SIZE; i ++) {
            g.setColor(Color.BLACK);
            if(i % (int)Math.sqrt(SUDOKU_SIZE) == 0) {
                g.fillRect(x, y + (i * cellSize) - 1, tableSize, 3);
                g.fillRect(x + (i * cellSize) - 1, y, 3, tableSize);
            }
            else {
                g.drawLine(x, y + (i * cellSize), x + tableSize, y + (i * cellSize));
                g.drawLine(x + (i * cellSize), y, x + (i * cellSize), y + tableSize);
            }
        }

        int btnSizeX = tableSize / (int)Math.sqrt(SUDOKU_SIZE);
        int btnSizeY = btnSizeX / 3;
        btnSolve.setSize(btnSizeX, btnSizeY);
        btnSolve.setBounds(getWidth() / 2 - btnSizeX / 2, y + tableSize + 10, btnSizeX, btnSizeY);
        if(!btnExists) {
            add(btnSolve);
            btnExists = true;
        }
    }

    //method for drawing the current table (while and after solving)
    private void drawTable(Graphics g) {
        tableSize = Math.min(getWidth(), getHeight()) / 8 * 6;
        cellSize = tableSize / SUDOKU_SIZE;
        x = getWidth() / 2 - tableSize / 2;
        y = getHeight() / 2 - tableSize / 2;

        g.setColor(Color.WHITE);
        g.fillRect(x, y, tableSize, tableSize);

        for(int i = 0; i < SUDOKU_SIZE; i ++) {
            for(int j = 0; j < SUDOKU_SIZE; j++) {
                outputTable[i][j].setBounds(x + (j * cellSize), y + (i * cellSize), cellSize, cellSize);
            }
        }
        for(JLabel[] lblArr : outputTable) 
            for(JLabel lbl : lblArr) 
                lbl.setVisible(true);

        //drawing the grid
        for(int i = 0; i <= SUDOKU_SIZE; i ++) {
            g.setColor(Color.BLACK);
            if(i % (int)Math.sqrt(SUDOKU_SIZE) == 0) {
                g.fillRect(x, y + (i * cellSize) - 1, tableSize, 3);
                g.fillRect(x + (i * cellSize) - 1, y, 3, tableSize);
            }
            else {
                g.drawLine(x, y + (i * cellSize), x + tableSize, y + (i * cellSize));
                g.drawLine(x + (i * cellSize), y, x + (i * cellSize), y + tableSize);
            }
        }

        //printing the label of the current situation
        int lblSizeX = tableSize / (int)Math.sqrt(SUDOKU_SIZE);
        int lblSizeY = lblSizeX / 3;
        lblSolving.setSize(lblSizeX, lblSizeY);
        lblSolving.setBounds(getWidth() / 2 - lblSizeX / 2, y + tableSize + 10, lblSizeX, lblSizeY);
        if(!lblExists) {
            add(lblSolving);
            lblExists = true;
        }

        //drawing the restart button
        int btnSizeX = tableSize / (int)Math.sqrt(SUDOKU_SIZE);
        int btnSizeY = btnSizeX / 3;
        btnRestart.setSize(btnSizeX, btnSizeY);
        btnRestart.setBounds(x + tableSize - btnSizeX, y + tableSize + 10, btnSizeX, btnSizeY);
        if(!btnRestartExists) {
            add(btnRestart);
            btnRestartExists = true;
        }
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        super.setBackground(Color.DARK_GRAY);
        if(!solving)
            drawInputTable(g);
        else
            drawTable(g);
    }
}
