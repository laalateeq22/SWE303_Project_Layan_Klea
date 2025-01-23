package Controller;

import Model.BookTM;
import db.DB;
import db.DBConnection;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import mocks.MockAlert;
import org.junit.jupiter.api.BeforeAll;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class BookFormController {
    public TextField txt_bk_id;
    public TextField  txt_bk_title;
    public TextField  txt_bk_auth;
    public TextField  txt_bk_st;
    public TableView<BookTM> tbl_bk;
    public BookTM book;
    public AnchorPane bk_root;
    public Button btn_add;
    private Connection connection;

    //JDBC
    private PreparedStatement selectall;
    private PreparedStatement selectID;
    private PreparedStatement newIdQuery;
    private PreparedStatement addToTable;
    private PreparedStatement updateQuarary;
    private PreparedStatement deleteQuarary;
    private PreparedStatement updateQuery;
    private ObservableList<BookTM> books = FXCollections.observableArrayList();

    public void initialize() throws ClassNotFoundException {
        // Disable the ID field
        txt_bk_id.setDisable(true);

        // Load the table
        tbl_bk.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl_bk.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tbl_bk.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tbl_bk.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("status"));


        Class.forName("com.mysql.jdbc.Driver");

        try {
            connection = DBConnection.getInstance().getConnection();
            selectall = connection.prepareStatement("SELECT * FROM bookdetail");
            updateQuarary = connection.prepareStatement("UPDATE bookdetail SET title=?, author=?, status=? WHERE id=?");
            selectID = connection.prepareStatement("SELECT * FROM bookdetail WHERE id=?");
            addToTable = connection.prepareStatement("INSERT INTO bookdetail VALUES (?, ?, ?, ?)");
            newIdQuery = connection.prepareStatement("SELECT id FROM bookdetail");
            deleteQuarary = connection.prepareStatement("DELETE FROM bookdetail WHERE id=?");

            ObservableList<BookTM> members = tbl_bk.getItems();
            ResultSet rst = selectall.executeQuery();
            while (rst.next()) {
                members.add(new BookTM(
                        rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)
                ));
            }
            tbl_bk.setItems(members);

            // Validate prepared statements
            checkNullFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tbl_bk.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txt_bk_id.setText(newValue.getId());
                txt_bk_title.setText(newValue.getTitle());
                txt_bk_auth.setText(newValue.getAuthor());
                txt_bk_st.setText(newValue.getStatus());
                btn_add.setText("Update");
            }
        });
    }

    // Initialize database connection and prepare statements
    @BeforeAll
    public void initDatabaseConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            // Replace with your database connection details
            Class.forName("com.mysql.jdbc.Driver");

            // Establish connection to the database
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/library",
                    "root",
                    ""
            );        }
        if (addToTable == null) {
            String add = "INSERT INTO bookdetail (id, title, author, status) VALUES (?, ?, ?, ?)";
            addToTable = connection.prepareStatement(add);
            String update = "UPDATE bookdetail SET title=?, author=?, status=? WHERE id=?";
            updateQuery =connection.prepareStatement(update);
        }
    }

    //button new action
    public void btn_new(ActionEvent actionEvent) throws SQLException {
        btn_add.setText("Add");
        txt_bk_st.setText("Available");
        txt_bk_st.setDisable(true);
        txt_bk_id.setDisable(false);
        txt_bk_auth.clear();
        txt_bk_title.clear();
        txt_bk_title.requestFocus();

        ResultSet rst = newIdQuery.executeQuery();

        String ids = null;
        int maxId = 0;

        while (rst.next()) {
            ids = rst.getString(1);

            int id = Integer.parseInt(ids.replace("B", ""));
            if (id > maxId) {
                maxId = id;
            }
        }
        maxId = maxId + 1;
        String id = "";
        if (maxId < 10) {
            id = "B00" + maxId;
        } else if (maxId < 100) {
            id = "B0" + maxId;
        } else {
            id = "B" + maxId;
        }
        txt_bk_id.setText(id);
    }

    //----------------------------VALIDATE THE INPUT------------------------------------
    private boolean validateInputB(BookTM book) {
        // Check if any required field (ID, Title, Author) is empty
        if (book.getId().isEmpty() || book.getTitle().isEmpty() || book.getAuthor().isEmpty()) {
            return false;
        }

        // Validate that Title and Author only contain letters and spaces (if applicable)
        if (!(book.getTitle().matches("^\\b([A-Za-z.]+\\s?)+$") &&
                book.getAuthor().matches("^\\b([A-Za-z.]+\\s?)+$"))) {
            return false;
        }

        // If all conditions are met, return true
        return true;
    }
    //----------------------------GET ALL BOKS------------------------------------------
    public List<BookTM> getAllBooks() throws SQLException {
        List<BookTM> books = new ArrayList<>();
        String query = "SELECT * FROM bookdetail";

        // Open a statement and execute the query
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Iterate over the result set
            while (rs.next()) {
                // Assuming 'ID', 'Title', 'Author', and 'Status' are columns in your database
                String id = rs.getString("ID");
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                String status = rs.getString("Status");

                // Create a new BookTM object and add it to the list
                BookTM book = new BookTM(id, title, author, status);
                books.add(book);
            }
        }

        return books;
    }

    //-------------------------BUTTEN ADD-----------------------------------------------
    public void btn_Add(ActionEvent actionEvent) throws SQLException {
        // Prepare the book details from UI fields
        BookTM book = new BookTM(
                txt_bk_id.getText(),
                txt_bk_title.getText(),
                txt_bk_auth.getText(),
                txt_bk_st.getText()
        );

        if (btn_add.getText().equals("Add")) {
            // Add the book
            boolean success = addBookToDatabase(book);
            showResult(success, "Data insertion successful", "ERROR");
        } else if (btn_add.getText().equals("Update")) {
            // Update the book
            boolean success = updateBookInDatabase(book);
            showResult(success, "Record updated!", "Update error!");
        }

        // Refresh the table
        refreshTable();
    }

    public boolean addBookToDatabase(BookTM book) throws SQLException {
        if(validateInputB(book)){
            addToTable.setString(1, book.getId());
            addToTable.setString(2, book.getTitle());
            addToTable.setString(3, book.getAuthor());
            addToTable.setString(4, book.getStatus());
            int affectedRows = addToTable.executeUpdate();
            return affectedRows > 0;
        }
        else{
            return false;
        }
    }

    //-------------------------UPDATE BUTTON--------------------------------------------
    public boolean updateBookInDatabase(BookTM book) throws SQLException {
        if(!validateInputB(book)){
            return false;
        }
        List<BookTM> books = getAllBooks();
        String update = "UPDATE bookdetail SET title=?, author=?, status=? WHERE id=?";
        updateQuery =connection.prepareStatement(update);
        for (BookTM existingBook : books) {
            if (existingBook.getId().equals(book.getId())) {
                updateQuery.setString(1, book.getTitle());
                updateQuery.setString(2, book.getAuthor());
                updateQuery.setString(3, book.getStatus());
                updateQuery.setString(4, book.getId());
                int affectedRows = updateQuery.executeUpdate();
                return affectedRows > 0;
            }
        }
        return false;
    }

    //-------------------------DELETE BUTTON--------------------------------------------
    public void btn_dlt(ActionEvent actionEvent) throws SQLException {
        BookTM selectedBook = tbl_bk.getSelectionModel().getSelectedItem();

        if (selectedBook == null) { // Check if no book is selected
            showAlert(Alert.AlertType.ERROR, "Please select a book to delete.");
            return;
        }

        try {
            boolean isDeleted = deleteBook(selectedBook.getId());
            if (isDeleted) {
                showAlert(Alert.AlertType.INFORMATION, "Book deleted successfully!");
                tbl_bk.getItems().remove(selectedBook); // Update the UI
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to delete the book. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred while deleting the book.");
        }
    }

    public boolean deleteBook(String bookId) throws SQLException {
        String deleteQuery = "DELETE FROM bookdetail WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, bookId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Return true if at least one row was deleted
        }
    }

    //----------------------------------------------------------------------------------
    public void img_back(MouseEvent event) throws IOException {

        URL resource = this.getClass().getResource("/View/HomeFormView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.bk_root.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }

    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.YELLOW);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }

    // Show feedback to the user
    private void showResult(boolean success, String successMessage, String errorMessage) {
        if (success) {
            new Alert(Alert.AlertType.INFORMATION, successMessage, ButtonType.OK).show();
        } else {
            new Alert(Alert.AlertType.ERROR, errorMessage, ButtonType.OK).show();
        }
    }

    // Refresh the table data
    private void refreshTable() {
        try {
            tbl_bk.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Helper to check null fields and report issues
    private void checkNullFields() {
        if (addToTable == null) {
            throw new IllegalStateException("PreparedStatement 'addToTable' is not initialized.");
        }
        if (updateQuarary == null) {
            throw new IllegalStateException("PreparedStatement 'updateQuarary' is not initialized.");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

}