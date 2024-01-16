package FEM.gui;

import FEM.solver.Calculator;
import FEM.solver.Result;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Spinner;
import java.net.URL;
import java.util.ResourceBundle;

public class Presenter implements Initializable {
    @FXML
    private Spinner<Integer> input;
    @FXML
    private LineChart<Number, Number> chart;
    private final Calculator calculator = new Calculator();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.input.getValueFactory().valueProperty().addListener(value -> this.calculate());
        XYChart.Series<Number, Number> exactSolution = new XYChart.Series<>();
        exactSolution.getData().add(new XYChart.Data<>(0d, 43d));//80d / 3d));
        exactSolution.getData().add(new XYChart.Data<>(1d, 10d));
        exactSolution.getData().add(new XYChart.Data<>(2d, 3d));
        this.chart.getData().add(0, exactSolution);
        this.calculate();
    }

    @FXML
    public void calculate() {
        Result result = this.calculator.solve(this.input.getValue());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < result.coefficients().length; i++) {
            double x = (result.right() - result.left()) * i / (result.coefficients().length - 1);
            series.getData().add(new XYChart.Data<>(x, result.coefficients()[i]));
        }
        if (this.chart.getData().size() > 1) {
            this.chart.getData().remove(1);
        }
        this.chart.getData().add(1, series);
    }
}
