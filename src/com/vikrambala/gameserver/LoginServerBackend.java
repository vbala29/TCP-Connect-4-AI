package com.vikrambala.gameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class contains the code for the backend login server. It uses 
 * a connection worker inner class to create new connections with users, and 
 * runs each user's connection with the server on a separate thread via the 
 * ExecutorService. 
 * @author vikrambala
 *
 */

public class LoginServerBackend implements Runnable {
    
    
    private volatile ServerSocket serverSocket;
    private final Map<String, Socket> openSockets;
    private LoginFileParser fileParser = new LoginFileParser();
    
    private volatile boolean running;


    public LoginServerBackend() {
        serverSocket = null;
        openSockets = Collections.synchronizedMap(new HashMap<String, Socket>());
        running = false;
    }
    
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        running = true;
       
        
        try {
            serverSocket = new ServerSocket(31313);
        } catch (IOException iox) {
            iox.printStackTrace();
            running = false;
            serverSocket = null;
        }
        
        // Await new connections on the current thread
        ExecutorService workerPool = Executors.newCachedThreadPool();
        try {
            while (isRunning() && !serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                workerPool.execute(new ConnectionWorker(clientSocket));
            }
        } catch (IOException iox) {
            iox.printStackTrace();
        } finally {
            running = false;
            workerPool.shutdown();
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException iox) {
                iox.printStackTrace();
            } finally {
                serverSocket = null;
            }

            synchronized (openSockets) {
                Iterator<Socket> iterator = openSockets.values().iterator();
                while (iterator.hasNext()) {
                    Socket clientSocket = iterator.next();
                    try {
                        clientSocket.close();
                    } catch (IOException iox) {
                        iox.printStackTrace();
                    } finally {
                        iterator.remove();
                    }
                }
            }
        }
        

        
    }
    
    //==========================================================================
    // ConnectionWorker
    //==========================================================================

    private final class ConnectionWorker implements Runnable {
        private String username;
        private final Socket clientSocket;
        PrintWriter pw;

        public ConnectionWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
            username = null;
            try {
                pw = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()))
            ) {
                while (isRunning() && !clientSocket.isClosed() && (username == null)) {
                    String line = reader.readLine();
                    
                    if (line != null) {
                        System.out.printf("Request received: " +
                                "\"%s\"\n", line);
                        String tempUsername;
                        String tempPassword;
                        
                        if (line.startsWith("login")) {
                            int index = line.indexOf(" ");
                            int index2 = line.indexOf(",");
                            tempUsername = line.substring(index + 1, index2);
                            tempPassword = line.substring(index2 + 2); 
                            int[] userInfo = fileParser.findUser(tempUsername, tempPassword);
                            
                            if (!(userInfo[0] == -1)) {
                                openSockets.put(tempUsername, clientSocket);
                                this.username = tempUsername;
                                pw.println(arrayResponse(userInfo)); 
                                System.out.println("Sent " + arrayResponse(userInfo) + " to " 
                                        + this.username);
                                break; 
                            } 
                            
                            pw.println(arrayResponse(userInfo)); 
                            break; 
                            
                        } else if (line.startsWith("labelInfo")) { 
                            //For requests when game restarts
                            int index = line.indexOf(" ");
                            tempUsername = line.substring(index + 1);
                            int[] userInfo = fileParser.updatedUserLabelInfo(tempUsername);
                            
                            if (!(userInfo[0] == -1)) {
                                openSockets.put(tempUsername, clientSocket);
                                this.username = tempUsername;
                                pw.println(arrayResponse(userInfo)); 
                                System.out.println("Sent " + arrayResponse(userInfo) + " to " 
                                        + this.username);
                                break; 
                            } 
                            
                            pw.println(arrayResponse(userInfo)); 
                            break; 
                            
                        } else if (line.startsWith("create")) {
                            int index1 = line.indexOf(" ");
                            int index2 = line.indexOf(",");
                            tempUsername = line.substring(index1 + 1, index2);
                            tempPassword = line.substring(index2 + 2); 
                            
                            int fileResponse = fileParser.createUser(tempUsername, tempPassword);
                            if (fileResponse == 0) {
                                openSockets.put(tempUsername, clientSocket);
                                pw.println(fileResponse);
                                this.username = tempUsername;
                                System.out.println("Sent " + fileResponse + " to " + this.username);
                                break; 
                            }
                            
                            pw.println(fileResponse); 
                            break;
                        } else if (line.startsWith("win")) {
                            int index = line.indexOf(" ");
                            tempUsername = line.substring(index + 1);
                            
                            int fileResponse = fileParser.updateWin(tempUsername); 
                            if (fileResponse == 0) {
                                openSockets.put(tempUsername, clientSocket);
                                pw.println(fileResponse);
                                this.username = tempUsername;
                                System.out.println("Sent " + fileResponse + " to " + this.username);
                                break; 
                            }
                            
                            pw.println(fileResponse); 
                            break;
                        } else if (line.startsWith("loss")) {
                            int index = line.indexOf(" ");
                            tempUsername = line.substring(index + 1);
                            
                            int fileResponse = fileParser.updateLoss(tempUsername); 
                            if (fileResponse == 0) {
                                openSockets.put(tempUsername, clientSocket);
                                pw.println(fileResponse);
                                this.username = tempUsername;
                                System.out.println("Sent " + fileResponse + " to " + this.username);
                                break; 
                            }
                            
                            pw.println(fileResponse); 
                            break;
                        }
                    }
                }
                
                pw.close();
                clientSocket.close(); 
            } catch (IOException iox) {
                iox.printStackTrace();
                openSockets.remove(username);
            } finally {
                pw.close();
                openSockets.remove(username);
            }
        }
        
        private String arrayResponse(int[] userInfo) {
            String formatted = "";
            for (int i = 0; i < userInfo.length; i++) {
                formatted = formatted.concat(Integer.toString(userInfo[i]));
                if (i != (userInfo.length - 1)) {
                    formatted = formatted.concat(", ");
                }
            }
            
            return formatted; 
        }
    }

    
}
