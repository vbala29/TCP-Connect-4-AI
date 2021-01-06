package com.vikrambala.connectfour;

/**
 * This class is used to store previous gameMoves. Each move is stored in one 
 * GameMove object. See the ConnectFour class for use of this class. 
 * @author vikrambala
 *
 */

public class GameMove {
    
    private final int r;
    private final int c; 
    
    public GameMove(int r, int c) {
        if (r > 6 || r < 0 || c > 7 || c < 0) {
            this.r = -1;
            this.c = -1;
        } else {
            this.r = r;
            this.c = c;
        }
    }
    
    public int getRow() {
        return r;
    }
    
    public int getColumn() {
        return c;
    }

}
