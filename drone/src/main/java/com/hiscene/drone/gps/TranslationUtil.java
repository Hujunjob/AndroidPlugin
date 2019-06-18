package com.hiscene.drone.gps;

/**
 * Created by hujun on 2019/4/23.
 * 用于矩阵转换计算
 */

public class TranslationUtil {
    /**
     * 绕任意轴旋转某个角度时的旋转矩阵
     *
     * @param u 旋转单位向量在x方向的分量
     * @param v 旋转单位向量在y方向的分量
     * @param w 旋转单位向量在z方向的分量
     * @param a 旋转角度，单位弧度
     * @return
     */
    @Tested
    public static double[][] rotateByAxle(double u, double v, double w, double a) {
        double[][] m = new double[3][3];

        double cos = Math.cos(a);
        double sin = Math.sin(a);

        m[0][0] = square(u) + (1 - square(u)) * cos;
        m[0][1] = u * v * (1 - cos) - w * sin;
        m[0][2] = u * w * (1 - cos) + v * sin;

        m[1][0] = u * v * (1 - cos) + w * sin;
        m[1][1] = square(v) + (1 - square(v)) * cos;
        m[1][2] = v * w * (1 - cos) - u * sin;

        m[2][0] = u * w * (1 - cos) - v * sin;
        m[2][1] = v * w * (1 - cos) + u * sin;
        m[2][2] = square(w) + (1 - square(w)) * cos;

        return m;
    }

    /**
     * 求平方
     *
     * @param x
     * @return
     */
    private static double square(double x) {
        return x * x;
    }

    /**
     * 和右手螺旋方向相反
     *
     * @param roll 弧度
     * @return
     */
    @Tested
    public static double[][] rotateRoll(double roll) {
        double m[][] = new double[3][3];
        m[0][0] = 1;
        m[0][1] = 0;
        m[0][2] = 0;

        m[1][0] = 0;
        m[1][1] = Math.cos(roll);
        m[1][2] = -Math.sin(roll);

        m[2][0] = 0;
        m[2][1] = Math.sin(roll);
        m[2][2] = Math.cos(roll);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Math.abs(m[i][j]) < 1e-10) {
                    m[i][j] = 0;
                }
            }
        }
        return m;
    }

    /**
     * 和右手螺旋方向相反
     *
     * @param pitch 弧度
     * @return
     */
    @Tested
    public static double[][] rotatePitch(double pitch) {
        double m[][] = new double[3][3];
        m[0][0] = Math.cos(pitch);
        m[0][1] = 0;
        m[0][2] = Math.sin(pitch);

        m[1][0] = 0;
        m[1][1] = 1;
        m[1][2] = 0;

        m[2][0] = -Math.sin(pitch);
        m[2][1] = 0;
        m[2][2] = Math.cos(pitch);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Math.abs(m[i][j]) < 1e-10) {
                    m[i][j] = 0;
                }
            }
        }

        return m;
    }

    /**
     * 和右手螺旋方向相反
     *
     * @param yaw 弧度
     * @return
     */
    @Tested
    public static double[][] rotateYaw(double yaw) {
        double[][] m = new double[3][3];
        m[0][0] = Math.cos(yaw);
        m[0][1] = -Math.sin(yaw);
        m[0][2] = 0;

        m[1][0] = Math.sin(yaw);
        m[1][1] = Math.cos(yaw);
        m[1][2] = 0;

        m[2][0] = 0;
        m[2][1] = 0;
        m[2][2] = 1;


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Math.abs(m[i][j]) < 1e-10) {
                    m[i][j] = 0;
                }
            }
        }

        return m;
    }


    /**
     * 3*3矩阵乘以3*1矩阵
     *
     * @param r
     * @param t
     * @return
     */
    @Tested
    public static double[] multiply3331(double[][] r, double[] t) {
        double[] m = new double[3];
        m[0] = r[0][0] * t[0] + r[0][1] * t[1] + r[0][2] * t[2];
        m[1] = r[1][0] * t[0] + r[1][1] * t[1] + r[1][2] * t[2];
        m[2] = r[2][0] * t[0] + r[2][1] * t[1] + r[2][2] * t[2];
        return m;
    }

    /**
     * 3*3矩阵乘以3*4矩阵
     *
     * @param k
     * @param r
     * @return
     */
    @Tested
    public static double[][] multiply3334(double[][] k, double[][] r) {
        double[][] m = new double[3][4];

        m[0][0] = k[0][0] * r[0][0] + k[0][1] * r[1][0] + k[0][2] * r[2][0];
        m[0][1] = k[0][0] * r[0][1] + k[0][1] * r[1][1] + k[0][2] * r[2][1];
        m[0][2] = k[0][0] * r[0][2] + k[0][1] * r[1][2] + k[0][2] * r[2][2];
        m[0][3] = k[0][0] * r[0][3] + k[0][1] * r[1][3] + k[0][2] * r[2][3];


        m[1][0] = k[1][0] * r[0][0] + k[1][1] * r[1][0] + k[1][2] * r[2][0];
        m[1][1] = k[1][0] * r[0][1] + k[1][1] * r[1][1] + k[1][2] * r[2][1];
        m[1][2] = k[1][0] * r[0][2] + k[1][1] * r[1][2] + k[1][2] * r[2][2];
        m[1][3] = k[1][0] * r[0][3] + k[1][1] * r[1][3] + k[1][2] * r[2][3];

        m[2][0] = k[2][0] * r[0][0] + k[2][1] * r[1][0] + k[2][2] * r[2][0];
        m[2][1] = k[2][0] * r[0][1] + k[2][1] * r[1][1] + k[2][2] * r[2][1];
        m[2][2] = k[2][0] * r[0][2] + k[2][1] * r[1][2] + k[2][2] * r[2][2];
        m[2][3] = k[2][0] * r[0][3] + k[2][1] * r[1][3] + k[2][2] * r[2][3];

        return m;
    }

    /**
     * 3*4矩阵乘以4*1矩阵
     *
     * @param m
     * @param t
     * @return
     */
    @Tested
    public static double[] multiply3441(double[][] m, double[] t) {
        double[] r = new double[3];

        r[0] = m[0][0] * t[0] + m[0][1] * t[1] + m[0][2] * t[2] + m[0][3] * t[3];
        r[1] = m[1][0] * t[0] + m[1][1] * t[1] + m[1][2] * t[2] + m[1][3] * t[3];
        r[2] = m[2][0] * t[0] + m[2][1] * t[1] + m[2][2] * t[2] + m[2][3] * t[3];

        return r;
    }

    /**
     * 两个3×3矩阵相乘
     *
     * @param m1
     * @param m2
     * @return 返回3×3矩阵
     */
    @Tested
    public static double[][] matrixMultiply33(double[][] m1, double[][] m2) {
        double[][] m = new double[3][3];

        m[0][0] = m1[0][0] * m2[0][0] + m1[0][1] * m2[1][0] + m1[0][2] * m2[2][0];
        m[0][1] = m1[0][0] * m2[0][1] + m1[0][1] * m2[1][1] + m1[0][2] * m2[2][1];
        m[0][2] = m1[0][0] * m2[0][2] + m1[0][1] * m2[1][2] + m1[0][2] * m2[2][2];

        m[1][0] = m1[1][0] * m2[0][0] + m1[1][1] * m2[1][0] + m1[1][2] * m2[2][0];
        m[1][1] = m1[1][0] * m2[0][1] + m1[1][1] * m2[1][1] + m1[1][2] * m2[2][1];
        m[1][2] = m1[1][0] * m2[0][2] + m1[1][1] * m2[1][2] + m1[1][2] * m2[2][2];

        m[2][0] = m1[2][0] * m2[0][0] + m1[2][1] * m2[1][0] + m1[2][2] * m2[2][0];
        m[2][1] = m1[2][0] * m2[0][1] + m1[2][1] * m2[1][1] + m1[2][2] * m2[2][1];
        m[2][2] = m1[2][0] * m2[0][2] + m1[2][1] * m2[1][2] + m1[2][2] * m2[2][2];

        return m;
    }


    /**
     *
     * @param m1
     * @param m2
     * @return
     */
    @Tested
    public static double[][] multiply4444(double[][] m1,double[][] m2){
        double[][] m = new double[4][4];

        m[0][0] = m1[0][0]*m2[0][0] + m1[0][1]*m2[1][0] +m1[0][2]*m2[2][0] +m1[0][3]*m2[3][0];
        m[0][1] = m1[0][0]*m2[0][1] + m1[0][1]*m2[1][1] +m1[0][2]*m2[2][1] +m1[0][3]*m2[3][1];
        m[0][2] = m1[0][0]*m2[0][2] + m1[0][1]*m2[1][2] +m1[0][2]*m2[2][2] +m1[0][3]*m2[3][2];
        m[0][3] = m1[0][0]*m2[0][3] + m1[0][1]*m2[1][3] +m1[0][2]*m2[2][3] +m1[0][3]*m2[3][3];

        m[1][0] = m1[1][0]*m2[0][0] + m1[1][1]*m2[1][0] +m1[1][2]*m2[2][0] +m1[1][3]*m2[3][0];
        m[1][1] = m1[1][0]*m2[0][1] + m1[1][1]*m2[1][1] +m1[1][2]*m2[2][1] +m1[1][3]*m2[3][1];
        m[1][2] = m1[1][0]*m2[0][2] + m1[1][1]*m2[1][2] +m1[1][2]*m2[2][2] +m1[1][3]*m2[3][2];
        m[1][3] = m1[1][0]*m2[0][3] + m1[1][1]*m2[1][3] +m1[1][2]*m2[2][3] +m1[1][3]*m2[3][3];

        m[2][0] = m1[2][0]*m2[0][0] + m1[2][1]*m2[1][0] +m1[2][2]*m2[2][0] +m1[2][3]*m2[3][0];
        m[2][1] = m1[2][0]*m2[0][1] + m1[2][1]*m2[1][1] +m1[2][2]*m2[2][1] +m1[2][3]*m2[3][1];
        m[2][2] = m1[2][0]*m2[0][2] + m1[2][1]*m2[1][2] +m1[2][2]*m2[2][2] +m1[2][3]*m2[3][2];
        m[2][3] = m1[2][0]*m2[0][3] + m1[2][1]*m2[1][3] +m1[2][2]*m2[2][3] +m1[2][3]*m2[3][3];

        m[3][0] = m1[3][0]*m2[0][0] + m1[3][1]*m2[1][0] +m1[3][2]*m2[2][0] +m1[3][3]*m2[3][0];
        m[3][1] = m1[3][0]*m2[0][1] + m1[3][1]*m2[1][1] +m1[3][2]*m2[2][1] +m1[3][3]*m2[3][1];
        m[3][2] = m1[3][0]*m2[0][2] + m1[3][1]*m2[1][2] +m1[3][2]*m2[2][2] +m1[3][3]*m2[3][2];
        m[3][3] = m1[3][0]*m2[0][3] + m1[3][1]*m2[1][3] +m1[3][2]*m2[2][3] +m1[3][3]*m2[3][3];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (Math.abs(m[i][j])<1e-10){
                    m[i][j]=0;
                }
            }
        }

        return m;
    }


    /**
     * 4*4矩阵乘以4*1矩阵
     *
     * @param m
     * @param t
     * @return
     */
    @Tested
    public static double[] matrixMultiply4441(double[][] m, double[] t) {
        double[] m1 = new double[4];

        m1[0] = m[0][0] * t[0] + m[0][1] * t[1] + m[0][2] * t[2] + m[0][3] * t[3];
        m1[1] = m[1][0] * t[0] + m[1][1] * t[1] + m[1][2] * t[2] + m[1][3] * t[3];
        m1[2] = m[2][0] * t[0] + m[2][1] * t[1] + m[2][2] * t[2] + m[2][3] * t[3];
        m1[3] = m[3][0] * t[0] + m[3][1] * t[1] + m[3][2] * t[2] + m[3][3] * t[3];

        return m1;
    }

    /**
     * 矩阵转置
     *
     * @param m 原矩阵
     * @return
     */
    @Tested
    public static double[][] transpose(double[][] m) {
        int row = m.length;
        int col = m[0].length;
        double[][] t = new double[col][row];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                t[j][i] = m[i][j];
            }
        }
        return t;
    }


//    public static double[][] multiply3444(double[][] m, double[][] r, double[] t) {
//        double[][] mm = new double[3][4];
//
//        mm[0][0] = m[0][0] * r[0][0] + m[0][1] * r[1][0] + m[0][2] * r[2][0];
//        mm[0][1] = m[0][0] * r[0][0] + m[0][1] * r[1][0] + m[0][2] * r[2][0];
//        mm[0][2] = m[0][0] * r[0][1] + m[0][1] * r[1][1] + m[0][2] * r[2][1];
//
//        mm[0][3] = m[0][0] * t[0] + m[0][1] * t[1] + m[0][2] * t[2] + m[0][3];
//
//        mm[1][0] = m[1][0] * r[0][0] + m[1][1] * r[1][0] + m[1][2] * r[2][0];
//        mm[1][1] = m[1][0] * r[0][0] + m[1][1] * r[1][0] + m[1][2] * r[2][0];
//        mm[1][2] = m[1][0] * r[0][1] + m[1][1] * r[1][1] + m[1][2] * r[2][1];
//
//        mm[1][3] = m[1][0] * t[0] + m[1][1] * t[1] + m[1][2] * t[2] + m[1][3];
//
//        mm[2][0] = m[2][0] * r[0][0] + m[2][1] * r[1][0] + m[2][2] * r[2][0];
//        mm[2][1] = m[2][0] * r[0][0] + m[2][1] * r[1][0] + m[2][2] * r[2][0];
//        mm[2][2] = m[2][0] * r[0][1] + m[2][1] * r[1][1] + m[2][2] * r[2][1];
//
//        mm[2][3] = m[2][0] * t[0] + m[2][1] * t[1] + m[2][2] * t[2] + m[2][3];
//        return mm;
//    }

    /**
     * 3*4矩阵乘以4*4矩阵
     *
     * @param k
     * @param m
     * @return
     */
    @Tested
    public static double[][] multiply3444(double[][] k, double[][] m) {
        double[][] mm = new double[3][4];

        mm[0][0] = k[0][0] * m[0][0] + k[0][1] * m[1][0] + k[0][2] * m[2][0] + k[0][3] * m[3][0];
        mm[0][1] = k[0][0] * m[0][1] + k[0][1] * m[1][1] + k[0][2] * m[2][1] + k[0][3] * m[3][1];
        mm[0][2] = k[0][0] * m[0][2] + k[0][1] * m[1][2] + k[0][2] * m[2][2] + k[0][3] * m[3][2];
        mm[0][3] = k[0][0] * m[0][3] + k[0][1] * m[1][3] + k[0][2] * m[2][3] + k[0][3] * m[3][3];

        mm[1][0] = k[1][0] * m[0][0] + k[1][1] * m[1][0] + k[1][2] * m[2][0] + k[1][3] * m[3][0];
        mm[1][1] = k[1][0] * m[0][1] + k[1][1] * m[1][1] + k[1][2] * m[2][1] + k[1][3] * m[3][1];
        mm[1][2] = k[1][0] * m[0][2] + k[1][1] * m[1][2] + k[1][2] * m[2][2] + k[1][3] * m[3][2];
        mm[1][3] = k[1][0] * m[0][3] + k[1][1] * m[1][3] + k[1][2] * m[2][3] + k[1][3] * m[3][3];

        mm[2][0] = k[2][0] * m[0][0] + k[2][1] * m[1][0] + k[2][2] * m[2][0] + k[2][3] * m[3][0];
        mm[2][1] = k[2][0] * m[0][1] + k[2][1] * m[1][1] + k[2][2] * m[2][1] + k[2][3] * m[3][1];
        mm[2][2] = k[2][0] * m[0][2] + k[2][1] * m[1][2] + k[2][2] * m[2][2] + k[2][3] * m[3][2];
        mm[2][3] = k[2][0] * m[0][3] + k[2][1] * m[1][3] + k[2][2] * m[2][3] + k[2][3] * m[3][3];

        return mm;
    }

    /**
     * 4*3 3*1
     * @param m
     * @param t
     * @return
     */
    @Tested
    public static double[] multiply4331(double[][] m,double[] t){
        double[] m1 = new double[4];
        m1[0] = m[0][0] * t[0] + m[0][1]*t[1] + m[0][2]*t[2];
        m1[1] = m[1][0] * t[0] + m[1][1]*t[1] + m[1][2]*t[2];
        m1[2] = m[2][0] * t[0] + m[2][1]*t[1] + m[2][2]*t[2];
        m1[3] = m[3][0] * t[0] + m[3][1]*t[1] + m[3][2]*t[2];
        return m1;
    }

    /**
     * 产生3*3单位阵
     *
     * @return
     */
    public static double[][] eye33() {
        double[][] m = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };
        return m;
    }

    /**
     * 产生4*4单位阵
     *
     * @return
     */
    public static double[][] eye44() {
        double[][] m = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        return m;
    }

    /**
     * 产生num阶单位阵
     *
     * @param num
     * @return
     */
    public static double[][] eye(int num) {
        double[][] m = new double[num][num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                m[i][j] = 0;
            }
        }
        for (int i = 0; i < num; i++) {
            m[i][i] = 1;
        }
        return m;
    }

    /**
     * 求矩阵的逆矩阵
     *
     * @param m
     * @return
     */
    @Tested
    public static double[][] reverseMatrix(double[][] m) {
        Matrix matrix = new Matrix(m);
        Matrix reMatrix = MatrixUtil.getReMatrix(matrix);

        double[][] mm = matrixMultiply33(matrix.getValue(), reMatrix.getValue());
        reMatrix.multiple(1.0 / mm[0][0]);

        return reMatrix.getValue();
    }

    /**
     * 计算投影矩阵
     *
     * @param intrinsic 相机内参
     * @param pitch     旋转角度，弧度
     * @param roll      旋转角度，弧度
     * @param yaw       旋转角度，弧度
     * @param x         平移
     * @param y         平移
     * @param z         平移
     * @return
     */
    public static double[][] projectionMatrix(double[][] intrinsic, double pitch, double roll, double yaw, double x, double y, double z) {
        double[][] m = rotateAndTransMatrix(pitch, roll, yaw, x, y, z);
        return multiply3334(intrinsic, m);
    }

    /**
     * 获取无人机移动和旋转后的矩阵
     *
     * @param pitch
     * @param roll
     * @param yaw
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static double[][] rotateAndTransMatrix(double pitch, double roll, double yaw, double x, double y, double z) {
        double C[] = {x, y, z};

        //求旋转矩阵
        //首先绕Z轴旋转
        double[][] mYaw = rotateYaw(yaw);
        //然后求得无人机旋转后的新y轴向量
        double[] axleY = {1, 0, 0};
        double[] newAxle = multiply3331(mYaw, axleY);

        double[][] rotateNewY = rotateByAxle(newAxle[0], newAxle[1], newAxle[2], pitch);

        double Rc[][] = matrixMultiply33(rotateNewY, mYaw);

        double RcT[][] = transpose(Rc);

        double r[][] = RcT;

        double t[] = multiply3331(r, C);
        for (int i = 0; i < t.length; i++) {
            t[i] = -t[i];
        }

        double[][] m = new double[3][4];
        m[0][0] = r[0][0];
        m[0][1] = r[0][1];
        m[0][2] = r[0][2];

        m[1][0] = r[1][0];
        m[1][1] = r[1][1];
        m[1][2] = r[1][2];

        m[2][0] = r[2][0];
        m[2][1] = r[2][1];
        m[2][2] = r[2][2];

        m[0][3] = t[0];
        m[1][3] = t[1];
        m[2][3] = t[2];
        return m;
    }

    /**
     * 计算rotateAndTransMatrix的逆矩阵
     *
     * @param pitch
     * @param roll
     * @param yaw
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static double[][] reRotateAndTranslation(double pitch, double roll, double yaw, double x, double y, double z) {
        double C[] = {x, y, z};

        //求旋转矩阵
        //首先绕Z轴旋转
        double[][] mYaw = rotateYaw(yaw);
        //然后求得无人机旋转后的新y轴向量
        double[] axleY = {1, 0, 0};
        double[] newAxle = multiply3331(mYaw, axleY);

        double[][] rotateNewY = rotateByAxle(newAxle[0], newAxle[1], newAxle[2], pitch);

        double Rc[][] = matrixMultiply33(rotateNewY, mYaw);

        double[][] m = eye44();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                m[i][j] = Rc[i][j];
            }
        }
        m[0][3] = C[0];
        m[1][3] = C[1];
        m[2][3] = C[2];
        return m;
    }

    /**
     * 利用投影矩阵计算世界坐标系->相机图像坐标系
     *
     * @param transMatrix 投影矩阵
     * @param xw          世界坐标系x
     * @param yw          世界坐标系y
     * @param zw          世界坐标系z
     * @return
     */
    public static double[] translateWorldToImage(double[][] transMatrix, double xw, double yw, double zw) {
        double[] t = {xw, yw, zw, 1};
        double[] image = multiply3441(transMatrix, t);
        return image;
    }

//    /**
//     * 利用逆矩阵，计算相机图像坐标系->世界坐标系
//     * @param transMatrix 相机投影矩阵
//     * @param x
//     * @param y
//     * @return
//     */
//    public static double[] translateImageToWorld(double[][] transMatrix, double x, double y){
//        double[] t = {x,y,1};
//
//        //将3*4的投影矩阵扩充到4*4的矩阵
//        double[][] transMatrix2 = eye44();
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 4; j++) {
//                transMatrix2[i][j] = transMatrix[i][j];
//            }
//        }
//
//        double[][] reMatrix = MatrixUtil.getReMatrix(new Matrix(transMatrix2)).getValue();
//
//
//
//        double[] world =
//    }


    @Tested
    public static String toString(double[][] m) {
        StringBuilder builder = new StringBuilder();
        int col = m[0].length;
        int row = m.length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                builder.append(m[i][j]).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Tested
    public static String toString(double[] m) {
        StringBuilder builder = new StringBuilder();
        for (double i : m) {
            builder.append(i).append(" ");
        }
        builder.append("\n");
        return builder.toString();
    }

}

