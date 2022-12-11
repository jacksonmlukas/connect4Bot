import java.io.Serializable;
import java.util.Arrays;

public class State implements Serializable {

    // Define the game board
    public int[][] board;

    // Define a constructor for the State class
    public State(int[][] board) {
        // Copy the board array into the board property of the State object
        this.board = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
    }

    // Implement the hashCode() method
    @Override
    public int hashCode() {
        // Compute the hash code of the State object by combining the hash codes of each element in the board array
        return Arrays.deepHashCode(board);
    }

    // Implement the equals() method
    @Override
    public boolean equals(Object obj) {
        // Check if the obj argument is an instance of State
        if (obj instanceof State other) {
            // Cast the obj argument to a State object

            // Return true if the board arrays of this State object and the other State object are equal
            return Arrays.deepEquals(board, other.board);
        }

        // Return false if the obj argument is not an instance of State
        return false;
    }
}
