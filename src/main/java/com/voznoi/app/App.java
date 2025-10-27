package com.voznoi.app;

import com.voznoi.app.ai.CheckResult;
import com.voznoi.app.ai.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class App {
    public static Map<Integer,JLabel> labelMap = new HashMap<>();
    //public static List<Integer> board = new ArrayList<>();
    public static int[] board = new int[9];

    public static List<Integer> availableCells = new ArrayList<>();

    public static boolean gameFinished;

    private static CheckResult checkResult = new CheckResult();

    enum GameStatus{
        PLAYING,DRAW,WINNER_X,WINNER_O
    }

    public static JLabel gameStatusLabel;
    public static JLabel gameStatusLabel2;

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
                                computerTurn();
                            }
                        },200);

                }
            });
            labelMap.put(i,label);
        }

        JPanel actionPanel = new JPanel(new FlowLayout());

        JButton nextButton = new JButton("next");
        nextButton.addActionListener(e -> computerTurn());

        JButton resetButton = new JButton("reset");
        resetButton.addActionListener(e -> resetGame());

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
        frame.add(actionPanel,BorderLayout.SOUTH);
        frame.setVisible(true);
        checkResult.init();
        //computerTurn();
    }

    public static SecureRandom secureRandom = new SecureRandom();
    public static void computerTurn(){
        int level = availableCells.size();
        int random = secureRandom.nextInt(level);
        int cell = availableCells.get(random);
        Result selectedResult=null;
        try {
            int[] boardCopy = Arrays.copyOf(board, 9);
            List<Result> resultList = checkResult.getPossibleTopResults(boardCopy,1,1);
            List<Result> topResult2;
            if (resultList.size()>0) {
                int r = secureRandom.nextInt(resultList.size());
                selectedResult = resultList.get(r);
            }
            boardCopy[selectedResult==null?cell:selectedResult.getDesiredCell()]=1;
            topResult2 = checkResult.getPossibleTopResults(boardCopy,2,1);
            if (topResult2.size()>0){
                Result result2 = topResult2.get(0);
                System.out.println(result2);
                System.out.println(selectedResult);
                if (selectedResult== null || result2.getDistance()<=selectedResult.getDistance()){
                    selectedResult = result2;
                }
            }
            if (selectedResult!=null) {
                cell = selectedResult.getDesiredCell();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        setToBoard(cell,1);
    }

    public static void setToBoard(int cell, int xo){
        board[cell] = xo;
        JLabel label = labelMap.get(cell);
        String text = xo==1?"X":"O";
        label.setText(text);
        removeFromAvailableCells(cell);
        checkGame(xo);
    }

    public static void removeFromAvailableCells(int i){
        availableCells.removeIf(c->c.equals(i));
    }

    public static void checkGame(int xo){
        boolean[] g = new boolean[8];
        g[0] = checkLine(0,1,2);
        g[1] = checkLine(3,4,5);
        g[2] = checkLine(6,7,8);
        g[3] = checkLine(0,3,6);
        g[4] = checkLine(1,4,7);
        g[5] = checkLine(2,5,8);
        g[6] = checkLine(0,4,8);
        g[7] = checkLine(2,4,6);

        for (boolean b : g) {
            if (b) {
                gameOver(xo);
                return;
            }
        }

        if (availableCells.size()==0){
            gameOver(0);
        }

        try {
            List<Result> resultList = checkResult.getResult(board,1,1);
            gameStatusLabel2.setText(String.valueOf(resultList.get(0).getResult()));
            //System.out.println(Arrays.toString(resultList.get(0).getRow()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void gameOver(int xo){
        String winner = switch (xo){
            case 1 -> GameStatus.WINNER_X.toString();
            case 2 -> GameStatus.WINNER_O.toString();
            default -> GameStatus.DRAW.toString();
        };
        gameStatusLabel.setText(winner);
        gameFinished = true;
    }

    public static boolean checkLine(int c1, int c2, int c3){
        return board[c1]!=0 && board[c1]==board[c2] && board[c1]==board[c3];
    }

    public static void resetGame(){
        labelMap.values().forEach(l->l.setText(""));
        Arrays.fill(board,0);
        availableCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            availableCells.add(i);
        }
        gameFinished = false;
        //computerTurn();
    }

}
