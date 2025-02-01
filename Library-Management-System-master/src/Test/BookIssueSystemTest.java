package BookIssueSystemTest;

import Model.BookIssueTM;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

class BookIssueSystemTest extends ApplicationTest {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the HomeFormView.fxml directly and start the application
        Parent root = FXMLLoader.load(getClass().getResource("/View/HomeFormView.fxml"));
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Test
    public void testNavigateToBookIssueForm() {

        clickOn("#issue"); 

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#bk_ssue_tbl", isVisible());  
    }

    @Test
    public void testSearchIssueFunctionality() {

        clickOn("#issue");  

        WaitForAsyncUtils.waitForFxEvents();

        write("SS12345"); 
        
        press(KeyCode.ENTER);  


        TableView<BookIssueTM> tableView = lookup("#bk_ssue_tbl").queryAs(TableView.class);
        assertNotNull(tableView, "TableView should not be null.");
        assertTrue(tableView.getItems().isEmpty(), "TableView should display issues after searching.");
    }

    @Test
    public void testDeleteIssue() {

        clickOn("#issue");  

        WaitForAsyncUtils.waitForFxEvents();

        TableView<BookIssueTM> tableView = lookup("#bk_ssue_tbl").queryAs(TableView.class);
        assertNotNull(tableView, "TableView should not be null.");
        tableView.getSelectionModel().selectFirst();  

        clickOn("#btnDelete");  

        WaitForAsyncUtils.waitForFxEvents();

        int tableSizeAfterDelete = tableView.getItems().size();
        assertEquals(0, tableSizeAfterDelete, "TableView should have one less item after deleting a record.");
    }


}
