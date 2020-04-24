import models.Stick;
import models.StickStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class UIGame {

    private Map jPanel;

    public UIGame(String player, int turn, PlayerHandler ph, Player playerClass) {
        final int height = 500;
        final int width = 500;
        final int cells = 5;
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Player: " + player);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jPanel = new Map(cells, width, height, player, turn, ph, playerClass);
        frame.getContentPane().add(jPanel);

        frame.setPreferredSize(new Dimension(width, height));

        frame.pack();
        frame.setVisible(true);
    }


    public void update(int stickId, int result) {
        jPanel.update(stickId, result);
    }
}

class Map extends JPanel {
    private PlayerHandler ph;
    private boolean mouseListenerIsActive = true;
    private int linesCount;

    private StickStore stickStore;
    private Stick startStick;
    private ArrayList<Stick> sticks = new ArrayList<>();

    private int width;
    private int height;
    private Color playerColor;
    private Color opponentColor;
    private int turn;
    private int opponentScore = 0;
    private int playerScore = 0;

    public void repaintMap() {
        this.revalidate();
        this.repaint();
    }

    public Map(final int linesCount, int width, int height, String player, int turn,
               PlayerHandler ph, Player playerClass) {
        final Color green = new Color(0, 255, 0);
        final Color red = new Color(255, 0, 0);
        final Color blue = new Color(50, 25, 220);
        this.ph = ph;
        this.linesCount = linesCount;
        this.stickStore = new StickStore();
        this.height = height;
        this.width = width;
        this.turn = turn;
        switch (player) {
            case ("BLUE"):
                this.playerColor = blue;
                this.opponentColor = red;
                break;
            case ("RED"):
                this.playerColor = red;
                this.opponentColor = blue;
                break;
            default:
                this.playerColor = green;
                this.opponentColor = green;
                break;
        }
        this.init();


        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mouseListenerIsActive) {
                    super.mouseClicked(e);
                    if (getTurn() == 1) {
                        ArrayList<Stick> sticks = stickStore.getSticks();
                        for (int i = 0; i < sticks.size(); i++) {
                            Stick stick = sticks.get(i);
                            if (stick.contains(e.getPoint()) && stick.color != playerColor &&
                                    stick.color != opponentColor) {
                                int result = calculateResult(stick, playerColor);
                                setTurn(result);
                                if (result > 0) {
                                    playerScore++;
                                }
                                try {
                                    ph.playerTurn(playerClass, stick.getId(), result);
                                } catch (RemoteException ex) {
                                    ex.printStackTrace();
                                }
                                stick.color = playerColor;
                                repaintMap();
                                break;
                            }
                        }

                    } else {
                        if (mouseListenerIsActive) {
                            System.out.println("IT'S YOUR OPPONENT TURN !");
                        }
                    }
                }
            }
        });

    }

    private void setTurn(int i) {
        this.turn = i;
    }

    private int getTurn() {
        return this.turn;
    }

    private void init() {
        int cellWidth = this.height / linesCount;
        int cellHeight = this.width / linesCount;

        int stickWidth = 15;
        int topMargin = 8;
        int sideMargin = 15;
        for (int i = 0; i < linesCount; i++) {
            for (int j = 0; j < linesCount; j++) {
                int index;
                if (j != linesCount - 1) {
                    index = this.stickStore.addStick(j * cellWidth + topMargin + sideMargin, i * cellHeight + topMargin,
                            cellHeight + sideMargin, stickWidth);
                    if (i == 0) {
                        this.stickStore.getSticks().get(index).borderU = true;
                    }
                    if (i == linesCount - 1) {
                        this.stickStore.getSticks().get(index).borderD = true;
                    }
                    this.stickStore.getSticks().get(index).horizontal = true;
                }
            }
            for (int j = 0; j < linesCount; j++) {
                int index;
                if (i != linesCount - 1) {
                    index = this.stickStore.addStick(j * cellWidth + topMargin + sideMargin, i * cellHeight + topMargin,
                            stickWidth, cellHeight + sideMargin);
                    if (j == 0) {
                        this.stickStore.getSticks().get(index).borderL = true;
                    }
                    if (j == linesCount - 1) {
                        this.stickStore.getSticks().get(index).borderR = true;
                    }
                    if (index > -1) this.stickStore.getSticks().get(index).vertical = true;
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setBackground(Color.white);
        g.setColor(Color.BLACK);
        this.stickStore.draw(g);
    }

    private int calculateResult(Stick startStick, Color pColor) {
        this.sticks = this.stickStore.getSticks();
        this.startStick = startStick;
        boolean r = this.rightCheck(startStick.getId(), pColor);
        boolean l = this.leftCheck(startStick.getId(), pColor);
        boolean u = this.upCheck(startStick.getId(), pColor);
        boolean d = this.downCheck(startStick.getId(), pColor);
        if (r || l || u || d) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean rightCheck(int id, Color pColor) {
        if (sticks.get(id).borderR || sticks.get(id).horizontal) {
            return false;
        }
        if (
                (this.sticks.get(id + 1).color == opponentColor ||
                        this.sticks.get(id + 1).color == playerColor) &&
                        (this.sticks.get(id + linesCount).color == opponentColor ||
                                this.sticks.get(id + linesCount).color == playerColor) &&
                        (this.sticks.get(id - linesCount + 1).color == opponentColor ||
                                this.sticks.get(id - linesCount + 1).color == playerColor)
        ) {

            int index = this.stickStore.addStick(this.sticks.get(id).x, this.sticks.get(id).y,
                    this.width / linesCount, this.width / linesCount);
            this.stickStore.getSticks().get(index).color = pColor;
            this.stickStore.getSticks().get(index).cell = true;
            return true;
        }

        return false;
    }

    private boolean leftCheck(int id, Color pColor) {
        if (sticks.get(id).borderL || sticks.get(id).horizontal) {
            return false;
        }
        if (
                (this.sticks.get(id - 1).color == opponentColor ||
                        this.sticks.get(id - 1).color == playerColor) &&
                        (this.sticks.get(id - linesCount).color == opponentColor ||
                                this.sticks.get(id - linesCount).color == playerColor) &&
                        (this.sticks.get(id + linesCount - 1).color == opponentColor ||
                                this.sticks.get(id + linesCount - 1).color == playerColor)
        ) {
            int index = this.stickStore.addStick(this.sticks.get(id - 1).x, this.sticks.get(id - 1).y,
                    this.width / linesCount, this.width / linesCount);
            this.stickStore.getSticks().get(index).color = pColor;
            this.stickStore.getSticks().get(index).cell = true;
            return true;
        }

        return false;
    }

    private boolean upCheck(int id, Color pColor) {
        if (sticks.get(id).borderU || sticks.get(id).vertical) {
            return false;
        }
        if (
                (this.sticks.get(id - linesCount * 2 + 1).color == opponentColor ||
                        this.sticks.get(id - linesCount * 2 + 1).color == playerColor) &&
                        (this.sticks.get(id - linesCount).color == opponentColor ||
                                this.sticks.get(id - linesCount).color == playerColor) &&
                        (this.sticks.get(id - linesCount + 1).color == opponentColor ||
                                this.sticks.get(id - linesCount + 1).color == playerColor)
        ) {
            int index = this.stickStore.addStick(this.sticks.get(id - linesCount).x,
                    this.sticks.get(id - linesCount).y, this.width / linesCount,
                    this.width / linesCount);
            this.stickStore.getSticks().get(index).color = pColor;
            this.stickStore.getSticks().get(index).cell = true;
            return true;
        }
        return false;
    }

    private boolean downCheck(int id, Color pColor) {
        if (sticks.get(id).borderD || sticks.get(id).vertical) {
            return false;
        }
        if (
                (this.sticks.get(id + linesCount * 2 - 1).color == opponentColor ||
                        this.sticks.get(id + linesCount * 2 - 1).color == playerColor) &&
                        (this.sticks.get(id + linesCount).color == opponentColor ||
                                this.sticks.get(id + linesCount).color == playerColor) &&
                        (this.sticks.get(id + linesCount - 1).color == opponentColor ||
                                this.sticks.get(id + linesCount - 1).color == playerColor)
        ) {
            int index = this.stickStore.addStick(this.sticks.get(id + linesCount - 1).x,
                    this.sticks.get(id + linesCount - 1).y, this.width / linesCount,
                    this.width / linesCount);
            this.stickStore.getSticks().get(index).color = pColor;
            this.stickStore.getSticks().get(index).cell = true;
            return true;
        }
        return false;
    }

    public void update(int stickId, int result) {
        stickStore.getSticks().get(stickId).color = opponentColor;
        if (result > 0) {
            this.opponentScore++;
            System.out.println("You: " + this.playerScore + "\nOpponent: " + this.opponentScore);
            calculateResult(stickStore.getSticks().get(stickId), opponentColor);
            this.turn = 0;
        } else {
            this.turn = 1;
        }
        repaintMap();
        if (playerScore >= 9) {
            System.out.println("RED WON!");
            mouseListenerIsActive = false;
        } else if (opponentScore >= 9) {
            System.out.println("BLUE WON!");
            mouseListenerIsActive = false;
        } else if (playerScore == 8 && opponentScore == 8) {
            System.out.println("TIE!");
            mouseListenerIsActive = false;
        }
    }
}
