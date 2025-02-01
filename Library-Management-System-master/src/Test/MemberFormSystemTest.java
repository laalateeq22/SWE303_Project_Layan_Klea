package Test;

import Model.BookTM;
import Model.MemberTM;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.service.query.PointQuery;

import java.util.function.Supplier;

import java.util.List;

public class MemberFormSystemTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize the main application and show the stage
        new Main().start(stage);
        stage.show();
    }
    public void displayActiveThreads() {
        System.out.println("\n=== Active Threads ===");
        Thread.getAllStackTraces().keySet().forEach(thread ->
                System.out.println("Thread Name: " + thread.getName() + ", State: " + thread.getState())
        );
        System.out.println("======================\n");
    }
    @Test
    public void testAddMemberThroughUI() throws InterruptedException {
        displayActiveThreads();
        Thread thread = new Thread(() -> {
            try {

                        // Ensure the 'Member' section is visible and clickable
                clickOn("#member");

                // Wait for navigation to complete
                waitForFxEvents();

                // Verify that the button is visible before clicking
                verifyThat("#btn_new", NodeMatchers.isVisible());

                // Simulate user input for adding a member
                clickOn("#btn_new");
                clickOn("#mem_id").eraseText(5).write("12");
                clickOn("#mem_nme").write("Jane Doe");
                clickOn("#mem_addss").write("Tirana");
                clickOn("#mem_num").write("1234567890");

                // Click the "Add" button
                verifyThat("#btn_add", NodeMatchers.isVisible());
                clickOn("#btn_add");

                // Verify that the member appears in the table
                verifyThat("#mem_tbl", hasTableCell("12"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join(); // Ensures the main test thread waits for the spawned thread
    }

    @Test
    public void testUpdateMemberThroughUI() throws InterruptedException {
        displayActiveThreads();
        Thread thread = new Thread(() -> {
            try {
        clickOn("#member");


        Thread.sleep(1000);

        clickOn("#btn_new");
        clickOn("#mem_id").eraseText(5).write("15");
        clickOn("#mem_nme").write("Jane Doe");
        clickOn("#mem_addss").write("Tirana");
        clickOn("#mem_num").write("1234567890");

        // Click the "Add" button
        clickOn("#btn_add");
        clickOn("OK");

        Thread.sleep(1000);

        //Verify the member is added successfully
        verifyThat("#mem_tbl", hasTableCell("15"));
        verifyThat("#mem_tbl", hasTableCell("Jane Doe"));
        verifyThat("#mem_tbl", hasTableCell("Tirana"));
        verifyThat("#mem_tbl", hasTableCell("1234567890"));

        TableView<MemberTM> table = lookup("#mem_tbl").queryTableView();
        MemberTM targetMember = table.getItems().stream()
                .filter(member -> member.getId() == 15) // Match the specific member ID
                .findFirst()
                .orElseThrow(() -> new AssertionError("Member not found in the table"));

// Simulate clicking the row containing the target member
        int targetIndex = table.getItems().indexOf(targetMember);
        Platform.runLater(() -> table.getSelectionModel().select(targetIndex));
        Thread.sleep(1000);

        //Update the details
        clickOn("#mem_nme").eraseText(8).write("Updated Name");
        clickOn("#mem_addss").eraseText(6).write("Updated Address");
        clickOn("#mem_num").eraseText(10).write("0987654321");
        clickOn("#btn_add");

        // Wait for the table to refresh and verify the updated details
        Thread.sleep(1000); // Allow time for UI update
        verifyThat("#mem_tbl", hasTableCell("15"));
        verifyThat("#mem_tbl", hasTableCell("Updated Name"));
        verifyThat("#mem_tbl", hasTableCell("Updated Address"));
        verifyThat("#mem_tbl", hasTableCell("0987654321"));

        clickOn("OK"); // Handle alert dialog
        Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join(); // Ensures the main test thread waits for the spawned thread
    }


    @Test
    public void testDeleteBookThroughUI() throws InterruptedException {

        displayActiveThreads();
        Thread thread = new Thread(() -> {
            try {

        // Step 1: Navigate to the members section
        clickOn("#member");
        Thread.sleep(1000); // Wait for UI to load

        // Step 2: Add a new member
        clickOn("#btn_new");
        clickOn("#mem_id").eraseText(5).write("13");
        clickOn("#mem_nme").write("Jane Doe");
        clickOn("#mem_addss").write("Tirana");
        clickOn("#mem_num").write("1234567890");

        // Click the "Add" button
        clickOn("#btn_add");
        clickOn("OK");
        Thread.sleep(1000);

        // Verify the member is added successfully
        verifyThat("#mem_tbl", hasTableCell(13));
        verifyThat("#mem_tbl", hasTableCell("Jane Doe"));
        verifyThat("#mem_tbl", hasTableCell("Tirana"));
        verifyThat("#mem_tbl", hasTableCell("1234567890"));
        Thread.sleep(1000);

        // Select the member in the table by clicking the row
        TableView<MemberTM> table = lookup("#mem_tbl").queryTableView();
        MemberTM targetMember = table.getItems().stream()
                .filter(member -> member.getId() == 13)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Member not found in the table"));

//choosing the row
        int targetIndex = table.getItems().indexOf(targetMember);
        Platform.runLater(() -> table.getSelectionModel().select(targetIndex));
        Thread.sleep(1000);


//click delete button
        assertTrue(lookup("#btn_dtl").queryButton().isDisable() == false, "Delete button is disabled!");

        clickOn("#btn_dtl"); // Delete button
        Thread.sleep(1000);

        clickOn("OK");

        Thread.sleep(1000);

//Verify the member is no longer in the table
        List<MemberTM> items = table.getItems();
        boolean isMemberDeleted = items.stream().noneMatch(member -> member.getId() == 13);
        assertTrue(isMemberDeleted, "The member with ID 13 was not deleted.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join(); // Ensures the main test thread waits for the spawned thread
    }



}
