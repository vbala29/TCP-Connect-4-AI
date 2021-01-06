package com.vikrambala.connectfour.accounts;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.vikrambala.connectfour.GameBoard;
import com.vikrambala.connectfour.LoginServerConnection;
import com.vikrambala.connectfour.error.ErrorHandler;

/**
 * This class is used to create a login popup window up application startup.
 * A user must login in order to be able to play the game. Thus, if the window 
 * is closed, the application terminates. There is synchronized access 
 * to the loggedIn object in this code, which allows for the Game class to know when
 * the user has successfully logged in and the board should be displayed. 
 * @author vikrambala
 *
 */

@SuppressWarnings("serial")
public class Login extends JFrame {
    
    private JPanel panel; 
    
    public Login(JLabel userInfo, LoggedIn loggedIn, GameBoard board) {
        
        setLocation(500, 300);
        
        panel = new JPanel(); 
        setTitle("Connect-4 Login"); 
        
        panel.setLayout(new GridLayout(4,1));
        
        JLabel status = new JLabel("Please enter your username and password"); 
        panel.add(status);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(2, 2));
        textPanel.add(new JLabel("Username"));
        JTextField username = new JTextField(10); 
        textPanel.add(username); 
        
        textPanel.add(new JLabel("Password")); 
        JTextField password = new JTextField(10); 
        textPanel.add(password); 
        
        
        panel.add(textPanel);
        
   
        JButton loginButton = new JButton("login");
        loginButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String user = username.getText();
                String pword = password.getText(); 
                
                int[] userInfoArray = new LoginServerConnection().userInfoRequest(user, pword); 
                if (userInfoArray[0] == -1 && userInfoArray.length == 3) {
                    if (userInfoArray[2] == 200) {
                        ErrorHandler.errorHandler(200);
                    } else if (userInfoArray[2] == 301) {
                        ErrorHandler.errorHandler(301);
                    } else if (userInfoArray[2] == 202) {
                        ErrorHandler.errorHandler(202);
                    }
                }
                
                if (userInfoArray[0] == -1) {
                    username.setText("");
                    password.setText("");
                    status.setText("Wrong username/password combo, try again.");
                    
                } else {
                    userInfo.setText("User: " + user + " Wins: " + userInfoArray[0] + " Losses: "
                             + userInfoArray[1]);
                    board.setUsername(user);
                    loggedIn.setLoggedIn(true);
                    
                    //Allows for game window to be shown in Game class. 
                    synchronized (loggedIn) {
                        loggedIn.notifyAll(); 
                    }

                }
                
            }
            
        });
        
        panel.add(loginButton); 
        
        JButton createButton = new JButton("createAccount");
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateAccount(userInfo, loggedIn, board); 
                dispose(); 
            }
               
        });
        panel.add(createButton); 
        
        this.add(panel); //Adds the login panel to the login frame.
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
  
        setFocusable(true);
        
    }
    
    
    

}
