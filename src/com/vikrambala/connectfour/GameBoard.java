package com.vikrambala.connectfour;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.vikrambala.connectfour.error.ErrorHandler;


/**
 * This class instantiates a Connect4 object, which is the model for the game.
 * As the user clicks the game board, the model is updated.  Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model. (VIEW/CONTROLLER)
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private ConnectFour cfour; // model for the game
    private JLabel status; // current status text
    private JFrame mainFrame;
    private String username; 
    private JLabel userInfo; 
    private GameAI ai;
    
    int winner = 0; //1, 2 for player win, 3 for tie
    //1 is the player of this application. 2 is the other player online

    // Game constants
    public static final int BOARD_WIDTH = 700;
    public static final int BOARD_HEIGHT = 600;
    

    /**
     * Initializes the game board.
     */
    public GameBoard(JLabel statusInit, JFrame mainFrame, JLabel userInfo, JButton undo) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        
        this.mainFrame = mainFrame;
        
        this.userInfo = userInfo;
       
        
        // Enable keyboard focus on the court area.
        // When this component has the keyboard focus, key events are handled by its key listener.
        setFocusable(true);
        
        cfour = new ConnectFour(); // initializes model for the game
        status = statusInit; // initializes the status JLabel
        ai = new GameAI(cfour); //initializes the AI
        
        undo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cfour.undoTurn(); 
                updateStatus(); // updates the status JLabel
                repaint(); // repaints the game board
            }
            
        });
        
        /*
         * Listens for mouseclicks.  Updates the model, then updates the game board
         * based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                //Prevents player from playing for the AI; 
                if (!cfour.playerOneTurn()) {
                    return; 
                }
                
                Point p = e.getPoint();

                // updates the model given the coordinates of the mouseclick
                boolean played = cfour.playTurn(p.x / 100, p.y / 100);

                updateStatus(); // updates the status JLabel
                repaint(); // repaints the game board
                
                boolean gameOver = cfour.getGameOver();
                
                //-------------AI-----------//
                if (!gameOver  && played) {
                    int[] minimax = ai.minimaxAlgo(6, cfour.getBoard(), true);
                    int c = minimax[0];
                    int score = minimax[1];
                    
                    System.out.println("For move: " + c + ", score was " + score + ".");
                    cfour.playTurn(c, cfour.rowForColumn(c));
                    
                    updateStatus(); // updates the status JLabel
                    repaint(); // repaints the game board
                }
                //------------AI END----------//
                
                if (cfour.getGameOver()) {
                    
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setLocation(300, 200);
                    frame.setAlwaysOnTop(true);
                    
                    JPanel winnerPanel = new JPanel();
                    frame.add(winnerPanel);
                    int choice = -1; 
                   
                    Object[] choices = new Object[]{"Restart", "Quit"};
                    String winnerString;
                    
                    switch (winner) {
                        case 1:
                            winnerString = "Player 1 Wins!!";
                            new LoginServerConnection().updateWin(username); 
                            break;
                        case 2:
                            winnerString = "AI wins!!";
                            new LoginServerConnection().updateLoss(username); 
                            break;
                        case 3:
                            winnerString = "Tie Game!!";
                            break; 
                        default:
                            winnerString = "ERROR, PLEASE CONTACT DEV";
                            break;
                                
                    }
                    
                    winnerPanel.add(new JLabel(winnerString)); 
                    choice = JOptionPane.showOptionDialog(frame, winnerPanel, "Game Ended", 
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, 0); 
                    handleDialog(choice);
      
                }
                
            }
        });
    }
    

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        cfour.reset();
        status.setText("Player 1's Turn");
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    
    private void handleDialog(int choice) {
        if (choice == 0) {
            int[] userInfoArray = new LoginServerConnection().updatedUserLabelInfo(this.username);
            if (userInfoArray.length == 3) {
                ErrorHandler.errorHandler(userInfoArray[2]);
                return; 
            }
            userInfo.setText("User: " + this.username + " Wins: " + userInfoArray[0] + " Losses: "
                             + userInfoArray[1]);
            reset();
        } else {
            mainFrame.dispose();
            System.exit(0);
        }
    }
    
    public void setUsername(String username) {
        this.username = username; 
    }
    
    
    /**
     * Updates the JLabel to reflect the current state of the game.
     */ 
    private void updateStatus() {
        
        if (cfour.getCurrentPlayer()) {
            status.setText("Player 1's Turn");
            
        } else {
            status.setText("AI's Turn");
        }
        
        
        
        int winner = cfour.checkWinner();
        if (winner == 1) {
            this.winner = 1;
            status.setText("Player 1 wins!!!");
        } else if (winner == 2) {
            this.winner = 2; 
            status.setText("AI wins!!!");
        } else if (winner == 3) {
            this.winner = 3;
            status.setText("It's a tie.");
        } 
        

        
    }

    /**
     * Draws the game board.
     * 
     * There are many ways to draw a game board.  This approach
     * will not be sufficient for most games, because it is not 
     * modular.  All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper 
     * methods.  Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        drawBoardLines(g);
        
        paintCircles(g); 
    }
    
    /** 
     * Draws connect-4 board grid
     */
    private void drawBoardLines(Graphics g) {
        
        setBackground(Color.BLUE);
        //Draw column
        g.drawLine(100, 0, 100, 600);
        g.drawLine(200, 0, 200, 600);
        g.drawLine(300, 0, 300, 600);
        g.drawLine(400, 0, 400, 600);
        g.drawLine(500, 0, 500, 600);
        g.drawLine(600, 0, 600, 600);
        g.drawLine(700, 0, 700, 600);
        
        //Draw row lines
        g.drawLine(0, 100, 700, 100);
        g.drawLine(0, 200, 700, 200);
        g.drawLine(0, 300, 700, 300);
        g.drawLine(0, 400, 700, 400);
        g.drawLine(0, 500, 700, 500);
        g.drawLine(0, 600, 700, 600);
        
        
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                g.drawOval(20 + 100 * j, 20 + 100 * i, 60, 60);
                g.setColor(Color.WHITE);
                g.fillOval(20 + 100 * j, 20 + 100 * i, 60, 60);
           
            }
        }
    }
    
    /**
     * Draws Different Colored Circles for Each Player
     */
    private void paintCircles(Graphics g) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                int state = cfour.getCell(j, i);
                if (state == 1) {
                    g.drawOval(20 + 100 * j, 20 + 100 * i, 60, 60);
                    g.setColor(Color.RED);
                    g.fillOval(20 + 100 * j, 20 + 100 * i, 60, 60);
                } else if (state == 2) {
                    g.drawOval(20 + 100 * j, 20 + 100 * i, 60, 60);
                    g.setColor(Color.BLACK);
                    g.fillOval(20 + 100 * j, 20 + 100 * i, 60, 60);
                }
            }
        }
    }
    
    

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}