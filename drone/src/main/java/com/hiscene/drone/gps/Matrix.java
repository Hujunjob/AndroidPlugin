package com.hiscene.drone.gps;

/**
 * Created by hujun on 2019/5/12.
 */

public class Matrix {
    private double[][] m;
    public Matrix(int row,int col){
        m = new double[col][row];
    }

    public Matrix(double[][] m){
        this.m = m;
    }

    public int getRow(){
        return m[0].length;
    }

    public int getCol(){
        return m.length;
    }

    public double getValue(int row,int col){
        return m[col][row];
    }

    public void setValue(int row,int col,double value){
        m[col][row] =  value;
    }

    public double[][] getValue(){
        return m;
    }

    public void multiple(double value){
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                m[i][j] = m[i][j] * value;
            }
        }
    }
}
