package com.hiscene.drone.gps;

/**
 * Created by hujun on 2019/4/23.
 */

public class CameraMatrix {
    private double fov;
    private double width;
    private double height;
    private double[][] matrix;


    public double fx = 979.9;
    public double fy = 983.1;
    public double cx = 642.2;
    public double cy = 369.2;

//    public double rX = 1.2;
//    public double rY = 1.2;


    public CameraMatrix(double fov, double width, double height) {
        this.fov = fov;
        this.width = width;
        this.height = height;
    }

    public double[][] getIntrinsicMatrix() {
        if (matrix != null) {
            return matrix;
        }
        matrix = new double[3][3];

        matrix[0][0] = (width / 2.0) / Math.tan(Math.toRadians(fov/2.0));
        matrix[0][1] = 0;
        matrix[0][2] = width / 2.0;

        matrix[1][0] = 0;
        matrix[1][1] = (width / 2.0) / Math.tan(Math.toRadians(fov/2.0));
        matrix[1][2] = height / 2.0;

        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = 1;


//        double ratio = width / 1280.0;
//
//
//        fx *= ratio;
//        fy *= ratio;
//        cx *= ratio;
//        cy *= ratio;
//
//        matrix[0][0] = fx;
//        matrix[0][1] = 0;
//        matrix[0][2] = cx;
//
//        matrix[1][0] = 0;
//        matrix[1][1] = fy;
//        matrix[1][2] = cy;
//
//        matrix[2][0] = 0;
//        matrix[2][1] = 0;
//        matrix[2][2] = 1;


        return matrix;
    }

    public void updateFov(double fov){
        this.fov = fov;
        matrix = null;
    }
}
