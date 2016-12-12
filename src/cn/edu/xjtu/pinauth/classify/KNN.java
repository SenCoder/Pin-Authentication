package cn.edu.xjtu.pinauth.classify;

/**
 * Created by Yunpeng on 15/10/17.
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import org.apache.commons.math3.linear.RealMatrix;

import cn.edu.xjtu.pinauth.util.DataHandler;


public class KNN {
    private static final String TAG = "KNN";
    /**
     * 判定为真
     */
    public static final double TRUE = 1.0;

    /**
     * 判定为假
     */
    public static final double FALSE = -1.0;

    public enum ComputationalMethod {
        kEuclideanDistance,
        kMahalanobisDistance,
        kManhattanDistance,
    }

    /**
     * 模型大小
     */
    private static int modelSize = 1;

    /**
     * 模型列表
     */
    private List<ArrayList<double[]>> modelList;


    /**
     * 模型阈值列表
     */
    private List<Double> thresholdList = new ArrayList<>();

    /**
     * K
     */
    private static int K = 4;
    private double[][] currentModelData = null;

    /**
     * 定义距离计算方法
     */
    private ComputationalMethod method = ComputationalMethod.kManhattanDistance;

    public ComputationalMethod getMethod() {
        return method;
    }

    public void setMethod(ComputationalMethod method) {
        this.method = method;
    }

    public KNN(int modelSize) {
        KNN.modelSize = modelSize;
        modelList= new ArrayList<>(modelSize);
        for (int i=0;i<modelSize;i++) {
            modelList.add(new ArrayList<double[]>());
        }
    }


    public void setK(int k) {
        K = k;
    }

    private Comparator<KNNNode> comparator = new Comparator<KNNNode>() {
        @Override
        public int compare(KNNNode lhs, KNNNode rhs) {
            if(lhs.getDistance()<rhs.getDistance()) {
                return 1;
            } else if(lhs.getDistance()>rhs.getDistance()) {
                return -1;
            } else  {
                return 0;
            }
        }
    };

    /**
     * 计算距离接口
     * @param x
     * @param y
     * @return 距离
     */
    public double calculateDistance(double[] x, double[] y) {
        switch (method) {
            case kEuclideanDistance:
                return calculateDistanceByED(x,y);
            case kMahalanobisDistance:
                return calculateDistanceByMD(x,y);
            case kManhattanDistance:
                return calculateDistanceByManhattanDistance(x,y);
            default:
                System.err.println("未知计算方法");
                return 0.0;
        }
    }


    /**
     * 计算欧几里得距离
     * @param x
     * @param y
     * @return 距离
     */
    private double calculateDistanceByED(double[]x , double[] y) {
        double distance = 0.0;
        for (int i=0;i<x.length;i++) {
            distance = (x[i] - y[i]) * (x[i] - y[i]);
        }
        return distance;
    }

    private static RealMatrix invCov = null;
    //Mahalanobis Distance
    /**
     * 计算马氏距离
     * @param x
     * @param y
     * @return 距离
     */
    private double calculateDistanceByMD(double[] x, double[] y) {
        return DataHandler.mahalanobisDistanceByInvCov(x, y, invCov);
    }

    /**
     * 计算曼哈顿距离
     * @param x
     * @param y
     * @return 距离
     */
    private double calculateDistanceByManhattanDistance(double[]x,double[]y) {
        return DataHandler.manhattanDistance(x, y);
    }

    /**
     * 训练新数据
     * @param newData 新数据
     * @param index 模型索引
     */
    public void train(double[] newData,int index) {
        modelList.get(index).add(DataHandler.normalization(newData));
    }

    /**
     * 获取大小为k的优先队列
     * @param testData
     * @param k
     * @return
     */
    private PriorityQueue<KNNNode> fetchQueueOfKNN(double[] testData,int k){
        PriorityQueue<KNNNode> queue = new PriorityQueue<>(k,comparator);
        if (currentModelData.length < k) {
        	System.out.println("训练数据个数小于K");
            System.exit(1);
        }
        for (int i=0;i<k;i++) {
            double distance = calculateDistance(currentModelData[i], testData);
            KNNNode node = new KNNNode(i,distance);
            queue.add(node);
        }

        for (int i=k;i<currentModelData.length;i++) {
            double[] data = currentModelData[i];
            double distance = calculateDistance(data, testData);
            double max = queue.peek().getDistance();
            if (distance < max) {
//                KNNNode re = queue.remove();
                KNNNode node = new KNNNode(i,distance);
                queue.add(node);
            }
        }
        return queue;
    }


    /**
     * 预测
     * @param preData 新数据
     * @param index 模型索引
     * @return  分数
     */
    public double predict(double[] preData,int index) {
//    	System.out.println("KNN.predict()");
        currentModelData = modelList.get(index).toArray(new double[][]{});
        PriorityQueue<KNNNode> queue = fetchQueueOfKNN(DataHandler.normalization(preData),K);
        double[] d1 = new double[K];
        for (int i=0;i<K-1;i++) {
            d1[i] = queue.poll().getDistance();
        }
        KNNNode neighborNode = queue.poll();
        d1[K-1] = neighborNode.getDistance();

        double[] neighbor = currentModelData[neighborNode.getIndex()];
        PriorityQueue<KNNNode> nQueue = fetchQueueOfKNN(neighbor,K+1);

        double[] d2 = new double[K];
        for (int i=0;i<K;i++) {
            d2[i] = nQueue.poll().getDistance();
        }

        double ad1 = DataHandler.meanValueOf(d1);
        double ad2 = DataHandler.meanValueOf(d2);
        double x = ad1/ad2;
        double t = thresholdList.get(index);
        Log.d(TAG,"[xxxx]比值:"+ad1/ad2 + " 阈值："+thresholdList.get(index));
        // End training.
//        ThresholdParameter p = thresholdParameters.get(index);
//        double result =(x-p.mean)/(p.st/p.sqrtN);
//        Log.d(TAG,"最终结果:"+result);

        /**
         * 利用logistic函数进行打分
         * 阈值所对应的函数值为0.5
         * 相对应的值为0.99
         *
         */
        double result = 1 / (1+Math.exp((4.5951/t) *(x-t)));

        return result;
//        double a = Math.log(0.6) / t;
//        return Math.exp(a*x);
//        return (ad1/ad2 < thresholdList.get(index))?TRUE:FALSE;

    }


    /**
     * 余一交叉接口
     * end of training
     */
    public void leaveOneOutCrossValidation(double threshold) {
        thresholdList.clear();
        for (int i=0;i< modelSize-1;i++) {
//            System.out.println("第"+(i+1)+"个模型");
            currentModelData = modelList.get(i).toArray(new double[][]{});
            thresholdList.add(leaveOneOutCrossValidationHelper(threshold));
            currentModelData = null;
        }

//        System.out.println("时间模型");
        currentModelData = modelList.get(modelSize-1).toArray(new double[][]{});
        method = ComputationalMethod.kEuclideanDistance;
        thresholdList.add(leaveOneOutCrossValidationHelper(threshold));
        currentModelData = null;
        method = ComputationalMethod.kManhattanDistance;
    }

    /**
     * 余一交叉验证实现
     * @return
     */
    private double leaveOneOutCrossValidationHelper(double threshold){
//        double margin = 0.0;
        double[] distances = new double[currentModelData.length];
        for (int index=0;index<currentModelData.length;index++) {
            PriorityQueue<KNNNode> queue = fetchQueueOfKNN(currentModelData[index], K+1);
            double[] d1 = new double[K];
            for (int i=0;i<K-1;i++) {
                KNNNode node = queue.poll();
                d1[i] = node.getDistance();
            }
            KNNNode neighborNode = queue.poll();
            d1[K-1] = neighborNode.getDistance();
            double[] neighbor = currentModelData[neighborNode.getIndex()];
            PriorityQueue<KNNNode> nQueue = fetchQueueOfKNN(neighbor, K + 1);
            double[] d2 = new double[K];
            for (int i=0;i<K;i++) {
                KNNNode node = nQueue.poll();
                d2[i] = node.getDistance();
            }
            double ad1 = DataHandler.meanValueOf(d1);
            double ad2 = DataHandler.meanValueOf(d2);
            distances[index] = ad1 / ad2;
//            System.out.println("ad1/ad2="+distances[index]);
        }
        Arrays.sort(distances);
//        Log.d(TAG,"[xxxx]原始距离数组"+Arrays.toString(distances));
        distances = Arrays.copyOfRange(distances,1,distances.length-1);
//        distances = DataHandler.histogramVector(distances);
//        Log.d(TAG,"[xxxx]距离数组"+Arrays.toString(distances));
        double mean = DataHandler.meanValueOf(distances);
        double st = Math.sqrt(DataHandler.varianceValueOf(distances));
        double sqrtN = Math.sqrt(distances.length);
//        double result = mUser.getThreshold() * (st/sqrtN) + mean;
        double result = threshold * (st/sqrtN) + mean;
//        Log.d(TAG,"[xxxx]mean:"+mean + " st/sqrtN="+(st/sqrtN));
//        Log.d(TAG,"阈值大小:"+result);
        return result;

    }

}
