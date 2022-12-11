import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ActionValues extends HashMap<Integer, Double> {
    public int maxKey() {
        // Set the initial maximum value to the lowest possible value
        double maxValue = Double.NEGATIVE_INFINITY;

        // Set the initial chosen action to -1
        int chosenAction = -1;

        // Loop through each action and value pair in the map
        for (Map.Entry<Integer, Double> entry : entrySet()) {
            // If the value is greater than the current maximum value, update the maximum value and the chosen action
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                chosenAction = entry.getKey();
            }
        }

        // Return the chosen action
        return chosenAction;
    }
}
