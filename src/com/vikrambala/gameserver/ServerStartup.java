package com.vikrambala.gameserver;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Starts up login and game servers
 * @author vikrambala
 *
 */

public final class ServerStartup {
    
    public static void main(String[] args) {
        final LoginServerBackend loginServer = new LoginServerBackend();
        new Thread(loginServer, "Login Server").start(); 
        
        final JFrame frame = new JFrame("Login Server"); 
        JLabel loginMessage = new JLabel("Login Server");
        frame.add(loginMessage);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(500, 100));
        frame.pack(); 
        frame.setVisible(true);

    }
    
    private ServerStartup() {
        
    }

}
