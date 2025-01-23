package Test;

import Controller.BookSearchFormController;
import Model.BookTM;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class BookSearchFormUITest extends ApplicationTest {

    private BookSearchFormController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/BookSearchFormView.fxml"));
        Parent root = loader.load();
        controller = loader.getController(); // Make sure this is called
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setup() {
        // Seed test data
        try (Connection connection = controller.connection) {
            connection.createStatement().executeUpdate("DELETE FROM bookdetail WHERE id = 'B001'");
            connection.createStatement().executeUpdate("INSERT INTO bookdetail (id, title, author, status) VALUES ('B001', 'Test Book', 'Test Author', 'Available')");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to set up test data.");
        }
    }

    @AfterEach
    public void tearDown() {
        // Cleanup test data
        try (Connection connection = controller.connection) {
            connection.createStatement().executeUpdate("DELETE FROM bookdetail WHERE id = 'B001'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTablePopulation() {
        TableView<BookTM> table = lookup("#tbl_bk").queryAs(TableView.class);

        // Wait for data to load
        WaitForAsyncUtils.waitForFxEvents();

        // Verify that the table contains the test data
        assertFalse(table.getItems().isEmpty(), "Table should not be empty.");
        assertTrue(table.getItems().stream().anyMatch(book -> book.getId().equals("B001")), "Table should contain the test book.");
    }

    @Test
    public void testSearchFunctionality() {
        TextField searchField = lookup("#bk_sch").queryAs(TextField.class);
        TableView<BookTM> table = lookup("#tbl_bk").queryAs(TableView.class);

        // Simulate typing in the search field
        interact(() -> searchField.setText("Test"));

        // Wait for search results to update
        WaitForAsyncUtils.waitForFxEvents();

        // Verify that the search results contain the test data
        assertFalse(table.getItems().isEmpty(), "Search results should not be empty.");
        assertTrue(table.getItems().stream().anyMatch(book -> book.getTitle().equals("Test Book")), "Search results should contain the test book.");
    }

    @Test
    public void testNavigationToHomePage() {
        // Simulate clicking on the back button
        clickOn("#img_bk");

        // Wait for the UI thread to update
        WaitForAsyncUtils.waitForFxEvents();

        // Verify that the home screen is displayed
        assertNotNull(lookup("#HomeRoot").tryQuery(), "Home screen should be displayed.");
    }
}
