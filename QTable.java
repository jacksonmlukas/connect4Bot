import java.io.Serializable;
import java.util.HashMap;

public class QTable extends HashMap<State, ActionValues> implements Serializable {
    // Implement the put() method
    @Override
    public ActionValues put(State state, ActionValues actionValues) {
        // Check if the Q-table already contains the given state
        if (this.containsKey(state)) {
            // Update the action values of the state in the Q-table
            this.get(state).putAll(actionValues);
        } else {
            // Add the given state and action values to the Q-table
            super.put(state, actionValues);
        }

        // Return the updated action values
        return this.get(state);
    }

    public double get(int[][] state, int action) {
        // Create a new State object from the given 2D state array
        State s = new State(state);

        // Check if the Q-value for the given state-action pair exists in the table
        if (this.containsKey(s) && this.get(s).containsKey(action)) {
            // Return the Q-value from the table
            return this.get(s).get(action);
        } else {
            // Return 0 if the Q-value does not exist in the table
            return 0;
        }
    }

}
