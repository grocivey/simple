package com.simple.hanoitower;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HanoiTowerGUI2 extends JFrame {
    private int numDiscs;
    private int moveCount;
    private JLabel moveLabel;
    private HanoiPanel hanoiPanel;
    private JButton forwardButton;
    private JButton backButton;

    public HanoiTowerGUI2(int numDiscs) {
        this.numDiscs = numDiscs;
        this.moveCount = 0;

        setTitle("汉诺塔");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        moveLabel = new JLabel("移动次数: 0");
        add(moveLabel, BorderLayout.NORTH);

        hanoiPanel = new HanoiPanel(numDiscs);
        add(hanoiPanel, BorderLayout.CENTER);

        forwardButton = new JButton("前进");
        forwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (moveCount < Math.pow(2, numDiscs) - 1) {
                    moveCount++;
                    moveLabel.setText("移动次数: " + moveCount);
                    hanoiPanel.moveNext();
                }
            }
        });

        backButton = new JButton("后退");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (moveCount > 0) {
                    moveCount--;
                    moveLabel.setText("移动次数: " + moveCount);
                    hanoiPanel.moveBack();
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(forwardButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }



    public static void main(String[] args) {
        int numDiscs = 5; // 几个盘子
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HanoiTowerGUI2(numDiscs);
            }
        });
    }
}

class HanoiPanel extends JPanel {
    private int numDiscs;
    private int[] discPositions;
    private int moveCount;
    private int[][] moves;

    public HanoiPanel(int numDiscs) {
        this.numDiscs = numDiscs;
        this.discPositions = new int[numDiscs];
        this.moveCount = 0;
        this.moves = new int[(int) Math.pow(2, numDiscs) - 1][2];

        for (int i = 0; i < numDiscs; i++) {
            discPositions[i] = 1; // Start with all discs on the first peg
        }

        solveHanoi(numDiscs, 1, 2, 3); // Solve the Hanoi Tower problem and store the moves

        setPreferredSize(new Dimension(1000, 800));
        //重置移动次数
        this.moveCount = 0;

    }


    public void moveNext() {
        if (moveCount < moves.length) {
            int[] move = moves[moveCount];
            int fromPeg = move[0];
            int toPeg = move[1];
            int discSize = getTopDiscSize(fromPeg);

            discPositions[discSize - 1] = toPeg;
            moveCount++;

            repaint();
        }
    }


    public void moveBack() {
        if (moveCount > 0) {
            moveCount--;
            int[] move = moves[moveCount];
            int fromPeg = move[0];
            int toPeg = move[1];
            int discSize = getTopDiscSize(toPeg);

            discPositions[discSize - 1] = fromPeg;

            repaint();
        }
    }

    private void solveHanoi(int numDiscs, int fromPeg, int toPeg, int auxPeg) {
        if (numDiscs == 1) {
            moves[moveCount][0] = fromPeg;
            moves[moveCount][1] = toPeg;
            moveCount++;
        } else {
            solveHanoi(numDiscs - 1, fromPeg, auxPeg, toPeg);
            moves[moveCount][0] = fromPeg;
            moves[moveCount][1] = toPeg;
            moveCount++;
            solveHanoi(numDiscs - 1, auxPeg, toPeg, fromPeg);
        }
    }

    private int getTopDiscSize(int peg) {
        int discSize = 0;

        for (int i = 0; i < numDiscs; i++) {
            if (discPositions[i] == peg) {
                discSize = i + 1;
                break; // 找到盘片后立即退出循环
            }
        }

        return discSize;
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int pegWidth = getWidth() / 4;
        int pegHeight = getHeight();
        int discHeight = 20;

        for (int i = 1; i <= 3; i++) {
            int pegX = i * pegWidth - pegWidth / 2;
            int pegY = getHeight() - pegHeight;

            g.setColor(Color.BLACK);
            g.fillRect(pegX - 5, pegY, 10, pegHeight);

            for (int j = 0; j < numDiscs; j++) {
                if (discPositions[j] == i) {
                    int discX = pegX - discHeight / 2 * (j + 1);
                    //大盘在最底下
                    int discY =  discHeight * j + 800 - discHeight*numDiscs;
                    g.setColor(Color.RED);
                    g.fillRect(discX, discY, discHeight * (j + 1), discHeight);
                }
            }
        }
    }
}
