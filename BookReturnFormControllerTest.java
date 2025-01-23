package Test;

import Controller.BookReturnFormController;
import Model.BookReturnTM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookReturnFormControllerTest {

    private BookReturnFormController controller;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private TableView<BookReturnTM> mockTableView;
    private ComboBox<String> mockComboBox;
    private DatePicker mockDatePicker;
    private TextField mockTextField;

    @BeforeEach
    void setUp() throws Exception {
        controller = new BookReturnFormController();

        // Mock JavaFX components
        mockTableView = mock(TableView.class);
        mockComboBox = mock(ComboBox.class);
        mockDatePicker = mock(DatePicker.class);
        mockTextField = mock(TextField.class);

        // Mock database components
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Inject mock connection and UI components
        controller.connection = mockConnection;
        controller.rt_tbl = mockTableView;
        controller.cmb_issue_id = mockComboBox;
        controller.txt_rt_date = mockDatePicker;
        controller.txt_issu_date = mockTextField;
        controller.txt_fine = mockTextField;
    }

    @Test
    void testGetIssueDate() throws Exception {
        // Mock SQL behavior
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
        LocalDate returnedDate = LocalDate.of(2025, 1, 20); // 5 days late (14 days borrowing period)

        float fine = BookReturnFormController.calculateFine(issuedDate, returnedDate);
        assertEquals(75.0f, fine); // 5 days x 15 fine/day
    }

    @Test
    void testAddReturnRecordSuccess() throws Exception {
        // Mock SQL behavior
        when(mockConnection.prepareStatement("INSERT INTO returndetail VALUES (?, ?, ?, ?)"))
                .thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        boolean result = controller.addReturnRecord("ISSUE_001", "2025-01-01", "2025-01-20", 75.0f);
        assertTrue(result);

        verify(mockStatement).setString(1, "ISSUE_001");
        verify(mockStatement).setString(2, "2025-01-01");
        verify(mockStatement).setString(3, "2025-01-20");
        verify(mockStatement).setFloat(4, 75.0f);
        verify(mockStatement).executeUpdate();
    }



    @Test
    void testUpdateBookStatus() throws Exception {
        // Mock SQL behavior
        when(mockConnection.prepareStatement("UPDATE bookdetail SET states = ? WHERE id = (SELECT bookId FROM issuetb WHERE issueId = ?)"))
                .thenReturn(mockStatement);

        controller.updateBookStatus("ISSUE_001", "Available");

        verify(mockStatement).setString(1, "Available");
        verify(mockStatement).setString(2, "ISSUE_001");
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testLoadInitialData() throws Exception {
        // Mock SQL behavior for return details
        when(mockConnection.prepareStatement("SELECT * FROM returndetail"))
                .thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false); // Two rows in result
        when(mockResultSet.getString(1)).thenReturn("ID_001", "ID_002");
        when(mockResultSet.getString(2)).thenReturn("2025-01-01", "2025-01-02");
        when(mockResultSet.getString(3)).thenReturn("2025-01-15", "2025-01-16");
        when(mockResultSet.getFloat(4)).thenReturn(30.0f, 45.0f);

        ObservableList<BookReturnTM> returnList = FXCollections.observableArrayList();
        controller.loadInitialData();

        assertNotNull(controller.rt_tbl.getItems());
        assertEquals(2, controller.rt_tbl.getItems().size());
    }
}
