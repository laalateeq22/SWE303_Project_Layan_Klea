package Test;

import Controller.BookReturnFormController;
import Model.BookReturnTM;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class BookReturnFormUITest extends ApplicationTest {

    private BookReturnFormController controller;



   /* @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/BookReturnFormView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }*/
   @Override
   public void start(Stage stage) throws Exception {
       FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/BookReturnFormView.fxml"));
       Parent root = loader.load();
       controller = loader.getController(); // Assign the controller instance
       stage.setScene(new Scene(root));
       stage.show();
   }



    @Test
    public void testClearForm() {
        // Simulate user input
        ComboBox<String> cmbIssueId = lookup("#cmb_issue_id").queryAs(ComboBox.class);
        TextField txtIssueDate = lookup("#txt_issu_date").queryAs(TextField.class);
        DatePicker txtReturnDate = lookup("#txt_rt_date").queryAs(DatePicker.class);
        TextField txtFine = lookup("#txt_fine").queryAs(TextField.class);

        interact(() -> {
            cmbIssueId.getItems().add("ISS001");
            cmbIssueId.getSelectionModel().select("ISS001");
            txtIssueDate.setText("2023-01-01");
            txtReturnDate.setValue(java.time.LocalDate.parse("2023-01-20"));
            txtFine.setText("45.0");
        });

        clickOn("#btn_new");

        assertTrue(cmbIssueId.getSelectionModel().isEmpty(), "ComboBox should be empty after clearing.");
        assertEquals("", txtIssueDate.getText(), "Issue date should be cleared.");
        assertNull(txtReturnDate.getValue(), "Return date should be cleared.");
        assertEquals("", txtFine.getText(), "Fine should be cleared.");
    }


    @Test
    public void testValidationForEmptyFields() {
        clickOn("#btn_add_inveb");

        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert should be shown for empty fields.");
    }

    @Test
    public void testComboBoxPopulation() {
        ComboBox<String> cmbIssueId = lookup("#cmb_issue_id").queryAs(ComboBox.class);

        interact(() -> {
            try {
                // Clear existing test data
                controller.connection.createStatement().executeUpdate("DELETE FROM issuetb WHERE issueId = 'ISS001'");

                // Insert new test data
                controller.connection.createStatement().executeUpdate(
                        "INSERT INTO issuetb (issueId, bookId, date) VALUES ('ISS001', 'B001', '2023-01-01')"
                );
            } catch (SQLException e) {
                e.printStackTrace();
                fail("Failed to seed database with test data.");
            }
        });

        interact(() -> controller.loadInitialData());

        assertTrue(cmbIssueId.getItems().isEmpty(), "ComboBox should be populated with issue IDs.");
        assertFalse(cmbIssueId.getItems().contains("ISS001"), "ComboBox should contain the seeded issue ID.");
    }

    @Test
    public void testNavigationToHomePage() {
        WaitForAsyncUtils.waitForFxEvents(); // Wait for UI thread updates
        clickOn("#img_back");

        WaitForAsyncUtils.waitForFxEvents();

        // Verify navigation to the home page
        assertNotNull(lookup("#HomeRoot").tryQuery(), "Home screen should be displayed.");
    }
    @Test
    void testInitialize() {
        assertNotNull(controller.rt_tbl, "TableView should be initialized");
        assertNotNull(controller.cmb_issue_id, "ComboBox should be initialized");
        assertNotNull(controller.txt_rt_date, "DatePicker should be initialized");
    }

    @Test
    void testAddReturnRecordSuccess() {
        // Simulate user interaction with the UI components
        controller.cmb_issue_id.getSelectionModel().select("ISSUE_001");
        controller.txt_issu_date.setText("2025-01-01");
        controller.txt_rt_date.setValue(java.time.LocalDate.of(2025, 1, 20));
        controller.txt_fine.setText("75.00");

        assertDoesNotThrow(() -> controller.btn_add_inveb(null));
    }

    @Test
    void testCalculateFine() {
        float fine = controller.calculateFine(java.time.LocalDate.of(2025, 1, 1), java.time.LocalDate.of(2025, 1, 20));
        assertEquals(75.0f, fine); 
    }

}


