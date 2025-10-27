package com.voznoi.app.generator;

import javax.swing.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ResultGenerator {
    public List<Integer> board = new ArrayList<>();
    public List<Integer> availableCells = new ArrayList<>();
    public boolean gameFinished;

    enum GameStatus {
        DRAW, WINNER_X, WINNER_O, PLAYING
    }

    public GameStatus gameStatus = GameStatus.PLAYING;

    public ResultGenerator() {
        for (int i = 0; i < 9; i++) {
            board.add(0);
            availableCells.add(i);
        }
    }

    int turn = 1;

    public void changeTurn() {
        turn = turn == 1 ? 2 : 1;
    }

    public static SecureRandom secureRandom = new SecureRandom();

    public void play() {
        int level = availableCells.size();
        int random = secureRandom.nextInt(level);
        int cell = availableCells.remove(random);
        board.set(cell, turn);
        checkGame();
        changeTurn();
    }

    private void checkGame() {
        boolean[] g = new boolean[8];
        g[0] = checkLine(0, 1, 2);
        g[1] = checkLine(3, 4, 5);
        g[2] = checkLine(6, 7, 8);
        g[3] = checkLine(0, 3, 6);
        g[4] = checkLine(1, 4, 7);
        g[5] = checkLine(2, 5, 8);
        g[6] = checkLine(0, 4, 8);
        g[7] = checkLine(2, 4, 6);

        for (boolean b : g) {
            if (b) {
                gameOver(turn);
                return;
            }
        }
        if (availableCells.size() == 0) {
            gameOver(0);
        }
    }

    public boolean checkLine(int c1, int c2, int c3) {
        return (!board.get(c1).equals(0)) && board.get(c1).equals(board.get(c2)) && board.get(c1).equals(board.get(c3));
    }

    private void gameOver(int res) {
        gameStatus = switch (res) {
            case 1 -> GameStatus.WINNER_X;
            case 2 -> GameStatus.WINNER_O;
            default -> GameStatus.DRAW;
        };
    }

    public List<Integer> generate() {
        for (int i = 0; i < 9; i++) {
            if (gameStatus==GameStatus.PLAYING)
                play();
        }
        List<Integer> result = new ArrayList<>(board);
        result.add(gameStatus.ordinal());
        return result;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            ResultGenerator resultGenerator = new ResultGenerator();
            List<Integer> generate = resultGenerator.generate();
            matrixPrint(generate);
        }
    }

    public static void matrixPrint(List<Integer> board){
        System.out.println();
        for (int i = 0; i < 9; i++) {
            System.out.print(board.get(i) + " ");
            if (i%3==2) System.out.println();
        }
        if (board.size()==10)
            System.out.println(board.get(9));
    }

}
