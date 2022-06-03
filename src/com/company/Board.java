package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serial;
import java.util.Locale;
import java.util.Random;

public class Board {

    static JPanel panel = new JPanel();

    public static int width;
    public static int height;
    public int mineCount;
    public String difficulty;

    public char flag = '⚑';
    public char mine = '✸';

    private char[][] board;

    public Board (String difficulty) {
        setDifficulty(difficulty);
        reset();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty.toUpperCase();
        if (difficulty.equals("BEGINNER")) {
            this.width = 10;
            this.height = 10;
            this.mineCount = 10;
        } else if (difficulty.equals("INTERMEDIATE")) {
            this.width = 20;
            this.height = 20;
            this.mineCount = 15;
        } else if (difficulty.equals("ADVANCED")) {
            this.width = 30;
            this.height = 20;
            this.mineCount = 30;
        } else {
            System.out.println("Selected default level.");
            this.width = 10;
            this.height = 10;
            this.mineCount = 10;
        }
    }

    public int surroundingMines (int row, int column) {
        int nearbyMines = 0;
        for (int i=row-1; i<=row+1;i++) {
            for (int j=column-1; j<=column+1;j++) {
                if (isMined(i, j, this.mine)) {
                    nearbyMines++;
                }
            }
        }
        return nearbyMines;
    }

    public boolean isMined (int row, int column, char mine) {
        if (isValid(row, column) && this.board[row][column] == mine) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isValid (int row, int column) {
        if (row >= 0 && row < this.height && column >= 0 && column < this.width) {
            return true;
        } else {
            return false;
        }
    }


    public String getMineAt (int row, int column) {
        return Character.toString(this.board[row][column]);
    }

    public void reset() {
        board = new char[this.height][this.width];
        int[] mineIndexes = new Random().ints(0, (this.width * this.height - 1)).limit(mineCount).distinct().toArray();

        for (int i = 0; i < mineIndexes.length; i++) {
            this.board[Math.floorDiv(mineIndexes[i], this.width)][mineIndexes[i] % this.width] = this.mine;
        }

        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[0].length; j++) {
                if (this.board[i][j] != mine) {
                    int nearbyMines = surroundingMines(i, j);
                    if (nearbyMines != 0) {
                        this.board[i][j] = (char) (nearbyMines + 48);
                    } else {
                        this.board[i][j] = ' ';
                    }
                }
            }
        }
    }

    public char getMine() {
        return this.mine;
    }

    public char getFlag() {
        return this.flag;
    }

    public String toString() {
        String newString = "";
        for (int i=0; i<this.board.length; i++) {
            for (int j=0; j<this.board[0].length; j++) {
                newString += this.board[i][j];
            }
            newString += "\n";
        }
        return newString;
    }



}
