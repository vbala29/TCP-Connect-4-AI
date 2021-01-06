package com.vikrambala.connectfour;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.vikrambala.connectfour.accounts.LoggedIn;
import com.vikrambala.connectfour.accounts.Login;
import com.vikrambala.connectfour.interlude.InterludeWindow;

/**
 * This class sets up the top-level frame and widgets for the GUI.
 * The class instantiates a login window, synchronizes access to a LoggedIn
 * object until the thread is interrupted, indicating that a login has been 
 * successfully performed and the board should be displayed. This class then displays
 * the GameBoard. 
 * @author vikrambala
 */
public class Game implements Runnable {
    public void run() {
        // NOTE: the 'final' keyword denotes immutability even for local variables.

        // Top-level frame in which game components live
        final JFrame frame = new JFrame("Connect-4");
        frame.setLocation(400, 200);
        
        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Initializing...");
        status_panel.add(status);
        
        //User Info (Wins and Losses)
        JLabel userInfo = new JLabel(); 
        
        //Allows for moves to be undone, take out if do network IO
        final JButton undo = new JButton("Undo");
        
        // Game board
        final GameBoard board = new GameBoard(status, frame, userInfo, undo);
        frame.add(board, BorderLayout.CENTER);
      
        //Login Window
        LoggedIn loggedIn = new LoggedIn();
        Login loginWindow = new Login(userInfo, loggedIn, board); 

        
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Note here that when we add an action listener to the reset button, we define it as an
        // anonymous inner class that is an instance of ActionListener with its actionPerformed()
        // method overridden. When the button is pressed, actionPerformed() will be called.
        
        // Reset button 
        final JButton reset = new JButton("Reset"); 
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.reset();
            }
        });
        
        final JButton instructions = new JButton("How to Play"); 
        instructions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, 
                        "How to Play Connect 4:\n \n \n"
                        + "Connect-4 is a game that involves tactical mastery.\n"
                        + "Many have claimed it is a game for simpletons, \n"
                        + "but soon realized their blasphemous claims and came \n"
                        + "to repent their sins. You are embarking on a journey \n"
                        + "that has a long and cherished history. You may have heard \n"
                        + "of Chess, Connect-4s sibling game that was created for those \n"
                        + "who did not possess the mental capacity to handle Connect-4. Be \n"
                        + "advised, Connect-4 is no chess, it requires utmost focus \n"
                        + "and application of opening theory as well as endgame \n"
                        + "mastery. \n \n"
                        + ""
                        + "The user that is logged in will be denoted player 1, and \n "
                        + "the other player 2. The status text at the bottom of the screen \n"
                        + "indicates whose turn it is. The obejct of the game is \n"
                        + "to place your pieces such that you can have four in a row, \n"
                        + "whether it be horizontally, verticaly, or diagonally. You must \n"
                        + "finesse your opponent, or be finessed. You cannot place pieces \n"
                        + "in slots where there is no piece below, unless you are placing \n"
                        + "on the bottom row. You cannot place a piece where there is \n"
                        + "already one. Whoever places four pieces in a row first...wins!\n"
                        + "May the best of luck be with you.\n\n"
                        + "To quote Dimetri Martin\n"
                        + "\"I was on the train the other day, and I heard somebody say,\n "
                        + "I'm really good at [chess]\n"
                        + "That's the same thing as saying, \n"
                        + "I'm not good at Connect-4.\"\n", "Instructions",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        control_panel.add(reset);
        
    
        control_panel.add(undo, BorderLayout.NORTH);
        
        control_panel.add(instructions);
        
        control_panel.add(userInfo); 

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Start the game
        board.reset();
        
        
        //Prevents game from displaying until user has successfully logged in. 
        new Thread() {

            @Override
            public void run() {
                synchronized (loggedIn) {
                    try {
                        loggedIn.wait(); 
                         
                        if (loggedIn.getLoggedIn()) {
                            loginWindow.setVisible(false); 
                            loginWindow.dispose(); 
                            
                            JFrame interludeFrame = new JFrame();
                            final JPanel interludeImagePanel = new InterludeWindow(); 
                            final JLabel gameLoadText = new JLabel("Please wait "
                                    + "while game loads..."); 
                            
                            
                            JPanel interludeGridOrganizer = new JPanel();
                            interludeGridOrganizer.setLayout(
                                    new BoxLayout(interludeGridOrganizer, BoxLayout.PAGE_AXIS));
                            
                            interludeGridOrganizer.add(gameLoadText);
                            interludeGridOrganizer.add(interludeImagePanel); 
                            
                            interludeFrame.add(interludeGridOrganizer); 
                            
                            interludeFrame.setLocation(400, 200); 
                            interludeFrame.pack();
                            interludeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            interludeFrame.setVisible(true);

                            
                            Thread.sleep(5000);
                            

                            interludeFrame.dispose(); 
                            
                            frame.setVisible(true);
                        } else {
                            System.err.println("ERROR: Failed To Start Game");
                        }
                    } catch (InterruptedException e) {
                        
                    }
                }
                
            }
            
        }.start();
    
    }

    /**
     * Main method run to start and run the game. Initializes the GUI elements specified in Game and
     * runs it. 
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}