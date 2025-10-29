package com.voznoi.app.generator;

import java.io.File;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class ResultGeneratorForJ48 {
    private int[] board;
    private List<Integer> availableCells;
    private boolean gameOver = false;
    private SecureRandom secureRandom = new SecureRandom();

    public ResultGeneratorForJ48(){
        init();
    }

    public void init(){
        board = new int[9];
        availableCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            availableCells.add(i);
        }
        gameOver = false;
    }

    public int setNextMove(int xo){
        int random = secureRandom.nextInt(availableCells.size());
        int cell = availableCells.remove(random);
        board[cell] = xo;
        return cell;
    }

    public boolean checkLine(int c1, int c2, int c3){
        return board[c1]!=0 && board[c1]==board[c2] && board[c1]==board[c3];
    }
    public int checkGame(int xo){
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
                gameOver = true;
                return xo;
            }
        }
        if (availableCells.size()==0){
            gameOver = true;
            return 0;
        }
        return -1;
    }

    private int swapXO(int xo){
        return xo==1?2:1;
    }
    public GameData createBoard(){
        int xo = 2;
        int gameStatus = 0;
        int lastCell = -1;
        while (!gameOver){
            xo = swapXO(xo);
            lastCell = setNextMove(xo);
            gameStatus = checkGame(xo);
        }


        return new GameData(board,lastCell,xo,gameStatus);


    }

    record GameData(int[] board,int lastCell, int xo, int gameStatus){}

    public String getGameStatus(int status){
        return switch (status){
            case 1 -> "X WINS";
            case 2 -> "O WINS";
            default -> "DRAW";
        };
    }

    public int[] getRow(int[] r, int lastCell){
        r[lastCell] = 0;
        int[] row = Arrays.copyOf(r, 10);
        row[9] = lastCell;
        return row;
    }

    public static void main(String[] args) {
        ResultGeneratorForJ48 generator = new ResultGeneratorForJ48();
        Map<String,GameData> map = new HashMap<>();
        for (int i = 0; i < 20000; i++) {
            generator.init();
            GameData gameData = generator.createBoard();
            map.put(Arrays.toString(gameData.board),gameData);
        }

        Set <GameData> xWinningSet = map.values().stream().filter(g->g.gameStatus==1).collect(Collectors.toSet());
        Set <GameData> oWinningSet = map.values().stream().filter(g->g.gameStatus==2).collect(Collectors.toSet());
        Set <GameData> drawSet = map.values().stream().filter(g->g.gameStatus==0).collect(Collectors.toSet());
        //map.values().forEach(g->System.out.println(Arrays.toString(g.board)+ " last Cell:" + g.lastCell + " gameStatus:" + g.gameStatus ));
        //map.values().forEach(g-> System.out.println(Arrays.toString(generator.getRow(g.board, g.lastCell))));

        //xWinningSet.forEach(g-> System.out.println(Arrays.toString(generator.getRow(g.board, g.lastCell))));


        //System.out.println("X Winning: " + xWinningSet.size());
        //System.out.println("O Winning: " + oWinningSet.size());
        //System.out.println("Draw: " + drawSet.size());


        oWinningSet.forEach(g-> System.out.println(        Arrays.stream(generator.getRow(g.board,g.lastCell))
                .mapToObj(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","))));

    }
}
