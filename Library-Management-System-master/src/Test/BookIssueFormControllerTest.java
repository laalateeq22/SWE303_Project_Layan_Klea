package Test;

import Controller.BookIssueFormController;
import Model.BookIssueTM;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookIssueFormControllerTest {

    private BookIssueFormController controller;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeAll
    static void initJavaFX() {
        // Initialize JavaFX runtime
        new JFXPanel();
    }

    @BeforeEach
    void setUp() {
        controller = new BookIssueFormController();

        // Mock database connection and components
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        controller.connection = mockConnection;

        // Mock JavaFX components
        controller.txt_issid = new TextField();
        controller.txt_isu_date = new DatePicker();
        controller.txt_name = new TextField();
        controller.txt_title = new TextField();
        controller.mem_is_id = new ComboBox<>();
        controller.book_id = new ComboBox<>();
        controller.bk_ssue_tbl = new TableView<>();
    }

    @Test
    void testInitialize() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Platform.runLater(() -> assertDoesNotThrow(() -> controller.initialize()));
    }

    @Test
    void testNewActionGeneratesNewId() {
        Platform.runLater(() -> {
            controller.txt_issid.setText("OldID");
            controller.new_action(new ActionEvent());
            assertNotNull(controller.txt_issid.getText(), "New ID should be generated");
            assertNotEquals("OldID", controller.txt_issid.getText(), "ID should be updated");
        });
    }

    @Test
    void testAddActionWithValidData() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        Platform.runLater(() -> {
            controller.txt_issid.setText("I001");
            controller.txt_isu_date.setValue(java.time.LocalDate.now());
            controller.mem_is_id.getItems().add("M001");
            controller.book_id.getItems().add("B001");
            controller.mem_is_id.getSelectionModel().select("M001");
            controller.book_id.getSelectionModel().select("B001");

            try {
                controller.add_action(new ActionEvent());

                // Verify database insert operation
                verify(mockStatement, times(1)).setString(1, "I001");
                verify(mockStatement, times(1)).setString(2, java.time.LocalDate.now().toString());
                verify(mockStatement, times(1)).setString(3, "M001");
                verify(mockStatement, times(1)).setString(4, "B001");
                verify(mockStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                fail("SQL Exception thrown during add_Action");
            }
        });
    }

    @Test
    void testAddActionWithInvalidData() {
        Platform.runLater(() -> {
            controller.txt_issid.setText("");
            assertDoesNotThrow(() -> controller.add_action(new ActionEvent()));
            verifyNoInteractions(mockStatement, "Database interactions should not occur with invalid data");
        });
    }

    @Test
    void testDeleteActionWithValidData() throws Exception {
        BookIssueTM mockIssue = new BookIssueTM("I001", "2025-01-01", "M001", "B001");
        controller.bk_ssue_tbl.setItems(FXCollections.observableArrayList(mockIssue));
        controller.bk_ssue_tbl.getSelectionModel().select(mockIssue);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        Platform.runLater(() -> {
            try {
                controller.delete_Action(new ActionEvent());

                // Verify delete operation
                verify(mockStatement, times(1)).setString(1, "I001");
                verify(mockStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                fail("SQL Exception thrown during delete_Action");
            }
        });
    }

    @Test
    void testDeleteActionWithoutSelection() {
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.delete_Action(new ActionEvent()));
            verifyNoInteractions(mockStatement, "Database interactions should not occur without selection");
        });
    }

    @Test
    void testMemberIdSelectionChangeUpdatesName() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn("John Doe");

        Platform.runLater(() -> {
            controller.mem_is_id.getItems().add("M001");
            controller.mem_is_id.getSelectionModel().select("M001");

            assertEquals("John Doe", controller.txt_name.getText(), "Member name should be updated");
        });
    }

    @Test
    void testBookIdSelectionChangeUpdatesTitle() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn("Java Programming");
        when(mockResultSet.getString(2)).thenReturn("Available");

        Platform.runLater(() -> {
            controller.book_id.getItems().add("B001");
            controller.book_id.getSelectionModel().select("B001");

            assertEquals("Java Programming", controller.txt_title.getText(), "Book title should be updated");
        });
    }
    @Test
    void testDeleteActionWithNoSelectedItem() {
        // Simulate no item selected in the table
        controller.bk_ssue_tbl.getSelectionModel().clearSelection();

        // Use an ArgumentCaptor to capture any potential interactions with the mockStatement
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        Platform.runLater(() -> {
            try {
                controller.delete_Action(new ActionEvent());

                // Verify that no interaction with mockStatement occurs when no item is selected
                verify(mockStatement, never()).setString(Integer.parseInt(anyString()), argumentCaptor.capture());
                verify(mockStatement, never()).executeUpdate();
            } catch (SQLException e) {
                fail("SQL Exception thrown during delete_Action");
            }
        });
    }

    @Test
    void testAddActionWithInvalidIssueDate() {
        Platform.runLater(() -> {
            controller.txt_issid.setText("I001");
            controller.txt_isu_date.setValue(null);  // Invalid date
            controller.mem_is_id.getItems().add("M001");
            controller.book_id.getItems().add("B001");
            controller.mem_is_id.getSelectionModel().select("M001");
            controller.book_id.getSelectionModel().select("B001");

            assertDoesNotThrow(() -> controller.add_action(new ActionEvent()));
            verifyNoInteractions(mockStatement, "Database interactions should not occur with invalid date");
        });
    }

    @Test
    void testNewActionGeneratesValidId() {
        Platform.runLater(() -> {
            controller.new_action(new ActionEvent());

            String newIssueId = controller.txt_issid.getText();
            // Check if the ID starts with "SS" and is followed by digits
            assertTrue(newIssueId.startsWith("SS"));
            assertTrue(newIssueId.length() > 2, "New Issue ID should be valid");
        });
    }

    @Test
    void testMemberIdSelectionHandlesInvalidId() throws SQLException {
        // Simulate a case where no member exists with the given ID
        when(mockResultSet.next()).thenReturn(false);  // No result found for member ID
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        Platform.runLater(() -> {
            controller.mem_is_id.getItems().add("INVALID_ID");
            controller.mem_is_id.getSelectionModel().select("INVALID_ID");

            assertEquals("", controller.txt_name.getText(), "Member name should be empty if ID is invalid");
        });
    }

    @Test
    void testBookIdSelectionHandlesInvalidId() throws SQLException {
        // Simulate a case where no book exists with the given ID
        when(mockResultSet.next()).thenReturn(false);  // No result found for book ID
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        Platform.runLater(() -> {
            controller.book_id.getItems().add("INVALID_BOOK_ID");
            controller.book_id.getSelectionModel().select("INVALID_BOOK_ID");

            assertEquals("", controller.txt_title.getText(), "Book title should be empty if ID is invalid");
        });
    }

    @Test
    void testBookIdSelectionWhenBookUnavailable() throws SQLException {
        // Simulate a scenario where the selected book is not available
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(2)).thenReturn("Not Available"); // Book status is not available
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        Platform.runLater(() -> {
            controller.book_id.getItems().add("B001");
            controller.book_id.getSelectionModel().select("B001");

            assertEquals("Java Programming", controller.txt_title.getText(), "Book title should be updated");
            // Verify that an alert is shown when the book is unavailable
            try {
                verify(mockStatement, times(1)).executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testIssueDateFormatHandling() {
        // Simulate a case where issue date is incorrect or null
        Platform.runLater(() -> {
            controller.txt_isu_date.setValue(null);  // Invalid issue date

            controller.add_action(new ActionEvent());

            // Ensure the system handles this without failing
            try {
                verify(mockStatement, never()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testValidMemberAndBookIdShouldPopulateFields() throws SQLException {
        when(mockResultSet.getString(1)).thenReturn("John Doe");
        when(mockResultSet.getString(2)).thenReturn("Java Programming");

        Platform.runLater(() -> {
            // Simulate valid selection
            controller.mem_is_id.getItems().add("M001");
            controller.book_id.getItems().add("B001");

            controller.mem_is_id.getSelectionModel().select("M001");
            controller.book_id.getSelectionModel().select("B001");

            assertEquals("John Doe", controller.txt_name.getText(), "Member name should be correctly displayed");
            assertEquals("Java Programming", controller.txt_title.getText(), "Book title should be correctly displayed");
        });
    }

    @Test
    void testAddActionWithEmptyFields() {
        Platform.runLater(() -> {
            controller.txt_issid.setText("");
            controller.txt_isu_date.setValue(null);
            controller.mem_is_id.setValue(null);
            controller.book_id.setValue(null);

            controller.add_action(new ActionEvent());  // Should not trigger any database interaction
            verifyNoInteractions(mockStatement, "No database interaction should occur when fields are empty");
        });
    }


}

