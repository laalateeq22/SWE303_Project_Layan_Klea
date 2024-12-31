import Controller.BookReturnFormController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookReturnFormControllerFineCalculationTest {

    private BookReturnFormController controller;

    @BeforeEach
    public void setUp() {
        controller = new BookReturnFormController();
    }

    @Test
    public void FineCalculation_AtExactBoundary_NoFine() {

        LocalDate issuedDate = LocalDate.of(2024, 1, 1);
        LocalDate returnedDate = LocalDate.of(2024, 1, 14); // 14 days inclusive

        float fine = controller.calculateFine(issuedDate, returnedDate);

        assertEquals(0, fine, "Fine should be 0 when returning on the 14th day.");
    }

    @Test
    void FineCalculation_JustOverBoundary() {
        LocalDate issuedDate = LocalDate.of(2024, 1, 1);
        LocalDate returnedDate = LocalDate.of(2024, 1, 16); // 15th day since issuance
        float expectedFine = 15.0f;

        float actualFine = BookReturnFormController.calculateFine(issuedDate, returnedDate);

        assertEquals(expectedFine, actualFine, "Fine should be 15 when returning on the 15th day.");
    }


    @Test
    public void FineCalculation_JustBelowBoundary() {
        // Return on the 13th day
        LocalDate issuedDate = LocalDate.of(2024, 1, 1);
        LocalDate returnedDate = LocalDate.of(2024, 1, 13);

        float fine = controller.calculateFine(issuedDate, returnedDate);

        assertEquals(0, fine, "Fine should be 0 when returning on the 13th day.");
    }

    @Test
    public void FineCalculation_AtStartDate() {
        //  Return on the same day
        LocalDate issuedDate = LocalDate.of(2024, 1, 1);
        LocalDate returnedDate = LocalDate.of(2024, 1, 1);

        float fine = controller.calculateFine(issuedDate, returnedDate);

        assertEquals(0, fine, "Fine should be 0 when returning on the same day.");
    }

    @Test
    public void FineCalculation_LongAfterBoundary() {
        //  Return far beyond the 14th day
        LocalDate issuedDate = LocalDate.of(2024, 1, 1);
        LocalDate returnedDate = LocalDate.of(2024, 2, 1); // 31 days overdue

        float fine = controller.calculateFine(issuedDate, returnedDate);

        assertEquals(255, fine, "Fine should be correctly calculated for long overdue returns.");
    }
}
