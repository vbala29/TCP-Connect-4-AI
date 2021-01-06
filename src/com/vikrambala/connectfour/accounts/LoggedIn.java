package com.vikrambala.connectfour.accounts;

/**
 * Wrapper for the boolean value loggedIn; Allows for synchronized access of boolean value,
 * to determine whether game window should be displayed or not. 
 * @author vikrambala
 *
 */
final public class LoggedIn {

    private volatile boolean loggedIn;
    
    public LoggedIn() {
        loggedIn = false;
    }
    
    public boolean getLoggedIn() {
        return loggedIn;
    }
    
    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
