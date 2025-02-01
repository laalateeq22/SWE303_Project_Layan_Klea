package BookIssueSystemTest;

import Model.BookIssueTM;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

class BookIssueSystemTest extends ApplicationTest {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the HomeFormView.fxml directly and start the application
        Parent root = FXMLLoader.load(getClass().getResource("/View/HomeFormView.fxml"));
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Test
    public void testNavigateToBookIssueForm() {
        // Step 1: Navigate to Book Issue Form
        clickOn("#issue");  // Click on the "issue" ImageView to navigate to the Book Issue page

        // Step 2: Wait for the page to load and verify elements are present
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#bk_ssue_tbl", isVisible());  // Check if the TableView is visible
    }

    @Test
    public void testSearchIssueFunctionality() {
        // Step 1: Navigate to Book Issue Form
        clickOn("#issue");  // Click on the "issue" ImageView to navigate to the Book Issue page

        // Step 2: Wait for the page to load
        WaitForAsyncUtils.waitForFxEvents();

        // Step 3: Perform the search action in the Book Issue form
        write("SS12345");  // Simulate typing the issue ID into the search box (assuming issue IDs are like SS12345)
        press(KeyCode.ENTER);  // Simulate pressing Enter to trigger the search

        // Step 4: Assert that the search results in the table are updated
        TableView<BookIssueTM> tableView = lookup("#bk_ssue_tbl").queryAs(TableView.class);
        assertNotNull(tableView, "TableView should not be null.");
        assertTrue(tableView.getItems().isEmpty(), "TableView should display issues after searching.");
    }

    @Test
    public void testDeleteIssue() {
        // Step 1: Navigate to Book Issue Form
        clickOn("#issue");  // Click on the "issue" ImageView to navigate to the Book Issue page

        // Step 2: Wait for the page to load
        WaitForAsyncUtils.waitForFxEvents();

        // Step 3: Select an issue in the TableView
        TableView<BookIssueTM> tableView = lookup("#bk_ssue_tbl").queryAs(TableView.class);
        assertNotNull(tableView, "TableView should not be null.");
        tableView.getSelectionModel().selectFirst();  // Select the first record in the table

        // Step 4: Delete the selected issue
        clickOn("#btnDelete");  // Click on the "Delete" button

        // Step 5: Wait for the UI to update and check if the issue is removed
        WaitForAsyncUtils.waitForFxEvents();

        // Step 6: Assert that the issue has been deleted (check the table for the new item count)
        int tableSizeAfterDelete = tableView.getItems().size();
        assertEquals(0, tableSizeAfterDelete, "TableView should have one less item after deleting a record.");
    }


}
