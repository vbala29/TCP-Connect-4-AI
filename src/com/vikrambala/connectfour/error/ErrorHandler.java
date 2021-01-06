package com.vikrambala.connectfour.error;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ErrorHandler {

    /**
     * Handles all non LoginFileIterator Errors. 
     * @param error
     */
    public static void errorHandler(ErrorCode error) {
        switch (error) {
            case IMAGE_RETRIVAL_ERROR:
                JFrame frame = new JFrame(); 
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocation(300, 100);
                frame.setAlwaysOnTop(true);
                JOptionPane.showMessageDialog(frame, "Image Retrival Error, Please"
                        + " Contact Administrator.", 
                        "Error 201", 
                        JOptionPane.ERROR_MESSAGE);
                break;
            case SERVER_ERROR:
                accountsError();
                break;
            case INVALID_CREDENTIAL:
                invalidCredentialError();
                break;
            case CALM:
                break;
            case USERNAME_EXISTS:
                break;
            case SERVER_TIMEOUT:
                break;
            default:
                break;
            
        }
    }
    
    public static void errorHandler(int errorCode) {
        switch (ErrorCode.valueOf(errorCode)) {
            case IMAGE_RETRIVAL_ERROR:
                JFrame frame = new JFrame(); 
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocation(300, 100);
                frame.setAlwaysOnTop(true);
                JOptionPane.showMessageDialog(frame, "Image Retrival Error, Please"
                        + " Contact Administrator.", 
                        "Error 201", 
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                break;
            case SERVER_ERROR:
                accountsError();
                break;
            case INVALID_CREDENTIAL:
                invalidCredentialError();
                break;
            case CALM:
                break;
            case USERNAME_EXISTS:
                break;
            case SERVER_TIMEOUT:
                serverTimeout();
                break;
            default:
                break;
            
        }
    }
    
    private static void invalidCredentialError() {
        JFrame frame = new JFrame(); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(300, 100);
        frame.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(frame, "Invalid credential. Credential should be"
                + " alphanumeric and no longer than 15 characters", "Error 301", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    private static void accountsError() {
        JFrame frame = new JFrame(); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(300, 100);
        frame.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(frame, "Error with accounts server, "
                + "please contact developer", "Error 200", 
                JOptionPane.ERROR_MESSAGE);
        System.exit(0); //Used after windows are closed to prevent new ones 
                        //from being created in Game
    }
    
    private static void serverTimeout() {
        JFrame frame = new JFrame(); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(300, 100);
        frame.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(frame, "Server Timed Out, "
                + "please contact developer", "Error 202", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    
}
