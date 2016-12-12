package cn.edu.xjtu.pinauth.classify;

/**
 * Created by Yunpeng on 15/10/17.
 */
public class KNNNode {

    private int index;
    private double distance;
    private double type;


    public KNNNode(int index, double distance) {
        this.index = index;
        this.distance = distance;
    }

    public KNNNode(int index, double distance, double type) {
        this.index = index;
        this.distance = distance;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public double getDistance() {
        return distance;
    }

    public double getType() {
        return type;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setType(double type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return Double.toString(distance);
    }
}

