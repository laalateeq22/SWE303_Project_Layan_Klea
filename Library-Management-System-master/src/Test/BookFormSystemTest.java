package Test;

import Model.BookTM;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class BookFormSystemTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the main application UI and show the stage
        new Main().start(stage); // Use Main as the entry point
        stage.show();           // Ensure the stage is displayed
    }

    @Test
    public void testAddBookThroughUI() throws Exception {
        displayActiveThreads();
        Thread thread = new Thread(() -> {
            try {
                // Click on the 'books' section
                clickOn("#books");
                Thread.sleep(1000);

                // Simulate user input for adding a book
                clickOn("#btn_new");
                clickOn("#txt_bk_id").eraseText(5).write("B002").push();
                clickOn("#txt_bk_title").write("TestFX").push();
                clickOn("#txt_bk_auth").write("Jane Doe").push();

                // Click the "Add" button
                clickOn("#btn_add");

                // Verify that the book appears in the table
                verifyThat("#tbl_bk", hasTableCell("B002"));
                verifyThat("#tbl_bk", hasTableCell("TestFX"));
                verifyThat("#tbl_bk", hasTableCell("Jane Doe"));
                verifyThat("#tbl_bk", hasTableCell("Available"));

                clickOn("OK"); // Assuming alert confirmation

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join(); // Ensures the main test thread waits for the spawned thread
    }

    public void displayActiveThreads() {
        System.out.println("\n=== Active Threads ===");
        Thread.getAllStackTraces().keySet().forEach(thread ->
                System.out.println("Thread Name: " + thread.getName() + ", State: " + thread.getState())
        );
        System.out.println("======================\n");
    }



    @Test
    public void testUpdateBookThroughUI() throws Exception {
        displayActiveThreads();
        Thread thread = new Thread(() -> {
            try {
                // Click on the 'books' section
                clickOn("#books");

                // Wait for navigation to complete
                Thread.sleep(1000);

                // Simulate user input for adding a book
                clickOn("#btn_new");
                clickOn("#txt_bk_id").eraseText(5).write("B201").push();
                clickOn("#txt_bk_title").write("TestFX").push();
                clickOn("#txt_bk_auth").write("Jane Doe").push();

                // Click the "Add" button
                clickOn("#btn_add");

                clickOn("OK");
                Thread.sleep(1000);
                // Verify the book is added successfully
                verifyThat("#tbl_bk", hasTableCell("B201"));
                verifyThat("#tbl_bk", hasTableCell("TestFX"));
                verifyThat("#tbl_bk", hasTableCell("Jane Doe"));
                verifyThat("#tbl_bk", hasTableCell("Available"));

                // Step 2: Select the book in the table
                TableView<BookTM> table = lookup("#tbl_bk").queryTableView();
                BookTM targetBook = table.getItems().stream()
                        .filter(book -> "B201".equals(book.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Book not found in table"));

                // Select the book programmatically
                Platform.runLater(() -> table.getSelectionModel().select(targetBook));
                Thread.sleep(1000);

                // Step 3: Update the details
                clickOn("#txt_bk_title").eraseText(6).write("Updated Title");
                clickOn("#txt_bk_auth").eraseText(8).write("Updated Author");
                clickOn("#btn_add"); // Assuming there's an "Update" button with fx:id "btn_update"

                // Wait for the table to refresh and verify the updated details
                Thread.sleep(1000);
                verifyThat("#tbl_bk", hasTableCell("B201"));
                verifyThat("#tbl_bk", hasTableCell("Updated Title"));
                verifyThat("#tbl_bk", hasTableCell("Updated Author"));
                verifyThat("#tbl_bk", hasTableCell("Available"));

                clickOn("OK");
                Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join(); // Ensures the main test thread waits for the spawned thread
    }

    @Test
    public void testDeleteBookThroughUI() throws Exception {
        displayActiveThreads();
        Thread thread = new Thread(() -> {
            try {

                clickOn("#books");

                // Wait for navigation to complete
                Thread.sleep(1000);

                // Simulate user input for adding a book
                clickOn("#btn_new");
                clickOn("#txt_bk_id").eraseText(5).write("B202").push();
                clickOn("#txt_bk_title").write("TestFX").push();
                clickOn("#txt_bk_auth").write("Jane Doe").push();

                // Click the "Add" button
                clickOn("#btn_add");

                // Handle the alert
                clickOn("OK");
                Thread.sleep(1000);

                // Verify the book is added successfully
                verifyThat("#tbl_bk", hasTableCell("B202"));
                verifyThat("#tbl_bk", hasTableCell("TestFX"));
                verifyThat("#tbl_bk", hasTableCell("Jane Doe"));
                verifyThat("#tbl_bk", hasTableCell("Available"));

                // Step 2: Select the book in the table
                TableView<BookTM> table = lookup("#tbl_bk").queryTableView();
                BookTM targetBook = table.getItems().stream()
                        .filter(book -> "B202".equals(book.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Book not found in table"));

                // Select the book programmatically
                Platform.runLater(() -> table.getSelectionModel().select(targetBook));
                Thread.sleep(1000);

                // Step 3: Delete the selected book
                clickOn("#btn_dlt"); // Delete button
                Thread.sleep(1000); // Wait for the table to refresh

                // Step 4: Verify the book is no longer in the table
                // Get the list of items in the table
                List<BookTM> items = table.getItems();

                // Verify that the book with ID "B202" is no longer in the list
                boolean isBookDeleted = items.stream().noneMatch(book -> "B202".equals(book.getId()));
                assertFalse(isBookDeleted, "The book with ID B202 was not deleted.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join(); // Ensures the main test thread waits for the spawned thread
    }


}





