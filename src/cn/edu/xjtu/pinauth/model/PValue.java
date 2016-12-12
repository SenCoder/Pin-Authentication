package cn.edu.xjtu.pinauth.model;

/**
 * Created by Yunpeng on 15/10/27.
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.edu.xjtu.pinauth.util.DataHandler;
import cn.edu.xjtu.pinauth.util.FileHelper;

/**
 * Created by Yunpeng on 15/8/25.
 */
public class PValue {
    public static void main(String[] args) throws IOException {
        String fromPath = FileHelper.APP_PATH;
        File file = new File(fromPath);
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.equals(".DS_Store")) {
                    return false;
                }
                return true;
            }
        };
        String[] users =  file.list(filenameFilter);
        System.out.println("鍏辫鐢ㄦ埛"+users.length+"浜�");
        System.out.println("寮�濮�...");
        output(users,"01478");
    }
    public static void output(String[] users,String password)throws IOException {
        for (int userIndex=0;userIndex<users.length;userIndex++) {
            String username = users[userIndex];
            ArrayList<AuthEvent> authEvents = FileHelper.loadRawData(username, password,FileHelper.TRAINING_DATA_Dir);
            for (int index =0;index<password.length();index++) {
//                double[][] data = new double[authEvents.size()][];
//                for (int i = 0; i < authEvents.size(); i++) {
//                    data[i] = authEvents.get(i).getTouchEvents().get(index).featureVector();
//                }
                String outpath = "/Users/Yunpeng/pvalue/rawdata/" + username + "_"+index+".txt";
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outpath)));
                for (int i=0;i<authEvents.size();i++) {
                    double[] vector = authEvents.get(i).getTouchEvents().get(index).allFeatureVector();
                    for (double num : vector) {
                        out.write(Double.toString(num).getBytes());
                        out.write(" ".getBytes());
                    }
                    out.write("\n".getBytes());
                }
                out.close();
            }
        }
    }


    public static void  pvalue(String[] users, String password) throws IOException {
        for(int ii=0;ii<users.length - 1 ;ii++) {
            for (int jj=ii+1;jj< users.length;jj++) {
                if (ii>=users.length || jj >= users.length) {
                    break;
                }
                String user1 = users[ii];
                String user2 = users[jj];
                System.out.println("鐢ㄦ埛1:"+user1+" 鐢ㄦ埛2:"+ user2);
                for (int index = 0;index<password.length();index++) {
//                    new ArrayList<>();
                    ArrayList<AuthEvent> authEvents = FileHelper.loadRawData(user1, password,FileHelper.TRAINING_DATA_Dir);
                    ArrayList<AuthEvent> authEvents2 = FileHelper.loadRawData(user2, password, FileHelper.TRAINING_DATA_Dir);


                    int size = Math.min(authEvents.size(),authEvents2.size());
                    double[][] data1 = new double[size][];
                    for (int i=0;i<size;i++) {
                            data1[i] = authEvents.get(i).getTouchEvents().get(index).allFeatureVector();
                    }
                    double[][] data2 = new double[size][];
                    for (int i=0;i<size;i++) {
                            data2[i] = authEvents2.get(i).getTouchEvents().get(index).allFeatureVector();
                    }


                    System.out.println("\n-------- 绗�"+(index+1)+"妯″瀷 ---------");
                    String file = "/Users/Yunpeng/pvalue/"+user1+"_"+user2+"_"+index+".txt";
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(file)));
                    for (int k=0;k<data1[0].length;k++) {
                        System.out.println("k=" + k);
                        double[] sample1 = fetchColumn(data1,k);
                        double[] sample2 = fetchColumn(data2,k);
                        System.out.println("鏍锋湰1:" + Arrays.toString(sample1));
                        System.out.println("鏍锋湰2:" + Arrays.toString(sample2));
                        double pvalue = DataHandler.kstest(sample1, sample2);
                        out.write(Double.toString(pvalue).getBytes());
                        out.write("\n".getBytes());
                        System.out.println("pValue=" + pvalue);
                    }
                    out.close();
                }
                ii = jj;
            }
        }
    }


    public static double[] fetchColumn(double[][] x,int column) {
        double[]singleData = new double[x.length];
        for (int i=0;i<x.length;i++){
            singleData[i] = x[i][column];
        }
        return singleData;
    }
}
