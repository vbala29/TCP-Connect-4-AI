package com.vikrambala.connectfour;


import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class is a model for Connect4. (MODEL)
 * @author vikrambala
 */
public class ConnectFour {

    private int[][] board;
    private int numTurns;
    private boolean player1; //Player 2 is the AI. 
    private boolean gameOver;
    private boolean firstMove;
    
    //A stack used to store the previous game moves. See undoTurn();
    private Stack<GameMove> gameMoves = new Stack<GameMove>(); 
    
    public static final int BOARD_SQUARES = 42; 
    
    /**
     * Constructor sets up game state.
     */
    public ConnectFour() {
        reset();
    }
    
    protected boolean getGameOver() {
        return gameOver;
    }
    
    protected int[][] getBoard() {
        return board; 
    }
    
    protected int rowForColumn(int c) {
        for (int i = 5; i >= 0; i--) {
            if (board[i][c] == 0) {
                return i;
            }
        }
        
        return -1;
    }
    
    protected boolean playerOneTurn() {
        return player1;
    }
   /**
    * This method allows for moves to be undone
    * as long as no one has won the game or no move has been made as yet. 
    * @return a boolean indicating whether or not a move was undone.
    */
    protected boolean undoTurn() {
        if (!gameOver && !gameMoves.isEmpty()) {
            GameMove g = gameMoves.pop(); 
            board[g.getRow()][g.getColumn()] = 0; 
            numTurns --; 
            player1 = !player1;
            return true; 
        } else {
            return false; 
        }
    }

    /**
     * playTurn allows players to play a turn. Returns true 
     * if the move is successful and false if a player tries
     * to play in a location that is taken or after the game
     * has ended. If the turn is successful and the game has 
     * not ended, the player is changed. If the turn is 
     * unsuccessful or the game has ended, the player is not 
     * changed. The parameter undo allows for this method to be used
     * to undo previous game moves that were stored in a collection. 
     * 
     * @param c column to play in
     * @param r row to play in
     * @param undo whether or not the last move is to be undone
     * @return whether the turn was successful
     */
    protected boolean playTurn(int c, int r) {
        if (board[r][c] != 0 || gameOver) {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocation(300, 100);
            frame.setAlwaysOnTop(true);
            JOptionPane.showMessageDialog(frame, "Sqaure already contains piece, please choose"
                    + " another sqaure", "Illegal Move", 
                    JOptionPane.ERROR_MESSAGE); 
            return false;
        }
        
        if (r != board.length - 1) {
            if (board[r + 1][c] == 0) { //Separated to prevent outOfBounds exception
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocation(300, 100);
                frame.setAlwaysOnTop(true);
                JOptionPane.showMessageDialog(frame, "Cannot place piece above empty sqaure,"
                        + " please choose another sqaure", "Illegal Move", 
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

        }
        
        
        if (player1) {
            board[r][c] = 1;
            gameMoves.push(new GameMove(r, c));
        } else {
            board[r][c] = 2;
            gameMoves.push(new GameMove(r, c));
        }

        numTurns++;
        if (checkWinner() == 0) {
            player1 = !player1;
        }
        return true;
    }
    
    
    protected int[][] flipBoard(int[][] a) {
        int[][] flipped = new int[a[0].length][a.length];
        
        for (int row = 0; row < a.length; row ++) {
            for (int col = 0; col < a[row].length; col++) {
                flipped[col][row] = a[row][col]; 
            }
        }
        
        return flipped;
    }
    
    private int[] horizontalWinner(Collection<Integer> l) {
        
        Iterator<Integer> iter = l.iterator();
        
        int zeroCount = 0; // can only increase, used for determining ties
        int oneCount = 0; //resets when the chain is broken
        int twoCount = 0; //resets when the chain is broken
        while (iter.hasNext()) {
            int currentInt = iter.next();
           
            if (currentInt == 0) {
                zeroCount ++; 
                oneCount = 0;
                twoCount = 0;
            } else {
                if (currentInt == 1) {
                    twoCount = 0;
                    oneCount ++;
                } else if (currentInt == 2) {
                    oneCount = 0;
                    twoCount ++;
                } 
            }
            
            if (oneCount >= 4) {
                return new int[] {1, zeroCount};
            } else if (twoCount >= 4) {
                return new int[] {2, zeroCount}; 
            }
        }
        
        return new int[] {0, zeroCount}; 
        
    }
    
    private int arraySum(int[] a) {
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            c += a[i];
        }
        
        return c;
    }
    
    
    private int checkDiagonalDown(int[][] b, int[] a, int count, int row, int col) {
        if (count >= 4) {
            return 0; 
        } else if (count == 0) {
            a[count] = b[row][col]; 
            count ++; 
        } else {
            a[count] = b[row][col]; 
            
            if (a[count] != a[count - 1]) {
                return 0; 
            }
            
            count ++; 
        }
        
        
        if (arraySum(a) == 4 && a[0] != 2) {
            return 1; 
        } else if (arraySum(a) == 8) {
            return 2; 
        } else if (row + 1 < b.length && col + 1 < b[row].length) {
            return checkDiagonalDown(b, a, count, row + 1, col + 1); 
        } else {
            return 0; 
        }
    }
    
    private int checkDiagonalUp(int[][] b, int[] a, int count, int row, int col) {
        if (count >= 4) {
            return 0;
        } else if (count == 0) {
            a[count] = b[row][col]; 
            count ++; 
        } else {
            a[count] = b[row][col]; 
            
            if (a[count] != a[count - 1]) {
                return 0; 
            }
            
            count ++; 
        }
        
        
        if (arraySum(a) == 4 && a[0] != 2) {
            return 1; 
        } else if (arraySum(a) == 8) {
            return 2; 
        } else if (row - 1 > 0  && col + 1 < b[row].length) {
            return checkDiagonalUp(b, a, count, row - 1, col + 1); 
        } else {
            return 0; 
        }
    }
    

    /**
     * checkWinner checks whether the game has reached a win 
     * condition. looks for horizontal and 
     * 
     * @return 0 if nobody has won yet, 1 if player 1 has won, and
     *            2 if player 2 has won, 3 if the game hits stalemate
     */
    public int checkWinner() {
        int totalZeroCount = 0;

    
        // Check horizontal win
        for (int i = 0; i < board.length; i++) {
            Collection<Integer> rowCheck = new LinkedList<>();
            for (int j = 0; j < board[i].length; j++) {
                rowCheck.add(board[i][j]); 
            }
            
            int[] winner = horizontalWinner(rowCheck); 
            totalZeroCount += winner[1];  //used at end of method to determine stalemate
            
            
            if (winner[0] == 1) {
                gameOver = true;
                return 1;
            } else if (winner[0] == 2) {
                gameOver = true;
                return 2;
            } 
        } 
        
        // Check vertical win
        int[][] flippedBoard = flipBoard(board); 
        
        for (int i = 0; i < flippedBoard.length; i++) {
            Collection<Integer> columnCheck = new LinkedList<>();
            for (int j = 0; j < flippedBoard[i].length; j++) {
                columnCheck.add(flippedBoard[i][j]); 
            }
            
            int[] winner = horizontalWinner(columnCheck); 
            
            if (winner[0] == 1) {
                gameOver = true;
                return 1;
            } else if (winner[0] == 2) {
                gameOver = true;
                return 2;
            } 
        } 
        
        //Check Diagonal Win (using Recursive Functions) 
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int up = checkDiagonalUp(board, new int[4], 0, i, j);
                int down = checkDiagonalDown(board, new int[4], 0, i, j);
                if (up == 1 || down == 1) {
                    gameOver = true;
                    return 1;
                } else if (up == 2 || down  == 2) {
                    gameOver = true;
                    return 2;
                }
            }
        }
        
        //Prevents from tie being detected before firstMove; 
        if (firstMove) {
            firstMove = false;
            totalZeroCount = 1; //non-zero 
        }
        
        
        //Check for stalemates (every grid square is filled & no wins_
        if (totalZeroCount == 0) {
            gameOver = true;
            return 3;
        } else {
            return 0;
        }
        
        
    }
    
    

    /**
     * printGameState prints the current game state
     * for debugging.
     */
    public void printGameState(int[][] board) {
        System.out.println("\n\nTurn " + (numTurns + 1) + ":\n");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j]);
                if (j < 7) { 
                    System.out.print(" | "); 
                }
            }
            if (i < 6) {
                System.out.println("\n---------"); 
            }
        }
    }
    
    /**
     * reset (re-)sets the game state to start a new game.
     */
    public void reset() {
        board = new int[6][7];
        numTurns = 0;
        player1 = true;
        gameOver = false;
        firstMove = true;
    }
    
    /**
     * getCurrentPlayer is a getter for the player
     * whose turn it is in the game.
     * 
     * @return true if it's Player 1's turn,
     * false if it's Player 2's turn.
     */
    public boolean getCurrentPlayer() {
        return player1;
    }
    
    /**
     * WARNING WARNING WARNING
     * ONLY TO BE USED FOR TESTING PURPOSES
     * @param a the array to set the board equal to
     * @return
     */
    public void setGame(int[][] a) {
        this.board = Arrays.copyOf(a, 6);
    }
    
    /**
     * WARNING WARNING WARNING
     * ONLY TO BE USED FOR TESTING PURPOSES
     * @return
     */
    public int[][] getGame() {
        return Arrays.copyOf(this.board, 6); 
    }
    
    /**
     * WARNING WARNING WARNING
     * ONLY TO BE USED FOR TESTING PURPOSES
     * @param i the player whose turn it will be
     * @return
     */
    public void setTurn(int i) {
        if (i == 1) {
            player1 = true;
        } else { 
            player1 = false;
        }
    }
    
    
    /**
     * getCell is a getter for the contents of the
     * cell specified by the method arguments.
     * 
     * @param c column to retrieve
     * @param r row to retrieve
     * @return an integer denoting the contents
     *         of the corresponding cell on the 
     *         game board.  0 = empty, 1 = Player 1,
     *         2 = Player 2
     */
    public int getCell(int c, int r) {
        return board[r][c];
    }
    
 
    public static void main(String[] args) {
        System.out.println("Unimplemented Main Method");
    }
}
