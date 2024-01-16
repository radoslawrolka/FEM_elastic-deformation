package FEM.solver;

// solve calculus
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
// solve matrix equations
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.LUDecomposition;

import java.util.Arrays;

public class Calculator {
    private final UnivariateIntegrator calculusCalculator = new IterativeLegendreGaussIntegrator(2, 1e-6, 1e-6);

    public Result solve(int N) {
        double intervalLength = 2.0;
        RealMatrix matrixB = buildMatrix(N, intervalLength);
        RealVector vectorL = buildVector(N, intervalLength);
        RealVector coefficients = solveEquation(matrixB, vectorL, N);
        double[] results = Arrays.copyOf(coefficients.toArray(), N + 1);
        results[N] = 0;
        return new Result(0, intervalLength, results);
    }

    private RealMatrix buildMatrix(int N, double intervalLength) {
        RealMatrix matrixB = new Array2DRowRealMatrix(N, N);
        for (int n = 0; n < N; n++) {
            for (int m = 0; m < N; m++) {
                double integral = calculateIntegral(N, intervalLength, n, m);
                matrixB.setEntry(n, m, -E(0) * shapeFunction(N, intervalLength, n, 0) * shapeFunction(N, intervalLength, m, 0) + integral);
            }
        }
        return matrixB;
    }

    private double calculateIntegral(int N, double intervalLength, int n, int m) {
        double integral = 0;
        if (Math.abs(m - n) <= 1) {
            double integralFrom = intervalLength * Math.max(Math.max(n, m) - 1, 0) / N;
            double integralTo = intervalLength * Math.min(Math.min(n, m) + 1, N) / N;
            integral = calculusCalculator.integrate(
                    Integer.MAX_VALUE,
                    x -> E(x) * derivativeOfShapeFunction(N, intervalLength, n, x) * derivativeOfShapeFunction(N, intervalLength, m, x),
                    integralFrom,
                    integralTo
            );
        }
        return integral;
    }

    private RealVector buildVector(int N, double intervalLength) {
        RealVector vectorL = new ArrayRealVector(N, 0);
        vectorL.setEntry(0, -10 * E(0) * shapeFunction(N, intervalLength, 0, 0));
        return vectorL;
    }

    private RealVector solveEquation(RealMatrix matrixB, RealVector vectorL, int N) {
        return new LUDecomposition(matrixB).getSolver().solve(vectorL);
    }

    private static double E(double x) {
        return x <= 1.0 ? 3.0 : 5.0;
    }

    private static double shapeFunction(int N, double intervalLength, int i, double x) {
        double h = intervalLength / N;
        double hInv = N / intervalLength;
        double center = intervalLength * i / N;
        double left = center - h;
        double right = center + h;
        if (x < left || x > right) {
            return 0.0;
        }
        return x <= center ? (x - left) * hInv : (right - x) * hInv;
    }

    private static double derivativeOfShapeFunction(int N, double intervalLength, int i, double x) {
        double h = intervalLength / N;
        double hInv = N / intervalLength;
        double center = intervalLength * i / N;
        double left = center - h;
        double right = center + h;
        if (x < left || x > right) {
            return 0.0;
        }
        return x <= center ? hInv : -hInv;
    }

}
