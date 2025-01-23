import Controller.BookSearchFormController;
import Model.BookTM;
import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookSearchFormControllerTest {

    private BookSearchFormController controller;

    @BeforeAll
    public static void initToolkit() {
        // Initialize JavaFX Toolkit
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void setUp() {
        controller = new BookSearchFormController();

        // Initialize JavaFX components
        controller.tbl_bk = new TableView<>();
        controller.bk_sch = new TextField();

        // Add columns to the TableView
        TableColumn<BookTM, String> idColumn = new TableColumn<>("id");
        TableColumn<BookTM, String> titleColumn = new TableColumn<>("title");
        TableColumn<BookTM, String> authorColumn = new TableColumn<>("author");
        TableColumn<BookTM, String> statusColumn = new TableColumn<>("status");

        controller.tbl_bk.getColumns().addAll(idColumn, titleColumn, authorColumn, statusColumn);

        // Initialize the controller
        controller.initialize();
    }

    @Test
    public void testSetupTableColumns() {
        assertEquals(4, controller.tbl_bk.getColumns().size(), "Table should have 4 columns initialized.");
        assertEquals("id", controller.tbl_bk.getColumns().get(0).getText(), "First column should be 'id'.");
        assertEquals("title", controller.tbl_bk.getColumns().get(1).getText(), "Second column should be 'title'.");
    }

    @Test
    public void testLoadAllBooks() {
        Platform.runLater(() -> controller.loadAllBooks());
        Platform.runLater(() -> {
            assertNotNull(controller.tbl_bk.getItems(), "Books should not be null.");
            assertFalse(controller.tbl_bk.getItems().isEmpty(), "Books should not be empty after loading.");
        });
    }
}
