import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;

public class SystemTest extends ApplicationTest {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the HomeFormView.fxml directly and start the application
        Parent root = FXMLLoader.load(getClass().getResource("/View/HomeFormView.fxml"));
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Test
    public void testBookSearchAndReturnToMainPage() {
        // Step 1: Navigate to Book Search
        clickOn("#bk_search"); // Assuming the ID of the ImageView is bk_search

        // Step 2: Verify BookSearchForm loaded
        TextField searchField = lookup("#bk_sch").queryAs(TextField.class);
        assertNotNull(searchField, "Search field should be present in the BookSearchForm.");

        TableView<?> tableView = lookup("#tbl_bk").queryAs(TableView.class);
        assertNotNull(tableView, "TableView should be present in the BookSearchForm.");

        // Step 3: Perform a book search using write for typing simulation
        write("B001"); // Simulate typing "Test Book" character by character
        press(javafx.scene.input.KeyCode.ENTER); // Simulate pressing the "Enter" key

        // Step 4: Verify results in the TableView
        assertTrue(
                tableView.getItems().stream().anyMatch(item -> item.toString().contains("Test Book")),
                "The search results should contain books matching 'Test Book'."
        );

        // Step 5: Navigate back to the main page
        clickOn("#img_bk"); // Assuming the back button has ID img_back

        // Step 6: Verify HomeForm is displayed
        ImageView homeMemberIcon = lookup("#member").queryAs(ImageView.class);
        assertNotNull(homeMemberIcon, "Member icon should be present on the HomeForm.");
    }
}
