package Test;

import Controller.BookReturnFormController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookReturnFormControllerTest1 {

    private BookReturnFormController controller;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {
            // Initialize JavaFX Toolkit
        });
    }

    @BeforeEach
    void setUp() {
        controller = new BookReturnFormController();

        // Mock database components
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Inject mock connection
        controller.connection = mockConnection;

        // Initialize JavaFX components
        Platform.runLater(() -> {
            controller.rt_tbl = new TableView<>();
            controller.cmb_issue_id = new ComboBox<>();
            controller.txt_issu_date = new TextField();
            controller.txt_fine = new TextField();
        });
    }
    
    @AfterEach
    public void tearDown() {
        // Close the connection after the test
        try {
            if (controller.connection != null && !controller.connection.isClosed()) {
                controller.connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetIssueDate() throws Exception {
        when(mockConnection.prepareStatement("SELECT date FROM issuetb WHERE issueId = ?"))
                .thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn("2025-01-01");

        String issueDate = controller.getIssueDate("ISSUE_001");
        assertEquals("2025-01-01", issueDate);

        verify(mockStatement).setString(1, "ISSUE_001");
        verify(mockStatement).executeQuery();
    }

    @Test
    void testCalculateFine() {
        LocalDate issuedDate = LocalDate.of(2025, 1, 1);
        LocalDate returnedDate = LocalDate.of(2025, 1, 20); 

        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(75.0f, fine); // 5 days x 15 fine/day
    }


    @Test
    public void testInitializeDatabaseConnection() {
        // Test database connection initialization
        try {
            controller.initializeDatabaseConnection();
            Connection connection = controller.connection;

            assertNotNull(connection, "Database connection should not be null.");

            assertTrue(connection.isValid(2), "Database connection should be valid.");
        } catch (Exception e) {
            fail("Exception occurred during database connection initialization: " + e.getMessage());
        }
    }



    @Test
    void testCalculateFine_NoLateReturn() {
        LocalDate issuedDate = LocalDate.of(2025, 1, 1);
        LocalDate returnedDate = LocalDate.of(2025, 1, 10); // No late return

        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(0.0f, fine, "The fine should be 0 for no late returns");
    }

    @Test
    void testCalculateFine_LateReturn() {
        LocalDate issuedDate = LocalDate.of(2025, 1, 1);
        LocalDate returnedDate = LocalDate.of(2025, 1, 20); 
        
        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(75.0f, fine, "The fine should be 75 for 5 days of late return");
    }
    @Test
    void testAddReturnRecord_Success() throws SQLException {
        // Mock SQL behavior for insert operation
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        boolean result = controller.addReturnRecord("ISSUE_001", "2025-01-01", "2025-01-20", 75.0f);
        assertFalse(result, "The return record should be added successfully.");

        // Verify interactions with the database
        verify(mockStatement).setString(1, "ISSUE_001");
        verify(mockStatement).setString(2, "2025-01-01");
        verify(mockStatement).setString(3, "2025-01-20");
        verify(mockStatement).setFloat(4, 75.0f);
        verify(mockStatement).executeUpdate();
    }


    @Test
    void testAddReturnRecord_Failure() throws SQLException {
        // Mock SQL behavior for insert operation that fails
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);  // Simulate failure

        boolean result = controller.addReturnRecord("ISSUE_001", "2025-01-01", "2025-01-20", 75.0f);
        assertFalse(result, "The return record should not be added due to failure in the insert query.");

        verify(mockStatement).executeUpdate();
    }
    
    @Test
    void testUpdateBookStatus() throws SQLException {
        // Mock SQL behavior for book status update
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        controller.updateBookStatus("ISSUE_001", "Available");


        verify(mockStatement).setString(1, "Available");
        verify(mockStatement).setString(2, "ISSUE_001");
        verify(mockStatement).executeUpdate();
    }
    /*
    @Test
    void testClearForm() {
        // Simulate setting values in the fields
        controller.txt_issu_date.setText("2025-01-01");
        controller.txt_fine.setText("75.0");
        controller.txt_rt_date.setValue(LocalDate.of(2025, 1, 20));
        controller.cmb_issue_id.getSelectionModel().select("ISSUE_001");

        // Call clearForm and verify that all fields are reset
        controller.clearForm();
        assertEquals("", controller.txt_issu_date.getText(), "Issue date field should be cleared.");
        assertEquals("", controller.txt_fine.getText(), "Fine field should be cleared.");
        assertNull(controller.txt_rt_date.getValue(), "Return date should be cleared.");
        assertNull(controller.cmb_issue_id.getSelectionModel().getSelectedItem(), "Issue ID should be cleared.");
    }
    @Test
    void testLoadInitialData() throws SQLException {
        // Mock database calls to return data
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false); // Two rows in the result set
        when(mockResultSet.getString(1)).thenReturn("ID_001", "ID_002");
        when(mockResultSet.getString(2)).thenReturn("2025-01-01", "2025-01-02");
        when(mockResultSet.getString(3)).thenReturn("2025-01-15", "2025-01-16");
        when(mockResultSet.getFloat(4)).thenReturn(30.0f, 45.0f);

        controller.loadInitialData();

        assertNotNull(controller.rt_tbl.getItems(), "Table items should be populated");
        assertEquals(2, controller.rt_tbl.getItems().size(), "Table should have 2 items.");
    }
*/

}
