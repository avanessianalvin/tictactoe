package com.voznoi.app;

import com.voznoi.app.ai.MindJ48;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.SecureRandom;
import java.util.List;
import java.util.Timer;
import java.util.*;

public class AppJ48 {
    public static Map<Integer,JLabel> labelMap = new HashMap<>();
    //public static List<Integer> board = new ArrayList<>();
    public static int[] board = new int[9];

    public static List<Integer> availableCells = new ArrayList<>();

    public static boolean gameFinished;

    private static MindJ48 mindXJ48 = new MindJ48();
    private static MindJ48 mindOJ48 = new MindJ48();


    enum GameStatus{
        PLAYING,DRAW,WINNER_X,WINNER_O
    }

    public static JLabel gameStatusLabel;
    public static JLabel gameStatusLabel2;

    private static int swapXO (int xo){
        if (xo==0) return 0;
        return (xo==1)?2:1;
    }

    private static int[] swapBoard(int [] board){
        return Arrays.stream(board).map(AppJ48::swapXO).toArray();
    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Tic Tak Tok");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);

        // Create a panel with GridLayout (3 rows, 3 columns)
        JPanel panel = new JPanel(new GridLayout(3, 3, 10, 10));
        // Last two params are horizontal and vertical gaps (optional)

        Font font = new Font(Font.SANS_SERIF,Font.BOLD,32);
        Font font2 = new Font(Font.SANS_SERIF,Font.BOLD,16);
        // Add 9 labels
        for (int i = 0; i < 9; i++) {
            board[i]=0;
            availableCells.add(i);
            JLabel label = new JLabel("Label " + i, SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBackground(Color.LIGHT_GRAY);
            label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            panel.add(label);
            label.setText("");
            label.setName(String.valueOf(i));
            label.setFont(font);
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setToBoard(Integer.parseInt(label.getName()),2);
                    if (!gameFinished)
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                computerTurn(1);
                            }
                        },200);

                }
            });
            labelMap.put(i,label);
        }

        JPanel actionPanel = new JPanel(new FlowLayout());

        JButton nextButton = new JButton("next");
        nextButton.addActionListener(e -> computerTurn(1));

        JButton resetButton = new JButton("reset");
        resetButton.addActionListener(e -> resetGame(1));

        JButton startButton = new JButton("start");
        startButton.addActionListener(e -> startGame());

        JButton nextMoveButton = new JButton("next move");
        nextMoveButton.addActionListener(e -> nextMove());

        Panel topPanel = new Panel(new GridLayout(1,4,10,10));
        JLabel xLabel = new JLabel("X", SwingConstants.CENTER);
        xLabel.setFont(font2);
        gameStatusLabel = new JLabel(GameStatus.PLAYING.toString(), SwingConstants.CENTER);
        gameStatusLabel.setFont(font2);
        gameStatusLabel2 = new JLabel("", SwingConstants.CENTER);
        gameStatusLabel2.setFont(font2);

        JLabel oLabel = new JLabel("O" ,SwingConstants.CENTER);
        oLabel.setFont(font2);
        topPanel.add(xLabel);
        topPanel.add(gameStatusLabel);
        topPanel.add(gameStatusLabel2);
        topPanel.add(oLabel);

        // Add panel to frame
        frame.add(topPanel,BorderLayout.NORTH);
        frame.add(panel,BorderLayout.CENTER);
        actionPanel.add(nextButton);
        actionPanel.add(resetButton);
        actionPanel.add(startButton);
        actionPanel.add(nextMoveButton);
        frame.add(actionPanel,BorderLayout.SOUTH);
        frame.setVisible(true);
        mindXJ48.init("./data/gameResultForXJ48.aarf");
        mindOJ48.init("./data/gameResultForOJ48.aarf");
        System.out.println("J48 created");
        //computerTurn(1);
    }

    public static void startGame(){
        resetGame(0);
        gameFinished=false;
        xo = 2;
    }
    static int xo;

    public static void nextMove(){
        System.out.println(xo);
        computerTurn(xo);
        xo = swapXO(xo);
    }

    public static SecureRandom secureRandom = new SecureRandom();
    public static void computerTurn(int xo){
        int level = availableCells.size();
        int random = secureRandom.nextInt(level);
        int cell = availableCells.get(random);
        if (availableCells.size()<9) {
            try {
                int[] boardCopy = Arrays.copyOf(board, 9);
                int cellX = mindXJ48.getResult(boardCopy);
                boardCopy[cellX] = xo;
                if (availableCells.contains(cellX)){
                    cell = cellX;
                }
                int[] swappedBoard = swapBoard(boardCopy);
                swappedBoard[cell] = swapXO(xo);
                int cellO = mindXJ48.getResult(swappedBoard);
                int[] boardCopyO = Arrays.copyOf(board, 9);
                boardCopyO[cellO] = swapXO(xo);
                System.out.println(Arrays.toString(board));
                System.out.println(Arrays.toString(swappedBoard) + "   " +  checkGame(swappedBoard, swapXO(xo)));
                if (availableCells.contains(cellO) && checkGame(swappedBoard, swapXO(xo))) {
                    cell = cellO;
                }

                System.out.println(cell + "  " + cellO);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int finalCell = cell;
        availableCells.removeIf(c->c.equals(finalCell));
        System.out.println(availableCells);

        setToBoard(cell,xo);
    }

    public static void setToBoard(int cell, int xo){
        board[cell] = xo;
        JLabel label = labelMap.get(cell);
        String text = xo==1?"X":"O";
        label.setText(text);
        removeFromAvailableCells(cell);
        if (availableCells.size()==0){
            gameOver(0);
        }
        boolean game = checkGame(board, xo);
        if (game){
            gameOver(xo);
        }
    }

    public static void removeFromAvailableCells(int i){
        availableCells.removeIf(c->c.equals(i));
    }

    public static boolean checkGame(int[] board, int xo){
        boolean[] g = new boolean[8];
        g[0] = checkLine(board,0,1,2);
        g[1] = checkLine(board,3,4,5);
        g[2] = checkLine(board,6,7,8);
        g[3] = checkLine(board,0,3,6);
        g[4] = checkLine(board,1,4,7);
        g[5] = checkLine(board,2,5,8);
        g[6] = checkLine(board,0,4,8);
        g[7] = checkLine(board,2,4,6);

        for (boolean b : g) {
            if (b) {
                return true;
            }
        }

        try {
            //gameStatusLabel2.setText(String.valueOf(resultList.get(0).getResult()));
            //System.out.println(Arrays.toString(resultList.get(0).getRow()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static void gameOver(int xo){
        String winner = switch (xo){
            case 1 -> GameStatus.WINNER_X.toString();
            case 2 -> GameStatus.WINNER_O.toString();
            default -> GameStatus.DRAW.toString();
        };
        gameStatusLabel.setText(winner);
        System.out.println(winner);
        gameFinished = true;
    }

    public static boolean checkLine(int[] b, int c1, int c2, int c3){
        return b[c1]!=0 && b[c1]==b[c2] && b[c1]==b[c3];
    }

    public static void resetGame(int xo){
        labelMap.values().forEach(l->l.setText(""));
        Arrays.fill(board,0);
        availableCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            availableCells.add(i);
        }
        gameFinished = false;
//        if (xo!=0)
//            computerTurn(xo);
    }

}
