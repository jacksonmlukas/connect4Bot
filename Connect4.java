import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Connect4 {
    // Define the dimensions of the game board
    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final double LEARNING_RATE = 0.5;
    public static final double DISCOUNT_FACTOR = 0.9;
    public static final double EXPLORATION_RATE = 0.1;
    public static Scanner scanner = new Scanner(System.in);

    // Define the initial state of the game board
    public static int[][] board = new int[ROWS][COLS];

    // Define the Q-table
    public static QTable Q = new QTable();

    // Define the main method
    public static void main(String[] args) {
        // Load the Q-table from a file
        try (FileInputStream fileIn = new FileInputStream("qtable.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Q = (QTable) in.readObject();
        } catch (Exception e) {
            // If there is an error loading the Q-table, create a new one
            Q = new QTable();
        }

        // Train the AI by playing 100000 games
        for (int i = 0; i < 1000; i++) {
            qLearning(board, 1, 20, Q, -1);
            resetBoard();
            if (Q == null) {
                System.out.println(i);
            }
        }

        // Save the Q-table to a file
        try (FileOutputStream fileOut = new FileOutputStream("qtable.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(Q);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Play a game against the AI
        System.out.println("Let's play Connect 4!");
        while (!isGameOver(board)) {
            // Print the game board
            printBoard();

            // Get the column from the user
            int col = getInput(1) - 1;

            // Update the game board with the user's action and switch players
            board = updateBoard(board, col, 1);

            // Check if the game is over
            if (isGameOver(board)) {
                // Print the game board
                printBoard();

                // Print the result of the game
                if (getReward(board, 1) == 1) {
                    System.out.println("You win!");
                } else if (getReward(board, 1) == -1) {
                    System.out.println("You lose!");
                } else {
                    System.out.println("It's a draw!");
                }
            } else {
                // Get the next action from the AI
                int action = getNextAction(board, -1);

                // Update the game board with the AI's action and switch players
                if (action != -1) {
                    board = updateBoard(board, action, -1);
                }
            }
        }
    }


    public static int isWin(int[][] board, int player) {
        // Check each column for a winning move
        for (int col = 0; col < COLS; col++) {
            // Check if the current column is a valid move
            if (isValidMove(board, col)) {
                // Create a temporary board with the move applied
                int[][] tempBoard = updateBoard(board, col, player);

                // Check if the player has won with the current move
                if (isGameOver(tempBoard)) {
                    // Return the column where the player has won
                    return col;
                }
            }
        }

        // Return -1 if the player is not about to win
        return -1;
    }


    public static int getNextAction(int[][] board, int player) {
        // Check if the game is over or the board is full
        if (isGameOver(board) || isFull(board)) {
            return -1;
        }

        // Check if the player is about to win
        int playerWin = isWin(board, player);
        if (playerWin != -1) {
            // Return the column where the player will win
            return playerWin;
        }

        // Check if the opponent is about to win
        int opponentWin = isWin(board, -player);
        if (opponentWin != -1) {
            // Return the column where the opponent can be blocked
            return opponentWin;
        }

        // Choose an action based on the Q-table
        int action;
        if (Math.random() < EXPLORATION_RATE) {
            // Explore: choose a random action
            do {
                action = (int) (Math.random() * COLS);
            } while (!isValidMove(board, action));
        } else {
            // Exploit: choose the best action according to the Q-table
            action = getBestAction(board, player, Q);
        }

        // Return the chosen action
        return action;
    }

    public static int getBestAction(int[][] board, int player, QTable Q) {
        // Create a list of valid actions
        ArrayList<Integer> actions = new ArrayList<>();
        for (int col = 0; col < COLS; col++) {
            if (isValidMove(board, col)) {
                actions.add(col);
            }
        }

        // Check if there are no valid actions
        if (actions.size() == 0) {
            return -1;
        }

        // Check if there is only one valid action
        if (actions.size() == 1) {
            return actions.get(0);
        }

        // Initialize the best action and value
        int bestAction = actions.get(0);
        double bestValue = Double.NEGATIVE_INFINITY;

        // Loop through the valid actions and find the best one
        for (int action : actions) {
            // Get the next state for the current action
            int[][] nextState = updateBoard(board, action, player);

            // Get the value of the current action
            double value = Q.get(nextState, player);

            // Update the best action and value if necessary
            if (value > bestValue) {
                bestAction = action;
                bestValue = value;
            }
        }

        // Return the best action
        return bestAction;
    }






    public static boolean isFull(int[][] board) {
        // Check if there are any empty cells in the game board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 0) {
                    // There is at least one empty cell, so the board is not full
                    return false;
                }
            }
        }

        // There are no empty cells, so the board is full
        return true;
    }


    // Define the Q-learning algorithm
    // Define the Q-learning algorithm
    // Define the Q-learning algorithm
    public static void qLearning(int[][] board, int player, int depth, QTable Q, int action) {
        // Check if the game is over or the maximum depth has been reached
        if (isGameOver(board) || depth == 0) {
            // Get the reward for the current player
            double reward = getReward(board, player);

            // Check if the current state is in the Q-table
            State currentState = new State(board);
            if (!Q.containsKey(currentState)) {
                Q.put(currentState, new ActionValues());
            }

            // Check if the current action is in the ActionValues map
            if (!Q.get(currentState).containsKey(action)) {
                Q.get(currentState).put(action, 0.0);
            }

            // Find the maximum Q-value for the next state
            double maxQ = Double.NEGATIVE_INFINITY;
            for (int nextAction = 0; nextAction < COLS; nextAction++) {
                if (isValidMove(board, nextAction)) {
                    int[][] nextBoard = updateBoard(board, nextAction, player);
                    State nextState = new State(nextBoard);
                    if (Q.containsKey(nextState) && Q.get(nextState).containsKey(nextAction)) {
                        maxQ = Math.max(maxQ, Q.get(nextState).get(nextAction));
                    }
                }
            }

            // Update the Q-value for the current state and action
            double qValue = Q.get(currentState).get(action);
            double updatedQ = qValue + LEARNING_RATE * (reward + DISCOUNT_FACTOR * maxQ - qValue);
            Q.get(currentState).put(action, updatedQ);

            return;
        }

        // Loop through each column to find a valid move
        for (int nextAction = 0; nextAction < COLS; nextAction++) {
            if (isValidMove(board, nextAction)) {
                // Update the game board with the next action and switch players
                int[][] nextBoard = updateBoard(board, nextAction, player);

                // Recursively call the qLearning() method with the updated board, player, depth, and action
                qLearning(nextBoard, -player, depth - 1, Q, nextAction);
            }
        }
    }







    public static boolean isValidMove(int[][] board, int col) {
        // Check if the chosen column is not full

        return (col > 0 && col < 8 && board[0][col - 1] == 0);
    }




    // Define a function to reset the game board
    public static void resetBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                board[row][col] = 0;
            }
        }
    }

    // Define a function to check if the game is over
    // Define a function to check if the game is over
    public static boolean isGameOver(int[][] board) {
        // Check if the game board is full
        if (isFull(board)) {
            return true;
        }

        // Check if there is a winner
        return isWinner(board, 1) || isWinner(board, -1);

        // The game is not over
    }



    // Define a function to print the game board
    public static void printBoard() {
        // Print the game board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 1) {
                    System.out.print("X ");
                } else if (board[row][col] == -1) {
                    System.out.print("O ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }




    // Define a function to get the player's input
    public static int getInput(int player) {
        int col = -1;
        while (!isValidMove(board, col)) {
            System.out.println("Player " + player + ", choose a column (1-7): ");
            col = scanner.nextInt();
        }
        return col;
    }


    // Define a function to update the game board
    public static int[][] updateBoard(int[][] board, int col, int player) {
        // Loop through each row in the specified column
        for (int row = ROWS - 1; row >= 0; row--) {
            // Check if the cell is empty
            if (board[row][col] == 0) {
                // Update the cell with the player's move
                board[row][col] = player;

                // Return the updated game board
                return board;
            }
        }
        // Return the original game board if no empty cells were found
        return board;
    }




    // Define a function to get the reward for a given game board and player
    public static int getReward(int[][] board, int player) {
        // Check if there is a winner
        if (isWinner(board, player)) {
            // If the specified player is the winner, return 1
            return 1;
        } else if (isWinner(board, -player)) {
            // If the other player is the winner, return -1
            return -1;
        } else {
            // If there is no winner, return 0
            return 0;
        }
    }

    public static boolean isWinner(int[][] board, int player) {
        // Check for a horizontal win
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                if (board[row][col] == player && board[row][col + 1] == player && board[row][col + 2] == player && board[row][col + 3] == player) {
                    return true;
                }
            }
        }

        // Check for a vertical win
        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == player && board[row + 1][col] == player && board[row + 2][col] == player && board[row + 3][col] == player) {
                    return true;
                }
            }
        }

        // Check for a diagonal win (top-left to bottom-right)
        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                if (board[row][col] == player && board[row + 1][col + 1] == player && board[row + 2][col + 2] == player && board[row + 3][col + 3] == player) {
                    return true;
                }
            }
        }

        // Check for a diagonal win (bottom-left to top-right)
        for (int row = 3; row < ROWS; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                if (board[row][col] == player && board[row - 1][col + 1] == player && board[row - 2][col + 2] == player && board[row - 3][col + 3] == player) {
                    return true;
                }
            }
        }

        // There is no winner
        return false;
    }


}