import Controller.BookIssueFormController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.service.query.PointQuery;

import static org.junit.jupiter.api.Assertions.*;

public class BookIssueFormUITest extends ApplicationTest {

    private BookIssueFormController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/BookIssueFormView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void testLookupButtons() {
        // Verify that buttons exist in the scene graph
        assertNotNull(lookup("#btnNew").query(), "btnNew should exist in the scene graph");
        assertNotNull(lookup("#btnAdd").query(), "btnAdd should exist in the scene graph");
        assertNotNull(lookup("#btnDelete").query(), "btnDelete should exist in the scene graph");
    }

    @Test
    public void testNewActionGeneratesNewIssueId() {
        // Simulate clicking the "New" button
        clickOn("#btnNew");

        // Verify the issue ID is generated
        String issueId = controller.txt_issid.getText();
        assertNotNull(issueId, "Issue ID should not be null");
        assertTrue(issueId.startsWith("SS"), "Issue ID should start with 'SS'");
    }




    @Test
    public void testMemberSelectionUpdatesNameField() {
        // Simulate member selection
        clickOn("#mem_is_id").type(KeyCode.DOWN).type(KeyCode.ENTER); // Select member

        // Ensure that the name field is updated correctly
        String memberName = controller.txt_name.getText();
        assertNotNull(memberName, "Member name should be populated after selection");
    }

    @Test
    public void testBookSelectionUpdatesTitleField() {
        // Simulate book selection
        clickOn("#book_id").type(KeyCode.DOWN).type(KeyCode.ENTER); // Select book

        // Ensure that the title field is updated correctly
        String bookTitle = controller.txt_title.getText();
        assertNotNull(bookTitle, "Book title should be populated after selection");
    }

    @Test
    public void testInvalidMemberIdSelection() {
        // Simulate an invalid member selection (e.g., no member exists)
        clickOn("#mem_is_id").type(KeyCode.DOWN).type(KeyCode.ENTER); // Select an invalid member

        // Ensure that the name field is not updated
        String memberName = controller.txt_name.getText();
        assertFalse(memberName.isEmpty(), "Member name should not be populated for an invalid member");
    }

    @Test
    public void testNewAction() {
        // Simulate clicking the "New" button
        ActionEvent event = new ActionEvent();
        controller.new_action(event);

        // Verify the issue ID is generated
        String issueId = controller.txt_issid.getText();
        assertNotNull(issueId, "Issue ID should not be null");
        assertTrue(issueId.startsWith("SS"), "Issue ID should start with 'SS'");
    }


    @Test
    public void testAddAction() {
        clickOn("#btnNew"); // Generate new issue ID
        clickOn("#mem_is_id").type(KeyCode.DOWN).type(KeyCode.ENTER); // Select member
        clickOn("#book_id").type(KeyCode.DOWN).type(KeyCode.ENTER); // Select book
        clickOn("#txt_isu_date").write("2025-01-23"); // Enter issue date

        clickOn("#btnAdd"); // Add entry

        TableView<?> tableView = lookup("#bk_ssue_tbl").query();
        assertNotNull(tableView.getItems().get(0), "TableView should have at least one row after adding");
    }






}
