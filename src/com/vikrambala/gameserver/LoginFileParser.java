package com.vikrambala.gameserver;

import java.io.*;
import java.util.Scanner;

/**
 * This class is login file parser, which can be used
 * to create new user accounts, as well as retrieve data from existing accounts.
 * @author vikrambala
 *
 */

class LoginFileParser {
    
    File loginFile;
    
    
    public LoginFileParser() {
        try {
            loginFile = new File("logininfo.txt");
            if (!loginFile.exists()) {
                loginFile.createNewFile(); 
            }
        } catch (IOException i) {
            //Error will be clear to user once they try to login/create account
            i.printStackTrace();
        } 
        
    }
    
    
    /**
     * Looks for the username/password combination specified in the file, 
     * and returns the win/loss count of the user if the combination is valid.
     * Else a win count of negative one is returned in the array, signaling
     * that no user has been found with this combination of credentials. 
     * @param username that the user entered in the text field
     * @param password that the user entered in the text field 
     * @return an array with user win/loss information. 
     * Returns an array with index 0 having element -1 if no user is found with 
     * argument credentials. Returns array with index 2 of 200 if SERVER_ERROR occurred. 
     */
    public int[] findUser(String username, String password) {
        FileReader f = null;
        BufferedReader br; 
        try {
            f = new FileReader(loginFile);
        } catch (FileNotFoundException e) {
            return new int[] {-1, 0, 200}; 
        }
        
        if (!(isValidCredential(username) == 0) || !(isValidCredential(password) == 0)) {
            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new int[] {-1, 0}; 
        }
        
        try {
            br = new BufferedReader(f); 
            
            String line = br.readLine();
            while (line != null) {
                int a = line.indexOf(" "); 
                String usernameInFile = line.substring(0, a); 
                
                int b = line.indexOf(" ", a + 1); 
                String passwordInFile = line.substring(a + 1, b); 
                
                if (usernameInFile.equals(username) && passwordInFile.equals(password)) {
                    int c = line.indexOf(" ", b + 1);
                    String wins = line.substring(b + 1, c);
                    
                    String losses = line.substring(c + 1); 
                    br.close();
                    return new int[] {Integer.parseInt(wins), Integer.parseInt(losses)}; 
                } else {
                    line = br.readLine(); 
                }
            }
            
            br.close(); 
            return new int[] {-1, 0}; 
        } catch (IOException ix) {
            ix.printStackTrace();
            return new int[] {-1, 0, 200}; 
        } 
    }
    
    /**
     * Used when creating a new account to prevent duplicate usernames. 
     * Checks to see if a user name already exists.
     * @param username to look for
     * @return a int indicating whether the username is avaliable. 
     * Returns 0 for available. 301 for INVALID_CREDENTIAL, 200 for SERVER_ERROR, 302 for 
     * USERNAME_EXISTS
     */
    private int findUser(String username) {
        FileReader f = null;
        BufferedReader br; 
        try {
            f = new FileReader(loginFile);
            if (!(isValidCredential(username) == 0)) {
                f.close(); 
                return 301;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 200;
        }
        
        try {
            br = new BufferedReader(f); 
            
            String line = br.readLine();
            while (line != null) {
                int a = line.indexOf(" "); 
                String usernameInFile = line.substring(0, a); 

                if (usernameInFile.equals(username)) {
                    br.close(); 
                    return 302; 
                } else {
                    line = br.readLine(); 
                }
            }
            
            br.close(); 
            return 0;  
        } catch (IOException ix) {
            ix.printStackTrace();
            return 200;
        } 
    }
    
    /**
     * Used to update the user info label on the GameBoard if the user chooses to play a new 
     * game after winning or losing.
     * @param username of the user who wants to continue playing a new game
     * @return the win/loss information of this user after their previous game.
     * Returns array with index 2 of 200 if SERVER_ERROR, 301 if INVALID_CREDENTIAL,
     */
    public int[] updatedUserLabelInfo(String username) {
        FileReader f = null;
        BufferedReader br; 
        try {
            f = new FileReader(loginFile);
        } catch (FileNotFoundException e) {
            return new int[] {-1, 0, 200};
        }
        
        if (isValidCredential(username) == 301) {
            try {
                f.close();
            } catch (IOException e) {
                return new int[] {-1, 0, 301}; 
            }
        }
        
        try {
            br = new BufferedReader(f); 
            
            String line = br.readLine();
            while (line != null) {
                int a = line.indexOf(" "); 
                String usernameInFile = line.substring(0, a); 
                
                int b = line.indexOf(" ", a + 1); //Password index, but we don't care about it here
                
                if (usernameInFile.equals(username)) {
                    int c = line.indexOf(" ", b + 1);
                    String wins = line.substring(b + 1, c);
                    
                    String losses = line.substring(c + 1); 
                    br.close(); 
                    return new int[] {Integer.parseInt(wins), Integer.parseInt(losses)}; 
                } else {
                    line = br.readLine(); 
                }
            }
            
            br.close(); 
            return new int[] {-1, 0}; 
        } catch (IOException ix) {
            ix.printStackTrace();
            return new int[] {-1, 0, 200}; 
        } 
    }
    
    /**
     * Determines whether or not a given username/password is only alphanumeric,
     * contains no white spaces, and is no more than 15 characters in length. 
     * @param s the credential to be analyzed
     * @return whether or not the credential string was valid under the criteria.
     * Returns 301 if INVALID_CREDENTIAL, 0 if CALM
     */
    public int isValidCredential(String s) {
        if (s.length() > 15 || s.isEmpty() || s == null) { 
            return 301;
        }
        
        //Checks for whitespace, non alphanumeric characters
        for (char c : s.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return 301;
            }
        }
        
        return 0;
    }
    
    /**
     * Creates a user with the specified username and password. If an account
     * with the username parameter already exists, false is returned
     * and no new account is created. Username and password cannot be more than
     * 10 characters long. 
     * @param username of the account to create
     * @param password of the account to create
     * @return a boolean: whether or not the account was successfully created. 
     * Returns 0 if CALM, 200 if SERVER_ERROR, 301 if INVALID_CREDENTIAL,
     * 302 if USERNAME_EXISTS.
     */
    public int createUser(String username, String password) {
        FileWriter f = null;
        BufferedWriter bw; 
        try {
            f = new FileWriter(loginFile, true);
        } catch (IOException ix) {
            return 200;
        }
        
        
        if (!(isValidCredential(username) == 0) || !(isValidCredential(password) == 0)) {
            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 301;
        }
        
      //If username already exists, do not create a new one with same username
        int findUser = findUser(username);
        if (findUser != 0) {
            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return findUser;
        } 
        
        try {
            bw = new BufferedWriter(f); 
            
            bw.write(username + " " + password + " " + 0 + " " + 0);
            bw.newLine();
            bw.flush();
            bw.close(); 
            return 0;
        } catch (IOException ix) {
            ix.printStackTrace();
            return 200;
        } 
    }
    
    /**
     * Adds a win to the win tally of the given user. Returns false if an IOException
     * occurs and the user's tally is not updated. 
     * @param username of the user who won
     * @return whether or not the user info file was updated correctly. 
     * Returns 200 if SERVER_ERROR, 0 if CALM
     */
    public int updateWin(String username) {
        FileWriter fw = null;
        FileReader fr = null; 
        BufferedWriter bw; 
        BufferedReader br;

        
        try {
            fr = new FileReader(loginFile);
            br = new BufferedReader(fr);

            String lineToCopy = br.readLine();
            String fileContents = "";
            while (lineToCopy != null) {
                fileContents += lineToCopy + "\n"; 
                lineToCopy = br.readLine(); 
            }
            Scanner sc = new Scanner(fileContents); 
            

            fw = new FileWriter(loginFile);
            bw = new BufferedWriter(fw); 
            while (sc.hasNext()) {
                String line = sc.nextLine(); 
                
                int a = line.indexOf(" "); 
                String usernameInFile = line.substring(0, a); 
                
                int b = line.indexOf(" ", a + 1); 
                String passwordInFile = line.substring(a + 1, b); 
                
                String wins = null; 
                String losses = null;
               
                
                if (usernameInFile.equals(username)) {
                    int c = line.indexOf(" ", b + 1);
                    wins = line.substring(b + 1, c);
                    
                    losses = line.substring(c + 1);
                    
                    
                    bw.write(usernameInFile + " " + passwordInFile + " " + 
                                (Integer.parseInt(wins) + 1) 
                            + " " + losses);
                    bw.newLine();
                    bw.flush(); 
                    
                    line = br.readLine();
                    
                } else {
                    int c = line.indexOf(" ", b + 1);
                    wins = line.substring(b + 1, c);
                    
                    losses = line.substring(c + 1);
                    
                    bw.write(usernameInFile + " " + passwordInFile + " " + wins + " " + losses);
                    bw.newLine();
                    bw.flush(); 
                    line = br.readLine(); 
                }
            }
            br.close();
            bw.close();
            sc.close(); 
        } catch (IOException ix) {
            ix.printStackTrace();
            return 200;
        } 
        
        return 0;
    }
    
    /**
     * Adds a loss to the loss tally of the given user. Returns false if an IOException
     * occurs and the user's tally is not updated. 
     * @param username of the user who wlost
     * @return whether or not the user info file was updated correctly. 
     * Returns 200 if SERVER_ERROR, 0 if CALM
     */
    public int updateLoss(String username) {
        FileWriter fw = null;
        FileReader fr = null; 
        BufferedWriter bw; 
        BufferedReader br;

        try {
            fr = new FileReader(loginFile);
            br = new BufferedReader(fr); 
          
            
            String lineToCopy = br.readLine();
            String fileContents = "";
            while (lineToCopy != null) {
                fileContents += lineToCopy + "\n"; 
                lineToCopy = br.readLine(); 
            }
            Scanner sc = new Scanner(fileContents); 
            
            
            fw = new FileWriter(loginFile);
            bw = new BufferedWriter(fw); 
            while (sc.hasNext()) {
                String line = sc.nextLine(); 
                
                int a = line.indexOf(" "); 
                String usernameInFile = line.substring(0, a); 
                
                int b = line.indexOf(" ", a + 1); 
                String passwordInFile = line.substring(a + 1, b); 
                
                String wins = null; 
                String losses = null;
               
                
                if (usernameInFile.equals(username)) {
                    int c = line.indexOf(" ", b + 1);
                    wins = line.substring(b + 1, c);
                    
                    losses = line.substring(c + 1);
       
                    bw.write(usernameInFile + " " + passwordInFile + " " + Integer.parseInt(wins) 
                            + " " + (Integer.parseInt(losses) + 1));
                    bw.newLine();
                    bw.flush(); 
                    
                    line = br.readLine();
                    
                } else {
                    int c = line.indexOf(" ", b + 1);
                    wins = line.substring(b + 1, c);
                    
                    losses = line.substring(c + 1);
       
                    bw.write(usernameInFile + " " + passwordInFile + " " + wins + " " + losses);
                    bw.newLine();
                    bw.flush(); 
                    line = br.readLine(); 
                }
            }
            br.close();
            bw.close();
            sc.close(); 
        } catch (IOException ix) {
            ix.printStackTrace();
            return 200;
        } 
        
        return 0;
    }
    
    
   
}
