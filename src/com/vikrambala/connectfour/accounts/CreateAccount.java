package com.vikrambala.connectfour.accounts;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import com.vikrambala.connectfour.GameBoard;
import com.vikrambala.connectfour.LoginServerConnection;
import com.vikrambala.connectfour.error.ErrorHandler;

/**
 * Creates a window that allows a new user to create their account. 
 * Will automatically log the user in once they have created the account. 
 * @author vikrambala
 */


@SuppressWarnings("serial")
public class CreateAccount extends JFrame {
    
    private JPanel panel; 
    
    public CreateAccount(JLabel userInfo, LoggedIn loggedIn, GameBoard board) {
        
        setLocation(300, 300);
        
        panel = new JPanel(); 
        panel.setLayout(new GridLayout(3, 1));
        setTitle("Create an Account"); 
        
        JLabel status = new JLabel("Please enter your new username and password"); 
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
                
                int created = new LoginServerConnection().createRequest(user, pword); 
                if (created == 302 || created == 301) {
                    username.setText("");
                    password.setText("");
                    status.setText("Username not avaliable or invalid, try again.");
                } else if (created == 200) {
                    ErrorHandler.errorHandler(200);
                } else if (created == 202) {
                    ErrorHandler.errorHandler(202);
                } else {
                    userInfo.setText("User: " + user + " Wins: " + 0 + " Losses: "
                             + 0);
                    board.setUsername(user);
                    loggedIn.setLoggedIn(true);
                    
                    
                    //Allows for game window to be shown in Game class. 
                    synchronized (loggedIn) {
                        loggedIn.notifyAll(); 
                    }
                    
                    dispose(); 

                }
                
            }
            
        });
        
        panel.add(loginButton); 
        
        this.add(panel); //Adds the login panel to the login frame.
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
  
        setFocusable(true);

    }
    
}
