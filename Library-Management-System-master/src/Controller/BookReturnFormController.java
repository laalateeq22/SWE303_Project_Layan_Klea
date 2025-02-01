package Controller;

import Model.BookReturnTM;
import db.DBConnection;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class BookReturnFormController {

    // UI Components
    public AnchorPane Returnroot;
    public TextField txt_issu_date;
    public TextField txt_fine;
    public DatePicker txt_rt_date;
    public TableView<BookReturnTM> rt_tbl;
    public ComboBox<String> cmb_issue_id;

    // Database Connection
    public Connection connection;

    /**
     * Section: Initialization
     */
    public void initialize() {
        initializeDatabaseConnection(); // Initialize database connection first

        TableColumn<BookReturnTM, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<BookReturnTM, String> issuedDateColumn = new TableColumn<>("Issued Date");
        issuedDateColumn.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));

        TableColumn<BookReturnTM, String> returnedDateColumn = new TableColumn<>("Returned Date");
        returnedDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnedDate"));

        TableColumn<BookReturnTM, Float> fineColumn = new TableColumn<>("Fine");
        fineColumn.setCellValueFactory(new PropertyValueFactory<>("fine"));

        rt_tbl.getColumns().addAll(idColumn, issuedDateColumn, returnedDateColumn, fineColumn);

        try {
            loadInitialData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initializeDatabaseConnection() {
        try {
            connection = DBConnection.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection");
        }
    }


    public void loadInitialData() {
        try {
            ObservableList<BookReturnTM> returnList = FXCollections.observableArrayList();

            // Load return details
            String sql = "SELECT * FROM returndetail";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                float fine = rs.getFloat(4);
                returnList.add(new BookReturnTM(rs.getString(1), rs.getString(2), rs.getString(3), fine));
            }
            rt_tbl.setItems(returnList);

            // Load issue IDs
            cmb_issue_id.getItems().clear();
            populateComboBox(cmb_issue_id, "SELECT issueId FROM issuetb");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateComboBox(ComboBox<String> comboBox, String query) {
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboBox.getItems().add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setupListeners() {
        // Issue ID ComboBox Listener
        cmb_issue_id.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    txt_issu_date.setText(getIssueDate(newValue));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Return DatePicker Listener
        txt_rt_date.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !txt_issu_date.getText().isEmpty()) {
                LocalDate issuedDate = LocalDate.parse(txt_issu_date.getText());
                float fine = calculateFine(issuedDate, newValue);
                txt_fine.setText(String.format("%.2f", fine));
            }
        });
    }

    /**
     * Section: Event Handlers
     */
    public void btn_new(ActionEvent actionEvent) {
        clearForm();
    }

    public void btn_add_inveb(ActionEvent actionEvent) {
        if (cmb_issue_id.getSelectionModel().isEmpty() || txt_issu_date.getText().isEmpty()
                || txt_rt_date.getValue() == null || txt_fine.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all details.");
            return;
        }

        String issueId = cmb_issue_id.getSelectionModel().getSelectedItem();
        String issuedDate = txt_issu_date.getText();
        String returnedDate = txt_rt_date.getValue().toString();
        float fine = Float.parseFloat(txt_fine.getText());

        boolean isSuccess = addReturnRecord(issueId, issuedDate, returnedDate, fine);

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Return record added successfully!");
            loadInitialData();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Something went wrong. Please try again.");
        }
    }

    @FXML
    public void img_back(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/HomeFormView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.Returnroot.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    @FXML
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

    /**
     * Section: Database Logic
     */
    public String getIssueDate(String issueId) throws SQLException {
        String sql = "SELECT date FROM issuetb WHERE issueId = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, issueId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString(1);
        }
        return null;
    }

    public boolean addReturnRecord(String issueId, String issuedDate, String returnedDate, float fine) {
        try {
            // SQL query without 'id' field
            String sql = "INSERT INTO returndetail (issueId, issuedDate, returnedDate, fine) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, issueId);
            stmt.setString(2, issuedDate);
            stmt.setString(3, returnedDate);
            stmt.setFloat(4, fine);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                updateBookStatus(issueId, "Available");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateBookStatus(String issueId, String status) {
        try {
            String sql = "UPDATE bookdetail SET states = ? WHERE id = (SELECT bookId FROM issuetb WHERE issueId = ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, issueId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Section: Utility Methods
     */
    public void clearForm() {
        txt_issu_date.clear();
        txt_fine.clear();
        txt_rt_date.setValue(null);
        cmb_issue_id.getSelectionModel().clearSelection();
    }

    public static float calculateFine(LocalDate issuedDate, LocalDate returnedDate) {
        // Borrowing period: 14 days
        LocalDate dueDate = issuedDate.plusDays(14);
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnedDate);
        return daysLate > 0 ? daysLate * 15 : 0; // Fine is 15 per day for late returns
    }




    public void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}