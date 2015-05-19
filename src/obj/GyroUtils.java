package obj;

import com.example.AndroidTests2.obj.AccelVectorPath;
import com.example.AndroidTests2.obj.CameraFrame;
import com.example.AndroidTests2.obj.GyroVectorAngle;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;
import tests.CalibChessBoard;

import java.awt.*;
import java.awt.List;
import java.util.*;

/**
 * Created by Sa User on 18.04.2015.
 */
public class GyroUtils {

    public static void calculateGyroFundamentalMat(PairOfFrames pair) {

        Mat C = calculateExternalCalibrationMat(pair);
        //printMat32F(C, "C ( My external)");


        Mat K = calculateInternalCalibrationMat(pair);
        //printMat32F(K, "K (My internal)");

        Mat E = calculateEssentialMat(pair);
        printMat32F(E, "My Essential Matrix");

        Mat F = calculateFundamentalMatByEssential(pair);
        printMat32F(F, "My Fundamental Matrix");

       // calculateHomography(pair);
      //  printMat32F(pair.Homography, "My Homography");

        //Mat z= makeOneMatrix();
        //Mat opt=makeZeroMatrix();

        //  Mat C1 = C.inv();
        //  printMat32F(C1,"C^-1");

        // the projection matrix is defined as P = KT,
        //K = intrisic
        //     [r11, r12, r13, t1;
        // T =  r21, r22, r23, t2;
        //      r31, r32, r33, t3]
        //(T=R|t)

        //The rotation vector unit is radians
        // and the translation vector unit depends on the unit of the 3D points you use, it could be meters, yards, inches, etc.

        //F=K.t() * E * K'^-1
        // Mat E = K.t() * F * K; //according to HZ (9.12) K-internal matrix, F-fundamental
    }


    private static Mat calculateExternalCalibrationMat(PairOfFrames pair) {
        //Matrix to show the angle between
        Mat Ri = new Mat(3, 3, CvType.CV_64FC1), Rj = new Mat(3, 3, CvType.CV_64FC1), Rk = new Mat(3, 3, CvType.CV_64FC1);

        GyroVectorAngle angle = pair.frame.angle;
        GyroVectorAngle angle1 = pair.frame1.angle;

    /*    GyroVectorAngle actualAngle = angle1.minus(angle);
        double x = actualAngle.x;
        double y = actualAngle.y;
        double z = actualAngle.z;


   //     System.out.println("Rotation: " + x + ", " + y + ", " + z);
     /*   if(x==0.0 && y==0.0 && z==0.0){
            Mat I = new Mat(3, 3, CvType.CV_64FC1);
            I.put(0, 0, 1);
            I.put(0, 1, 0);
            I.put(0, 2, 0);
            I.put(1, 0, 0);
            I.put(1, 1, 1);
            I.put(1, 2, 0);
            I.put(2, 0, 0);
            I.put(2, 1, 0);
            I.put(2, 2, 1);

            pair.R = I;

        } else {*/
        //   System.out.println("Actual Angle in Rad = " + actualAngle.toString());

 /*      Ri.put(0, 0, 1.0);
        Ri.put(0, 1, 0.0);
        Ri.put(0, 2, 0.0);
        Ri.put(1, 0, 0.0);
        Ri.put(1, 1, Math.cos(x));
        Ri.put(1, 2, -Math.sin(x));
        Ri.put(2, 0, 0);
        Ri.put(2, 1, Math.sin(x));
        Ri.put(2, 2, Math.cos(x));

        Rj.put(0, 0, Math.cos(y));
        Rj.put(0, 1, 0.0);
        Rj.put(0, 2, Math.sin(y));
        Rj.put(1, 0, 0.0);
        Rj.put(1, 1, 1.0);
        Rj.put(1, 2, 0.0);
        Rj.put(2, 0, -Math.sin(y));
        Rj.put(2, 1, 0.0);
        Rj.put(2, 2, Math.cos(y));

        Rk.put(0, 0, Math.cos(z));
        Rk.put(0, 1, -Math.sin(z));
        Rk.put(0, 2, 0.0);
        Rk.put(1, 0, Math.sin(z));
        Rk.put(1, 1, Math.cos(z));
        Rk.put(1, 2, 0.0);
        Rk.put(2, 0, 0.0);
        Rk.put(2, 1, 0.0);
        Rk.put(2, 2, 1.0);

        pair.Ri = Ri;
        pair.Rj = Rj;
        pair.Rk = Rk;

        printMat32F(Ri, "Ri");
        printMat32F(Rj, "Rj");
        printMat32F(Rk, "Rk");

        //int size = (int) (Ri.total() * Ri.channels());
        //double[] temp = new double[size]; // use double[] instead of byte[]
        //  System.out.println("Ri:");
        //     printMat32F(Ri);

        //      System.out.println("Rj:");
        //    printMat32F(Rj);

        //      System.out.println("Rk:");
//        printMat32F(Rk);

        Mat opt = Mat.zeros(3, 3, CvType.CV_64FC1);

        Mat mtemp = new Mat(3, 3, CvType.CV_64FC1);
        Core.gemm(Ri, Rj, 1, opt, 1, mtemp);

        Mat R = new Mat(3, 3, CvType.CV_64FC1);
        Core.gemm(mtemp, Rk, 1, opt, 1, R);
*/

        Mat R1 = new Mat(3, 3, CvType.CV_64FC1);
        Mat R2 = new Mat(3, 3, CvType.CV_64FC1);
        Mat R = new Mat(3, 3, CvType.CV_64FC1);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                R1.put(i, j, pair.frame.currentRotation[i][j]);
                R2.put(i, j, pair.frame1.currentRotation[i][j]);
            }
        }
        Core.gemm(R2, R1.inv(), 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, R);
        printMat32F(R, "Rotation matrix");

        //  if (x == 0 && y == 0 && z == 0) {
      /*      R = new Mat(3, 3, CvType.CV_64FC1);
            R.put(0, 0, 1);
            R.put(0, 1, 0);
            R.put(0, 2, 0);
            R.put(1, 0, 0);
            R.put(1, 1, 1);
            R.put(1, 2, 0);
            R.put(2, 0, 0);
            R.put(2, 1, 0);
            R.put(2, 2, 1);*/
        // }

        pair.R = R;
        //    System.out.println("R:");
        //    printMat32F(R);
        //    }
        AccelVectorPath path = pair.frame.path;
        AccelVectorPath path1 = pair.frame1.path;

        AccelVectorPath actualPath = path1.minus(path);

        Mat Tr = new Mat(1, 3, CvType.CV_64FC1);
        Tr.put(0, 0, actualPath.x * 1000.0);
        Tr.put(0, 1, actualPath.y * 1000.0);
        Tr.put(0, 2, actualPath.z * 1000.0);
        //  Tr.put(0, 1, 0.0);
        //  Tr.put(0, 2, 0.0);


        pair.T = Tr;
        //    System.out.println("Translation:");
        //     printMat32F(Tr);

        Mat G = new Mat(4, 4, CvType.CV_64FC1);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                G.put(i, j, pair.R.get(i, j));
            }
        }
        G.put(3, 0, 0);
        G.put(3, 1, 0);
        G.put(3, 2, 0);

        G.put(0, 3, Tr.get(0, 0));
        G.put(1, 3, Tr.get(0, 1));
        G.put(2, 3, Tr.get(0, 2));

        G.put(3, 3, 1);

        pair.External = G;
        //     System.out.println("G:");
        //     printMat32F(G);

        return G;
    }

    private static Mat calculateInternalCalibrationMat(PairOfFrames pair) {
        Mat K = new Mat(3, 3, CvType.CV_64FC1);

        float a, b, u0, v0, f, k, l, sizew, sizeh;
        //resolution=640*480 -> pixel size = 0.007089 (0.00139*5.1), zoom=1 -> focal length=3.5
        f = 3.5f; //focal length
        k = 0.007089f; //pixel size
        l = 0.007089f; //pixel size
        //sizew = 540f; //size of screen
        //sizeh = 960f; //size of screen
        sizew = 640f - 1f;
        sizeh = 480f - 1f;

        a = f / k;
        b = f / l;
        u0 = sizew / 2f;
        v0 = sizeh / 2f;

        K.put(0, 0, a);
        K.put(0, 1, 0);
        K.put(0, 2, u0);
        K.put(1, 0, 0);
        K.put(1, 1, b);
        K.put(1, 2, v0);
        K.put(2, 0, 0);
        K.put(2, 1, 0);
        K.put(2, 2, 1);

        pair.myInternal = K;
        return K;
    }

    private static Mat calculateEssentialMat(PairOfFrames pair) {
        Mat E = new Mat(3, 3, CvType.CV_64FC1);
        Mat tx = new Mat(3, 3, CvType.CV_64FC1);

        Mat R = pair.R;

        Mat t = pair.T;

        double t1, t2, t3;
        t1 = t.get(0, 0)[0];
        t2 = t.get(0, 1)[0];
        t3 = t.get(0, 2)[0];

        printMat32F(t, "Translation between frames");


    /*    if (t1 == 0 && t2 == 0 && t3 == 0) {
            tx.put(0, 0, 1);
            tx.put(0, 1, 0);
            tx.put(0, 2, 0);
            tx.put(1, 0, 0);
            tx.put(1, 1, 1);
            tx.put(1, 2, 0);
            tx.put(2, 0, 0);
            tx.put(2, 1, 0);
            tx.put(2, 2, 1);
        } else {*/
        tx.put(0, 0, 0);
        tx.put(0, 1, -t3);
        tx.put(0, 2, t2);
        tx.put(1, 0, t3);
        tx.put(1, 1, 0);
        tx.put(1, 2, -t1);
        tx.put(2, 0, -t2);
        tx.put(2, 1, t1);
        tx.put(2, 2, 0);
        //    }

        printMat32F(tx, "My T matrix");
        Core.gemm(tx, R, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, E);
        //   Core.gemm(I, tx, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, E);
        //   E=R;

        pair.E = E;

        return E;

    }

    public Mat makeMatrixX(double t1, double t2, double t3) {
        Mat tx = new Mat(3, 3, CvType.CV_64FC1);
        tx.put(0, 0, 0);
        tx.put(0, 1, -t3);
        tx.put(0, 2, t2);
        tx.put(1, 0, t3);
        tx.put(1, 1, 0);
        tx.put(1, 2, -t1);
        tx.put(2, 0, -t2);
        tx.put(2, 1, t1);
        tx.put(2, 2, 0);
        return tx;
    }

    private static Mat calculateFundamentalMatByEssential(PairOfFrames pair) {
        //F=K.t().inv() * E * K'^-1
        Mat temp = new Mat(3, 3, CvType.CV_64FC1);
        Mat K = pair.myInternal.t();
        K = K.inv();
        Core.gemm(K, pair.E, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, temp);

        Mat F = new Mat(3, 3, CvType.CV_64FC1);
        Mat Ki = pair.myInternal.inv();
        Core.gemm(temp, Ki, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, F);

        pair.myFundamentalMat = F;

        return F;

    }


    public static void testGoodMatches(PairOfFrames pair) {
        java.util.List<KeyPoint> kp1 = pair.frame.analyseMat.keyPoints.toList();
        java.util.List<KeyPoint> kp2 = pair.frame1.analyseMat.keyPoints.toList();
        Mat F = pair.myFundamentalMat;
        Mat K = new Mat(3, 4, CvType.CV_64FC1);
        for (int i = 0; i < pair.myInternal.rows(); i++) {
            for (int j = 0; j < pair.myInternal.cols(); j++) {
                K.put(i, j, pair.myInternal.get(i, j));
            }
        }
        K.put(0, 3, 0);
        K.put(1, 3, 0);
        K.put(2, 3, 0);

        for (int i = 0; i < pair.good_matches_by_cv.size(); i++) {
            DMatch m = pair.good_matches_by_cv.get(i);
            org.opencv.core.Point p1 = kp1.get(m.queryIdx).pt;
            org.opencv.core.Point p2 = kp2.get(m.trainIdx).pt;

            Mat mp1, mp2;

            mp1 = new Mat(1, 3, CvType.CV_64FC1);
            mp1.put(0, 0, p1.x);
            mp1.put(0, 1, p1.y);
            mp1.put(0, 2, 1.0);

            mp2 = new Mat(3, 1, CvType.CV_64FC1);
            mp2.put(0, 0, p2.x);
            mp2.put(1, 0, p2.y);
            mp2.put(2, 0, 1.0);


            Mat X1_temp = new Mat();
            Mat x2 = new Mat();

            Mat I = new Mat(3, 3, CvType.CV_64FC1);
            I.put(0, 0, 1);
            I.put(0, 1, 0);
            I.put(0, 2, 0);
            I.put(1, 0, 0);
            I.put(1, 1, 1);
            I.put(1, 2, 0);
            I.put(2, 0, 0);
            I.put(2, 1, 0);
            I.put(2, 2, 1);

            Mat calib = new Mat();
            Core.gemm(pair.myInternal, I, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, calib);

            Core.gemm(mp1, calib.inv(), 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, X1_temp);

            Mat Ext = new Mat(3, 4, CvType.CV_64FC1);
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 4; k++) {
                    Ext.put(i, j, pair.External.get(j, k));
                }

            }

            Mat calib1 = new Mat();
            Core.gemm(pair.myInternal, Ext, 1, Mat.zeros(3, 4, CvType.CV_64FC1), 1, calib1);

            Mat X1 = new Mat(4, 1, CvType.CV_64FC1);
            X1.put(0, 0, X1_temp.get(0, 0));
            X1.put(1, 0, X1_temp.get(0, 1));
            X1.put(2, 0, X1_temp.get(0, 2));
            X1.put(3, 0, 1);

            //printMat32F(X1, "M1");
            Core.gemm(calib1, X1, 1, Mat.zeros(3, 1, CvType.CV_64FC1), 1, x2);
            x2.put(0, 0, x2.get(0, 0)[0] / x2.get(2, 0)[0]);
            x2.put(1, 0, x2.get(1, 0)[0] / x2.get(2, 0)[0]);
            x2.put(2, 0, x2.get(2, 0)[0] / x2.get(2, 0)[0]);

            // printMat32F(x2, "m2 = (R|t * M1)");
            //  printMat32F(mp2, "m2 from picture");

        }
    }

    public static void calculateHomography(PairOfFrames pair) {
        Mat H = new Mat(3, 3, CvType.CV_64FC1);
        /*Column 1 of Homography is column (r11 r21 r31) of Pose.
          Column 2 of Homography is column (r12 r22 r32) of Pose.
          Column 3 of Homography is column (t1 t2 t3) of Pose.

          Then normalize dividing everything by t3.

          Now that you have homography, project the points.
           Your 2d points are x,y. Add them a z=1, so they are now 3d. Project them as follows:
           p=[x y 1];
           p=Homography*p;   //project
           p= p / p(z);      //normalize

*/

        H.put(0, 0, pair.R.get(0, 0)[0] / pair.T.get(0, 2)[0]);
        H.put(1, 0, pair.R.get(1, 0)[0] / pair.T.get(0, 2)[0]);
        H.put(2, 0, pair.R.get(2, 0)[0] / pair.T.get(0, 2)[0]);

        H.put(0, 1, pair.R.get(0, 1)[0] / pair.T.get(0, 2)[0]);
        H.put(1, 1, pair.R.get(1, 1)[0] / pair.T.get(0, 2)[0]);
        H.put(2, 1, pair.R.get(2, 1)[0] / pair.T.get(0, 2)[0]);

        H.put(0, 2, pair.T.get(0, 0)[0] / pair.T.get(0, 2)[0]);
        H.put(1, 2, pair.T.get(0, 1)[0] / pair.T.get(0, 2)[0]);
        H.put(2, 2, pair.T.get(0, 2)[0] / pair.T.get(0, 2)[0]);

        pair.Homography = H;
    }

    public static Mat calculte3DPoint(PairOfFrames pair, Point p1, Point p2) {
        Mat p3d = new Mat(1, 3, CvType.CV_64FC1);
        p3d.put(0, 0, p1.x);
        p3d.put(0, 1, p1.y);
        p3d.put(0, 2, 0.0);

        Mat p3d1 = new Mat(1, 3, CvType.CV_64FC1);
        Core.gemm(p3d, pair.Homography, 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, p3d1);
        return p3d1;

    }


    public static void printMat32F(Mat m, String title) {
        System.out.println("---");
        System.out.println(title);
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.cols(); j++) {
                double[] temp = m.get(i, j);
                System.out.print(String.format("%.6f", temp[0]) + " ");
            }
            System.out.println();
        }
    }
}
