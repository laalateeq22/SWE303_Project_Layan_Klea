package Test;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

/**
 * Utility class to initialize the JavaFX environment for testing purposes.
 */
public class JavaFXInitializer {

    public static boolean initialized = false;

    /**
     * Initializes the JavaFX runtime if it hasn't already been initialized.
     */
    public static void initialize() {
        if (!initialized) {
            // Create a JFXPanel to initialize the JavaFX runtime
            new JFXPanel();

            // Run the initialization on the JavaFX Application Thread
            Platform.setImplicitExit(false);
            initialized = true;
        }
    }
}
