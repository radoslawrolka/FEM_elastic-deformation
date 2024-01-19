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

public class Calculator2 {
    private final UnivariateIntegrator calculusCalculator = new IterativeLegendreGaussIntegrator(2, 1e-6, 1e-6);

    public Result solve(int N) {
        double intervalLength = 2.0;
        RealMatrix matrixB = buildMatrix(N, intervalLength);
        RealVector vectorL = buildVector(N, intervalLength);
        RealVector coefficients = solveEquation(matrixB, vectorL, N);
        double[] results = Arrays.copyOf(coefficients.toArray(), N + 1);
        for (int i = 0; i <= N; i++) {
            results[i] += 3;
        }
        return new Result(0, 2.0, results);
    }

    private RealMatrix buildMatrix(int N, double intervalLength) {
        RealMatrix matrixB = new Array2DRowRealMatrix(N, N);
        for (int n = 0; n < N; n++) {
            for (int m = 0; m < N; m++) {
                double integral = calculateIntegral(N, intervalLength, n, m);
                matrixB.setEntry(n, m, -2*E(0) * e(N, intervalLength, n, 0) * e(N, intervalLength, m, 0) + integral);
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
                    x -> E(x) * ePrim(N, intervalLength, n, x) * ePrim(N, intervalLength, m, x),
                    integralFrom,
                    integralTo
            );
        }
        return integral;
    }

    private double calculateIntegral2(int N, double intervalLength, int n, int m) {
        double integral = 0;
        if (Math.abs(m - n) <= 1) {
            double integralFrom = intervalLength * Math.max(Math.max(n, m) - 1, 0) / N;
            double integralTo = intervalLength * Math.min(Math.min(n, m) + 1, N) / N;
            integral = calculusCalculator.integrate(
                    Integer.MAX_VALUE,
                    x -> Math.sin(Math.PI * x) * e(N, intervalLength, m, x),
                    integralFrom,
                    integralTo
            );
        }
        return integral;
    }

    private RealVector buildVector(int N, double intervalLength) {
        RealVector vectorL = new ArrayRealVector(N, 0);
        double integral = 0;
        for (int i = 0; i < N; i++) {
            integral = calculateIntegral2(N, intervalLength, i, i);
            vectorL.setEntry(i, -4*E(0) * e(N, intervalLength, i, 0) - 1000*integral);
        }
        return vectorL;
    }

    private RealVector solveEquation(RealMatrix matrixB, RealVector vectorL, int N) {
        return new LUDecomposition(matrixB).getSolver().solve(vectorL);
    }

    private static double E(double x) {
        return x <= 1.0 ? 2.0 : 6.0;
    }

    private static double e(int N, double intervalLength, int i, double x) {
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

    private static double ePrim(int N, double intervalLength, int i, double x) {
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
