package com.vikrambala.connectfour;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class GameAI {
    
    //AI is maximizer, 100 is max. 
    private ConnectFour cfour;
    
    
    public GameAI(ConnectFour cfour) {
        this.cfour = cfour;
    }
    
    private boolean isTerminalNode(int[][] board) {
        return checkWinner(board) != 0;
    }
    
    public int evaluateWindow(int[] window) {
        int score = 0;
        int oppScore = 0;
        for (int n : window) {
            if (n == 1) {
                oppScore += 1;
            } else if (n == 2) {
                score += 1;
            } 
        }
        
        if (score == 0 && oppScore == 3) {
            return -10;
        } else if (oppScore == 0 && score == 3) {
            return 10;
        } else if (score + oppScore == 4) {
            return 0;
        } else if (oppScore == 0 && score == 2) {
            return 5; 
        } else if (oppScore == 0 && score == 1) {
            return 2; 
        } 
        
        return 0;
    }
    
    public int evaluatePosition(int[][] board) {
        int score = 0; 
        for (int row = 0; row < 6; row++) {
            for (int i = 0; i <= 3; i++) {
                int[] fourInRow = new int[] {board[row][i], board[row][i + 1], 
                        board[row][i + 2], board[row][i + 3]};
                score += evaluateWindow(fourInRow); 
            }
        }
        
        int[][] flippedBoard = cfour.flipBoard(board);
        for (int row = 0; row < 7; row++) {
            for (int i = 0; i <= 2; i++) {
                int[] fourInRow = new int[] {flippedBoard[row][i], flippedBoard[row][i + 1], 
                        flippedBoard[row][i + 2], flippedBoard[row][i + 3]};
                score += evaluateWindow(fourInRow); 
            }
        }
        
        for (int row = 5; row > 2; row--) {
            for (int col = 0; col < 4; col++) {
                int[] diagLeft = new int[] {board[row][col], board[row - 1][col + 1],
                        board[row - 2][col + 2], board[row - 3][col + 3]};
                score += evaluateWindow(diagLeft);
            }
        }
        
    
        for (int row = 5; row > 2; row--) {
            for (int col = 3; col < 7; col++) {
                int[] diagLeft = new int[] {board[row][col], board[row - 1][col - 1],
                        board[row - 2][col - 2], board[row - 3][col - 3]};
                score += evaluateWindow(diagLeft);
            }
        }
        

        return score; 
    }
    
    /**
     * Returns array where 1 signifies valid column, and 0 invalid.
     * @return an array with 1s and 0s to signify invalid/valid columns.
     */
    private int[] validColumns(int[][] board) {
        int[] validColumns = new int[7];
        for (int i = 0; i < 7; i++) {
            for (int j = 5; j >= 0; j--) {
                if (board[j][i] == 0) {
                    validColumns[i] = 1; 
                    break;
                }
            }
        }
        
        return validColumns; 
    }
    
    private void playTurn(int column, int[][] board, boolean aiTurn) {
        for (int i = 5; i >= 0; i--) {
            if (board[i][column] == 0) {
                board[i][column] = aiTurn ? 2 : 1;
                break; 
            }
        }
    }
    
    private int[][] deepCopy(int[][] arr) {
        int[][] deepCopy = new int[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                deepCopy[i][j] = arr[i][j];
            }
        }
        
        return deepCopy;
    }
    
    public int[] minimaxAlgo(int depth, int[][] board, boolean aiTurn) {
        boolean terminal = isTerminalNode(board);
        
        if (depth == 0) {
            return new int[] {-1, evaluatePosition(board)};
        } else if (terminal) {
            int winner = checkWinner(board);
            
            if (winner == 1) {
                return new int[]{-1, -100000};
            } else if (winner == 2) {
                return new int[]{-1, 100000};
            } else {
                System.out.println("Tie " + winner);
                cfour.printGameState(board);
                return new int[]{-1, 0};
            }
        } else if (aiTurn) {
            
            int score = Integer.MIN_VALUE;
            int column = 0;
            int[] validColumns = validColumns(board);
            
            for (int n = 0; n < validColumns.length; n++) {
                if (validColumns[n] != 0) {
                    int[][] tempBoard = deepCopy(board);
                    playTurn(n, tempBoard, true);
                    int tempScore = minimaxAlgo(depth - 1, tempBoard, !aiTurn)[1];
                    if (tempScore >= score) {
                        System.out.println("Score: " + tempScore + " for " + n);
                        column = n; 
                        score = tempScore; 
                    }
                }
            }
            
            return new int[]{column, score};
        } else {
            int score = Integer.MAX_VALUE;
            int column = 0;
            int[] validColumns = validColumns(board);
            
            for (int n = 0; n < validColumns.length; n++) {
                if (validColumns[n] != 0) {
                    int[][] tempBoard = deepCopy(board);
                    playTurn(n, tempBoard, false);
                    int tempScore = minimaxAlgo(depth - 1, tempBoard, !aiTurn)[1];
                    if (tempScore <= score) {
                        System.out.println("Score: " + tempScore + " for " + n);
                        column = n; 
                        score = tempScore; 
                    }
                }
            }
            return new int[]{column, score};
        }
        
    } 
    
    
    private int[][] flipBoard(int[][] a) {
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
    public int checkWinner(int[][] board) {
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
                return 1;
            } else if (winner[0] == 2) {
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
                return 1;
            } else if (winner[0] == 2) {
                return 2;
            } 
        } 
        
        //Check Diagonal Win (using Recursive Functions) 
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int up = checkDiagonalUp(board, new int[4], 0, i, j);
                int down = checkDiagonalDown(board, new int[4], 0, i, j);
                if (up == 1 || down == 1) {
                    return 1;
                } else if (up == 2 || down  == 2) {
                    return 2;
                }
            }
        }
        //Check for stalemates (every grid square is filled & no wins_
        if (totalZeroCount == 0) {
            return 3;
        } else {
            return 0;
        }
        
        
    }
    
    
    
    

}
