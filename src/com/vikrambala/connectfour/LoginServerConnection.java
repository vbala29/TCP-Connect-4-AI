package com.vikrambala.connectfour;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.vikrambala.connectfour.error.ErrorHandler;


/**
 * Each instance of this class is used to make request/connection to the login server.
 * The login server is used solely for logging in, creating accounts, and requesting
 * information about win/loss counts for a specific account. 
 * @author vikrambala
 */
public class LoginServerConnection {
    
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    
    public LoginServerConnection() {
        try {
            socket = new Socket("127.0.0.1", 31313);
            System.out.println("Connected to server!");
            
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(202);
            closeConnections();
        } 
        
    }

    /**
     * Closes connection to server
     */
    private void closeConnections() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Request to create a new account with given credentials.
     * @param username of the account to create
     * @param password of the account to create
     * @return serverReponse. 0 if CALM, 200 if SERVER_ERROR, 301 if INVALID_CREDENTIAL, 
     * 302 is USERNAME_EXISTS, 202 if SERVER_TIMEOUT
     */
    public int createRequest(String username, String password) {
        String message = "create " + username + ", " + password; 

        String serverMessage;
        try {
            out.println(message);
            
            if (!in.hasNextLine()) {
                Thread.sleep(100);
                if (!in.hasNextLine()) {
                    return 202;
                } else {
                    serverMessage = in.nextLine();
                    System.out.println("Recieved message from server: " + serverMessage); 
                }
            }  else {
                serverMessage = in.nextLine();
                System.out.println("Recieved near timeout message from server: " + serverMessage); 
            }
        } catch (Exception e) {
            return 200;
        }
        closeConnections();
        return Integer.parseInt(serverMessage);
    }
    
    /**
     * Request for the information of a user with given credentials. Used to 
     * check if a user exists when they try to login. 
     * @param username
     * @param password
     * @return information of about the user if they exist. 
     * Returns array with index 2 of 200 if SERVER_ERROR, 202 if SERVER_TIMEOUT.
     */
    public int[] userInfoRequest(String username, String password) {
        String message = "login: " + username + ", " + password; 
        String serverMessage = "";
        int[] userInfo = null;
        try {
            out.println(message);
            if (!in.hasNextLine()) {
                Thread.sleep(500);
                if (!in.hasNextLine()) {
                    return new int[]{-1, 0, 202};
                } else {
                    serverMessage = in.nextLine();
                    System.out.println("Recieved message from server: " + serverMessage); 
                }
            }  else {
                serverMessage = in.nextLine();
                System.out.println("Recieved near timeout message from server: " + serverMessage); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new int[]{-1, 0, 200};
        }
        
        int lastIndex = 0;
        int occurences = -1; 
        
        while (lastIndex != -1) {
            lastIndex = serverMessage.indexOf(",", lastIndex + 1);
            occurences ++; 
        }
         
        if (occurences == 1) {
            userInfo = new int[2];
            int index1 = serverMessage.indexOf(",");
            userInfo[0] = Integer.parseInt(serverMessage.substring(0, index1));
            userInfo[1] = Integer.parseInt(serverMessage.substring(index1 + 2)); 
        } else if (occurences == 2) {
            userInfo = new int[3];
            int index1 = serverMessage.indexOf(",");
            userInfo[0] = Integer.parseInt(serverMessage.substring(0, index1));
            int index2 = serverMessage.indexOf(",", index1 + 1);
            userInfo[1] = Integer.parseInt(serverMessage.substring(index1 + 2, index2)); 
            userInfo[2] = Integer.parseInt(serverMessage.substring(index2 + 2)); 
        }
        closeConnections();
        return userInfo;
    }

    /**
     * Used to get the updated information of a user after the game has been ended and been 
     * restarted. Used by GameBoard to update the win/loss label at the top of the game.
     * @param username
     * @return an array with the updated user info. 
     * Returns array with index 2 of 200 if SERVER_ERROR, 202 if SERVER_TIMEOUT, 
     * 301 if INVALID_CREDENTIAL
     */
    public int[] updatedUserLabelInfo(String username) {
        String message = "labelInfo: " + username;
        String serverMessage = "";
        int[] userInfo = null;
        try {
            out.println(message);
            if (!in.hasNextLine()) {
                Thread.sleep(500);
                if (!in.hasNextLine()) {
                    return new int[]{-1, 0, 202};
                } else {
                    serverMessage = in.nextLine();
                    System.out.println("Recieved message from server: " + serverMessage); 
                }
            }  else {
                serverMessage = in.nextLine();
                System.out.println("Recieved near timeout message from server: " + serverMessage); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new int[]{-1, 0, 200};
        }
        
        int lastIndex = 0;
        int occurences = -1; 
        while (lastIndex != -1) {
            lastIndex = serverMessage.indexOf(",", lastIndex + 1);
            occurences ++; 
        }
         
        if (occurences == 1) {
            userInfo = new int[2];
            int index1 = serverMessage.indexOf(",");
            userInfo[0] = Integer.parseInt(serverMessage.substring(0, index1));
            userInfo[1] = Integer.parseInt(serverMessage.substring(index1 + 2)); 
        } else if (occurences == 2) {
            userInfo = new int[3];
            int index1 = serverMessage.indexOf(",");
            userInfo[0] = Integer.parseInt(serverMessage.substring(0, index1));
            int index2 = serverMessage.indexOf(",", index1 + 1);
            userInfo[1] = Integer.parseInt(serverMessage.substring(index1 + 2, index2)); 
            userInfo[2] = Integer.parseInt(serverMessage.substring(index2 + 2)); 
        }
        closeConnections();
        return userInfo;
    }
    
    /**
     * Used to add a win to the user's account after a game has been won. 
     * Errors can be 202 -> SERVER_TIMEOUT or 200 -> SERVER_ERROR
     * @param username
     */
    public void updateWin(String username) {
        String message = "win: " + username; 
        String serverMessage = "";
        try {
            out.println(message);
            if (!in.hasNextLine()) {
                Thread.sleep(500);
                if (!in.hasNextLine()) {
                    ErrorHandler.errorHandler(202);
                } else {
                    serverMessage = in.nextLine();
                    System.out.println("Recieved message from server: " + serverMessage); 
                }
            }  else {
                serverMessage = in.nextLine();
                System.out.println("Recieved near timeout message from server: " + serverMessage); 
            }
        } catch (Exception e) {
            ErrorHandler.errorHandler(200);
        }
        
        ErrorHandler.errorHandler(Integer.parseInt(serverMessage));
        closeConnections();
    }
    
    /**
     * Used to add a loss to the user's account after a game has been lost. 
     * Errors can be 202 -> SERVER_TIMEOUT or 200 -> SERVER_ERROR
     * @param username
     */
    public void updateLoss(String username) {
        String message = "loss: " + username; 
        String serverMessage = "";
        try {
            out.println(message);
            if (!in.hasNextLine()) {
                Thread.sleep(500);
                if (!in.hasNextLine()) {
                    ErrorHandler.errorHandler(202);
                } else {
                    serverMessage = in.nextLine();
                    System.out.println("Recieved message from server: " + serverMessage); 
                }
            }  else {
                serverMessage = in.nextLine();
                System.out.println("Recieved near timeout message from server: " + serverMessage); 
            }
        } catch (Exception e) {
            ErrorHandler.errorHandler(200);
        }
        
        ErrorHandler.errorHandler(Integer.parseInt(serverMessage));
        closeConnections();
    }
    

}
