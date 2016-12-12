package cn.edu.xjtu.pinauth.util;

/**
 * Created by Yunpeng on 15/10/17.
 */
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Yunpeng on 15/7/11.
 */
public class DataHandler {


    /**
     * 工具
     */
    private static Mean mean = new Mean();
    private static Min min = new Min();
    private static Max max = new Max();
    private static Variance variance = new Variance();
    private static StandardDeviation standardDeviation = new StandardDeviation();
    private static Percentile percentile = new Percentile();
    private static Skewness skewness = new Skewness(); 
    private static Kurtosis kurtosis = new Kurtosis();

//    private static PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();

    private static KolmogorovSmirnovTest kolmogorovSmirnovTest = new KolmogorovSmirnovTest();


    /**
     * @param values
     * @return
     */
    public static double meanValueOf(double[] values){
        return mean.evaluate(values);
    }

    /**
     * @param values
     * @return
     */
    public static double minValueOf(double[] values) {
        return min.evaluate(values);
    }

    /**
     * @param values
     * @return
     */
    public static double maxValueOf(double[] values) {
        return max.evaluate(values);
    }


    /**
     * @param values
     * @return
     */
    public static double varianceValueOf(double[] values) {
        return variance.evaluate(values);
    }

    /**
     * @param values
     * @return
     */
    public static double standardValueOf(double[] values) {
        return standardDeviation.evaluate(values);
    }

    /**
     * @param values
     * @return
     */
    public static double medianValueOf(double[] values) {
        return percentile.evaluate(values);
    }


    /**
     * @param values
     * @return
     */
    public static double quartileDeviationValueOf(double[] values) {
        return percentile.evaluate(values,75.0) - percentile.evaluate(values,25.0);
    }

    /**
     * @param values
     * @return
     */
    public static double skewnessValueOf(double[] values) {
        return skewness.evaluate(values);
    }

    /**
     * 获取峰度
     * @param values
     * @return
     */
    public static double kurtosis(double[] values) {
        return kurtosis.evaluate(values);
    }


    /**
     * zero-mean normalization
     * @param x
     * @return
     */
    public static double[] normalization(double[] x) {
        double mean = DataHandler.meanValueOf(x);
        double s = Math.sqrt(DataHandler.varianceValueOf(x));
        double[] result = new double[x.length];
        for (int i=0;i<x.length;i++) {
            result[i] = (x[i] - mean)/s;
        }
        return result;
    }

    public static double[][] covarianceOf(double[][] x) {
        Covariance covariance = new Covariance(x);
        return covariance.getCovarianceMatrix().getData();
    }

    public static double[][] inverseOf(double[][] x) {
        RealMatrix matrix =  new Array2DRowRealMatrix(x);
        return MatrixUtils.inverse(matrix).getData();
    }

    public static RealMatrix inversedCovarianceOf(double[][] x) {
        Covariance covariance = new Covariance(x);
        RealMatrix matrix = MatrixUtils.inverse(covariance.getCovarianceMatrix());
        return matrix;
    }


    public static double mahalanobisDistance(double[] x, double[] y, double[][] dataSet) {
        Covariance cov = new Covariance(dataSet);
        RealMatrix invCovMatrix = MatrixUtils.inverse(cov.getCovarianceMatrix());

        return  mahalanobisDistanceByInvCov(x, y, invCovMatrix);
    }

    public static double mahalanobisDistanceByInvCov(double[] x, double[] y, RealMatrix invCovMatrix) {
        RealMatrix xVector = new Array2DRowRealMatrix(x);
        RealMatrix yVector = new Array2DRowRealMatrix(y);
        RealMatrix t = xVector.subtract(yVector);
        RealMatrix result = t.transpose().multiply(invCovMatrix).multiply(t);
        double r = result.getData()[0][0];
        return Math.sqrt(r);
    }

    public static double kstest(double[] x, double[] y) {
        return kolmogorovSmirnovTest.kolmogorovSmirnovTest(x, y);
    }

//    public static double[] histogramVector(double[] x) {
//        double min, max, mean, standardDev;
//        mean = meanValueOf(x);
//        standardDev = standardValueOf(x);
//        min = mean - 3 * standardDev;
//        max = mean + 3 * standardDev;
////        return histogramVector(min, max, x);
//
//        double[] newX = new double[x.length];
//        for (double t : x) {
//            if (t<max && t>min) {
//
//            }
//        }
//
//
//
//
//        return null;
//    }
//
//    private static double[] histogramVector(double min, double max, double[] x) {
//
//        int H = 9;
//        double[] histogram = new double[H];
//        for (int i = 0; i < x.length; i++) {
//            int k = (int) ((x[i] - min)/(max - min) * H);
//            if (k >= 0 && k < H) {
//                histogram[k] += 1;
//            }
//        }
//        for (int i = 0; i < histogram.length; i++) {
//            histogram[i] /= 10;
//        }
//        return histogram;
//    }

    public static double[] trasferToArray (ArrayList<Double> list) {
        double[] array = new double[list.size()];
        for (int i=0;i<array.length;i++) {
            array[i] = list.get(i);
        }
        return array;
    }


    public static double manhattanDistance(double[] u, double[] v) {

        double result = 0;
        for (int i = 0; i < v.length; i++) {
            result += Math.abs(u[i] - v[i]);
        }
        return result;
    }

    public static double[] concatAll(double[] first, double[]... rest) {
        int totalLength = first.length;
        for (double[] array : rest) {
            totalLength += array.length;
        }
        double[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (double[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
    
    public static double format(double v) {
		
		BigDecimal b = new BigDecimal(v);
		return b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
    
}



