package com.hiscene.drone.gps;

/**
 * Created by hujun on 2019/5/12.
 */

public class MatrixUtil {

    /**
     *       * 求矩阵在i,j处余子式
     *       * @param mat
     *       * @param i
     *       * @param j
     *       * @return
     *
     */
    public static Matrix getComplementMinor(Matrix mat, int i, int j) {
        //创建一个新的矩阵用于接收表示该余子式，需删除本行本列的数值
        Matrix m = new Matrix(mat.getRow() - 1, mat.getCol() - 1);
        //用于遍历新矩阵m的变量
        int row = 0, col = 0;
                 /*
14          * 遍历原矩阵的数据，j2表示行,k表示列
15          */
        for (int j2 = 0; j2 < mat.getRow(); j2++) {
            //在第i行除的数据省略
            if (j2 == i) {
                continue;
            }
            for (int k = 0; k < mat.getCol(); k++) {
                //在第j列的数据省略
                if (k == j) {
                    continue;
                }
                //赋值
                m.setValue(row, col, mat.getValue(j2, k));
                //遍历新矩阵的变量
                col++;
                if (col >= m.getCol()) {
                    col = 0;
                    row++;
                }
            }
        }
        return m;
    }

    /**
     * 求矩阵的行列式的值
     * @param mat
     * @return
     */
    public static double getMatrixValue(Matrix mat) {
        if(mat.getRow() != mat.getCol()) {
            System.out.println("该矩阵不是方阵，没有行列式");
            return Double.MIN_VALUE;
        }
        //若为1*1矩阵则直接返回
        if(mat.getRow() == 1) {
            return mat.getValue(0, 0);
        }
        //若为2*2矩阵则直接计算返回结果
        if(mat.getRow() == 2) {
            return mat.getValue(0, 0)*mat.getValue(1, 1) - mat.getValue(0, 1)*mat.getValue(1, 0);
        }
        //行列式的值
        double matrixValue = 0;
        for (int i = 0; i < mat.getCol(); i++) {
            //获取0，i位置的余子式，即第一行的余子式
            Matrix m = getComplementMinor(mat, 0, i);
            //将第一行的余子式相加 ，递归下去
            matrixValue += Math.pow(-1, i) * getMatrixValue(m);

        }
        return matrixValue;
    }

    /**
     * 求矩阵的伴随矩阵
     * @param mat
     * @return
     */
    public static Matrix getWithMatrix(Matrix mat) {
        //创建一个矩阵存放伴随矩阵的值
        Matrix withMatrix = new Matrix(mat.getRow(),mat.getCol());
        //遍历withMatrix存放对应的mat的值
        for (int i = 0; i < withMatrix.getRow(); i++) {
            for (int j = 0; j < withMatrix.getCol(); j++) {
                double temp = Math.pow(-1, i+j) * MatrixUtil.getMatrixValue(MatrixUtil.getComplementMinor(mat, j, i));
                if(Math.abs(temp) <= 10e-6) {
                    temp = 0;
                }
                withMatrix.setValue(i, j,temp);
            }
        }
        //返回结果
        return withMatrix;
    }

    /**
     * 求逆矩阵
     * @param mat
     * @return
     */
    @Tested
    public static Matrix getReMatrix(Matrix mat) {
        //创建一个矩阵接收逆矩阵数据
        Matrix reMatrix = new Matrix(mat.getRow(),mat.getCol());
        //得到原矩阵行列式的值
        double value = MatrixUtil.getMatrixValue(mat);
        //判断矩阵行列式的值是否为零
        if(Math.abs(value) <= 10e-6) {
            System.out.println("该矩阵不可逆！");
            return null;
        }
        //将原矩阵mat赋值除以原行列式的值value给逆矩阵
        for (int i = 0; i < reMatrix.getRow(); i++) {
            for (int j = 0; j < reMatrix.getCol(); j++) {
                reMatrix.setValue(i, j, MatrixUtil.getWithMatrix(mat).getValue(i, j) / value);
            }
        }
        return reMatrix;
    }



}
