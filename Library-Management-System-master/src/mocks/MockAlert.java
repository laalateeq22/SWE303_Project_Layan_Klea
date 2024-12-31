package mocks;
import javafx.scene.control.Alert;

public class MockAlert {
    private String capturedMessage;

    // Mock display alert method
    public void displayAlert(Alert alert) {
        capturedMessage = alert.getContentText();
    }

    // Get the captured message
    public String getCapturedMessage() {
        return capturedMessage;
    }

    // Clear the captured message for reuse
    public void clearCapturedMessage() {
        capturedMessage = null;
    }

}
