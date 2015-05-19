package tests;

import obj.AnalyseMat;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by Sa User on 19.04.2015.
 */
public class MainTest {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        Mat m=new Mat();

    }
    public static float[][] times (float[][] A, float[][] B) {
        if (A[0].length != B.length) {
            throw new IllegalArgumentException("inner dimensions must agree.");
        }
        int m = A.length, n = A[0].length, Bm = B.length, Bn = B[0].length;
        float[][] C = new float[m][Bn];
        float[] colj = new float[n];
        for (int j = 0; j < Bn; j++) {
            for (int k = 0; k < n; k++) {
                colj[k] = B[k][j];
            }
            for (int i = 0; i < m; i++) {
                float[] rowi = A[i];
                float s = 0;
                for (int k = 0; k < n; k++) {
                    s += rowi[k] * colj[k];
                }
                C[i][j] = s;
            }
        }
        return C;
    }
}
