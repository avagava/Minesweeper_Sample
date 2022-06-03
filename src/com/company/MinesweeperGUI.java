package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serial;

public class MinesweeperGUI extends JFrame {
    @Serial
    private static long serialVersionUID = 1L;

    Board board = new Board("BEGINNER");
    JFrame frame = new JFrame("Minesweeper");
    JButton newGame = new JButton("New Game");
    JButton resetButton = new JButton("Reset");
    JButton[][] boardButtons;

    JMenuItem beginnerOption = new JMenuItem("BEGINNER");
    JMenuItem intermediateOption = new JMenuItem("INTERMEDIATE");
    JMenuItem advancedOption = new JMenuItem("ADVANCED");
    boolean gameOver = false;

    String flag = Character.toString(board.flag);
    String mine = Character.toString(board.mine);

    public static final Color background = Color.GRAY;
    Color defaultBackground = new JButton().getBackground();

    public MinesweeperGUI() {
        frame.setJMenuBar(createMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(createBoard(board.getDifficulty()));
        frame.setVisible(true);
        frame.setResizable(true);
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenu submenu;

        menuBar = new JMenuBar();
        menu = new JMenu("File");

        frame.add(menu);
        menuBar.add(menu);

        submenu = new JMenu("Change Difficulty");
        submenu.add(beginnerOption);
        beginnerOption.addActionListener(new GameActionListener());

        submenu.add(intermediateOption);
        intermediateOption.addActionListener(new GameActionListener());

        submenu.add(advancedOption);
        advancedOption.addActionListener(new GameActionListener());

        menu.add(submenu);
        return menuBar;
    }

    public JPanel createBoard(String difficulty) {
        board.setDifficulty(difficulty);
        board.reset();

        gameOver = false;

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel gameBoard = new JPanel(new GridLayout(board.getHeight(), board.getWidth()));
        boardButtons = new JButton[board.getHeight()][board.getWidth()];

        frame.add(mainPanel);

        switch (difficulty) {
            case "BEGINNER":
                frame.setSize(500, 600);
                gameBoard.setPreferredSize(new Dimension(450, 450));
                break;
            case "INTERMEDIATE":
                frame.setSize(900, 1000);
                gameBoard.setPreferredSize(new Dimension(800, 800));
                break;
            case "ADVANCED":
                frame.setSize(1700, 1000);
                gameBoard.setPreferredSize(new Dimension(1600, 800));
                break;
        }

        mainPanel.add(gameBoard, BorderLayout.NORTH);
        mainPanel.add(resetButton);

        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setVisible(true);
                setColour(i, j);

                gameBoard.add(boardButtons[i][j]);
                boardButtons[i][j].addMouseListener(new GameMouseListener());
                boardButtons[i][j].addActionListener(new GameActionListener());
                boardButtons[i][j]
                        .setFont(new Font("Monospaced", Font.BOLD, 20));
            }
        }
        return mainPanel;

    }

   /* private ImageIcon mine() {
        BufferedImage BI = null;
        try {
            BI = ImageIO.read(getClass().getResource("/img.Mine.png"));
        } catch (IOException e) {
            System.out.println("Error: file not found");
        }
        return new ImageIcon(BI);
    }*/

    public void setColour(int row, int column) {
        String currentPiece = board.getMineAt(row, column);
        switch (currentPiece) {
            case "1":
               boardButtons[row][column].setForeground(new Color(1, 0, 254));
                break;
            case "2":
                boardButtons[row][column].setForeground(new Color(1, 127, 1));
                break;
            case "3":
                boardButtons[row][column].setForeground(new Color(254, 0, 0));
                break;
            case "4":
                boardButtons[row][column].setForeground(new Color(0, 0, 127));
                break;
            case "5":
                boardButtons[row][column].setForeground(new Color(129, 1, 2));
                break;
            case "6":
                boardButtons[row][column].setForeground(new Color(0, 128, 129));
                break;
            case "7":
                boardButtons[row][column].setForeground(new Color(0, 0, 0));
                break;
            case "8":
                boardButtons[row][column].setForeground(new Color(128, 128, 128));
                break;
        }
    }

    public void reveal(int row, int column) {
        if (board.isMined(row, column, ' ')) {
            revealBlanks(row, column);
        } else {
            if (board.isValid(row, column)
                    && !board.isMined(row, column, board.getMine())) {
                boardButtons[row][column].setText(board.getMineAt(row, column));
                boardButtons[row][column].setBackground(background);
                setColour(row, column);
            }
        }
    }

    // Will reveal all the mines on the board
    public void revealMines() {
        for (int i = 0; i < boardButtons.length; i++) {
            for (int j = 0; j < boardButtons[0].length; j++) {
                if (board.isMined(i, j, board.mine)) {
                    if (boardButtons[i][j].getText().equals(flag) == false) {
                        boardButtons[i][j].setForeground(Color.BLACK);
                        boardButtons[i][j].setText(board.getMineAt(i, j));
                    } else if (board.isMined(i, j, board.mine) &&
                            boardButtons[i][j].getText().equals(flag)) {
                        boardButtons[i][j].setForeground(Color.BLACK);
                        boardButtons[i][j].setBackground(Color.RED);
                    }
                }
            }
        }
    }

    // Will reveal all adjacent blank places and the outer layer of hints
    public void revealBlanks(int row, int column) {
        if (!boardButtons[row][column].getBackground().equals(defaultBackground)) {
            return;
        }
        if (board.isMined(row, column, ' ')) {
            boardButtons[row][column].setBackground(background);
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = column - 1; j <= column + 1; j++) {
                    if (board.isMined(i, j, ' ')) {
                        revealBlanks(i, j);
                    } else {
                        reveal(i, j);
                    }
                }
            }
        }
    }

    // Return true if the player has won the game
    public boolean isWinner() {
        for (int i = 0; i < boardButtons.length; i++) {
            for (int j = 0; j < boardButtons[0].length; j++) {
                if (!board.isMined(i, j, board.mine) && boardButtons[i][j]
                        .getBackground().equals(defaultBackground)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Place / Remove flag
    public void setFlag(int row, int column) {
        if (!boardButtons[row][column].getText().equals(flag)) {
            boardButtons[row][column].setText(flag);
            boardButtons[row][column].setForeground(Color.RED);
        } else {
            boardButtons[row][column].setText("");
        }
    }

    public class GameActionListener implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == beginnerOption) {
                frame.setContentPane(createBoard("BEGINNER"));
            } else if (e.getSource() == intermediateOption) {
                frame.setContentPane(createBoard("INTERMEDIATE"));
            } else if (e.getSource() == advancedOption) {
                frame.setContentPane(createBoard("ADVANCED"));
            } else if (e.getSource() == resetButton) {
                board.reset();
                gameOver = false;
                frame.setTitle("Minespweer");
                for (int i = 0; i < boardButtons.length; i++) {
                    for (int j = 0; j < boardButtons[0].length; j++) {
                        boardButtons[i][j].setBackground(defaultBackground);
                        boardButtons[i][j].setText("");
                    }
                }
            }
            // When any board button is clicked
            if (gameOver == false) {
                for (int i = 0; i < board.getHeight(); i++) {
                    for (int j = 0; j < board.getWidth(); j++) {
                        if (e.getSource() == boardButtons[i][j]
                                && !boardButtons[i][j].getText().equals(flag)) {

                            if (board.isMined(i, j, board.mine)) {
                                gameOver = true;
                                revealMines();
                                frame.setTitle("Game Over");
                                boardButtons[i][j].setBackground(Color.RED);
                                JOptionPane.showMessageDialog(frame,
                                        "You clicked on a mine! You lost.");
                            } else {
                                reveal(i, j);
                                if (isWinner()) {
                                    gameOver = true;
                                    frame.setTitle("Game Won");
                                    JOptionPane.showMessageDialog(frame,
                                            "Congratulations! You won!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public class GameMouseListener implements java.awt.event.MouseListener {
        public void mousePressed(MouseEvent e) {
            if (gameOver == false) {
                for (int i = 0; i < boardButtons.length; i++) {
                    for (int j = 0; j < boardButtons[0].length; j++) {
                        String buttonText = boardButtons[i][j].getText();
                        if (e.getButton() == 3
                                && e.getSource() == boardButtons[i][j]) {
                            if (buttonText.equals("")
                                    || buttonText.equals(flag)) {
                                setFlag(i, j);
                            }
                        }
                    }
                }
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }

}