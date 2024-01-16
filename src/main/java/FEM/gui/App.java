package FEM.gui;

import FEM.FEM;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.io.IOException;

public class App extends Application {
    @FXML
    private TextField input;
    @FXML
    private LineChart<Number, Number> chart;
    private int n;

    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("fxml/app.fxml"));
        VBox viewRoot = loader.load();
        configureStage(primaryStage, viewRoot);
        primaryStage.show();
    }

    private void configureStage(Stage primaryStage, VBox viewRoot) {
        var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FEM - elastic deformation");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }

    @FXML
    public void calculate() {
        if (validateInput()) {
            FEM.calculate(chart, n);
        }
    }

    private boolean validateInput() {
        try {
            n = Integer.parseInt(input.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid input: " + input.getText());
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Data");
        alert.setHeaderText("Error in Input");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
