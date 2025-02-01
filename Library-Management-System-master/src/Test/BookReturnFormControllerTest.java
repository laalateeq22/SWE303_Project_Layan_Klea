package Test;
import Controller.BookReturnFormController;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookReturnFormControllerTest {

    private BookReturnFormController controller;

    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {
            // Initialize JavaFX Toolkit
        });
    }

    @BeforeEach
    void setUp() {
        controller = new BookReturnFormController();
        Platform.runLater(() -> {
            controller.txt_issu_date = new javafx.scene.control.TextField();
            controller.txt_fine = new javafx.scene.control.TextField();
            controller.txt_rt_date = new javafx.scene.control.DatePicker();
        });
    }

    @AfterEach
    public void tearDown() {
        // Cleanup resources after each test
        try {
            if (controller.connection != null && !controller.connection.isClosed()) {
                controller.connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void FineCalculation_AtExactBoundary_NoFine() {
        // Verifies no fine on the 14th day (last day of grace period)
        LocalDate issuedDate = LocalDate.of(2025, 1, 1);
        LocalDate returnedDate = LocalDate.of(2025, 1, 15); 

        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(0.0f, fine, "The fine should be 0 on the 14th day (last day of grace period).");
    }
   

    @Test
    void FineCalculation_JustOverBoundary() {
        // Verifies fine of 15 on the 15th day (start of fine period)
        LocalDate issuedDate = LocalDate.of(2025, 1, 1);
        LocalDate returnedDate = LocalDate.of(2025, 1, 16); // 1 day late

        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(15.0f, fine, "The fine should be 15 for 1 day late after the grace period.");
    }

    
    @Test
    void testFineCalculation_JustBelowBoundary() {
        // Confirms no fine on the 13th day (within  period)
        LocalDate issuedDate = LocalDate.of(2025, 1, 1);
        LocalDate returnedDate = LocalDate.of(2025, 1, 13); // 13th day, no late return

        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(0.0f, fine, "The fine should be 0 for a return within the grace period.");
    }

    

    @Test
    void FineCalculation_AtStartDate() {
        // Ensures no fine for same-day return
        LocalDate issuedDate = LocalDate.of(2025, 1, 1);
        LocalDate returnedDate = LocalDate.of(2025, 1, 1); // Same-day return

        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(0.0f, fine, "The fine should be 0 for a return on the same day.");
    }


    @Test
    void testCalculateFine() {
        // Tests the calculation logic of the fine for 5 days late return
        LocalDate issuedDate = LocalDate.of(2025, 1, 1);
        LocalDate returnedDate = LocalDate.of(2025, 1, 20); // 5 days late (14-day borrowing period)

        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(75.0f, fine); // 5 days x 15 fine/day
    }

    
    @Test
    void testCalculateFine_NoLateReturn() {
        // Tests that no fine is charged if returned within the borrowing period
        LocalDate issuedDate = LocalDate.of(2025, 1, 1);
        LocalDate returnedDate = LocalDate.of(2025, 1, 10); 

        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(0.0f, fine, "No fine should be charged for a return within the grace period.");
    }

    
    @Test
    void testInitializeDatabaseConnection() {
        // Verifies database connection initialization
        try {
            controller.initializeDatabaseConnection();
            Connection connection = controller.connection;

            assertNotNull(connection, "Database connection should not be null.");

            assertTrue(connection.isValid(2), "Database connection should be valid.");
        } catch (Exception e) {
            fail("Exception occurred during database connection initialization: " + e.getMessage());
        }
    }
}
