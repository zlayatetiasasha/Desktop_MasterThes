
package tests;

import com.example.AndroidTests2.obj.CameraFrame;
import obj.GyroUtils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 * <p/>
 * This application handles a video stream and perform all the needed steps for
 * calibrating a camera.
 */

import java.io.File;
import java.util.ArrayList;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class CalibChessBoard {
    int flagsCorner = Calib3d.CALIB_CB_ADAPTIVE_THRESH
            | Calib3d.CALIB_CB_FAST_CHECK
            | Calib3d.CALIB_CB_NORMALIZE_IMAGE;
    int flagsCalib = Calib3d.CALIB_ZERO_TANGENT_DIST
            | Calib3d.CALIB_FIX_PRINCIPAL_POINT
            | Calib3d.CALIB_FIX_K4
            | Calib3d.CALIB_FIX_K5;
    TermCriteria criteria = new TermCriteria(TermCriteria.EPS
            + TermCriteria.MAX_ITER, 40, 0.001);
    Size winSize = new Size(5, 5), zoneSize = new Size(-1, -1);
    Size patternSize;
    ArrayList objectPoints, imagePoints = new ArrayList();
    ArrayList vCorners;
    ArrayList<Mat> vImg;
    Mat cameraMatrix = Mat.eye(3, 3, CvType.CV_64F);
    Mat distCoeffs = Mat.zeros(8, 1, CvType.CV_64F);
    ArrayList rvecs = new ArrayList();
    ArrayList tvecs = new ArrayList();

    CalibChessBoard() {
    }

    CalibChessBoard(Size patternSize) {
        this.patternSize = patternSize;
    }

    boolean getCorners(Mat gray, MatOfPoint2f corners) {
        if (!Calib3d.findChessboardCorners(gray, patternSize,
                corners, flagsCorner))
            return false;
        Imgproc.cornerSubPix(gray, corners, winSize, zoneSize,
                criteria);
        Calib3d.drawChessboardCorners(gray, patternSize, corners, true);
        testMat=gray;
        return true;

    }

    MatOfPoint3f getCorner3f() {
        MatOfPoint3f corners3f = new MatOfPoint3f();
       // double squareSize = 30;
        double squareSize=25;
        Point3[] vp = new Point3[(int) (patternSize.height *
                patternSize.width)];
        int cnt = 0;
        for (int i = 0; i < patternSize.height; ++i)
            for (int j = 0; j < patternSize.width; ++j, cnt++)
                vp[cnt] = new Point3(j * squareSize,
                        i * squareSize, 0.0d);
        corners3f.fromArray(vp);
        return corners3f;
    }

    public static void main(String[] args) {
        test0();
    }

    public static void test0() {
        CalibChessBoard cb = new CalibChessBoard(new Size(9, 6));
    //    CalibChessBoard cb = new CalibChessBoard(new Size(13, 12));
        cb.getAllCornors("C:\\Users\\Sa User\\IdeaProjects\\JMonkeyTest3\\assets\\calibrate\\new_640_480_andr_GOOD");
        cb.calibrate();
    }

    void calibrate() {
        double errReproj = Calib3d.calibrateCamera(objectPoints,
                imagePoints,vImg.get(0).size(), cameraMatrix,
                distCoeffs, rvecs, tvecs,flagsCalib);
        System.out.println("done, \nerrReproj = " + errReproj);
        //GyroUtils.printMat32F(cameraMatrix,"CameraMatrix");
     //   System.out.println("cameraMatrix = \n" + cameraMatrix.dump());
        System.out.println("distCoeffs = \n" + distCoeffs.dump());
    }

    public static Mat testMat;
    void getAllCornors(String path) {
        vImg = new ArrayList();
        objectPoints = new ArrayList();
        imagePoints = new ArrayList();
        MatOfPoint3f corners3f = getCorner3f();
        for (File f : new File(path).listFiles()) {
            Mat mat = Highgui.imread(f.getPath(),
                    Highgui.CV_LOAD_IMAGE_COLOR);
            if (mat == null || mat.channels() != 3)
                continue;
            System.out.println("fn = " + f.getPath());
            System.out.println("mat.channels() = " + mat.channels()
                    + ", " + mat.cols() + ", " + mat.rows());
            Mat gray = new Mat();

           // mat=mat.t();
            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
            MatOfPoint2f corners = new MatOfPoint2f();
            if (!getCorners(gray, corners))
                continue;
            objectPoints.add(corners3f);
            imagePoints.add(corners);
            vImg.add(mat);
        }
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static Mat getCalibrationMat(){
        //for resolution=640*480, zoom=1
       /* 501,343816 0,000000 319,500000
        0,000000 501,862379 239,500000
        0,000000 0,000000 1,000000*/

        Mat K=new Mat(3, 3, CvType.CV_64FC1);

        K.put(0, 0, 501,343816);
        K.put(0, 1, 0);
        K.put(0, 2, 319,5);
        K.put(1, 0, 0);
        K.put(1, 1, 501,862379);
        K.put(1, 2,239,5);
        K.put(2, 0, 0);
        K.put(2, 1,0);
        K.put(2, 2, 1);

        return K;
    }
}




 class CalibChessBoard1 {
    Mat input;


    /**
     * The effective camera calibration, to be performed once in the program
     * execution
     */
    private Mat intrinsic = new Mat();
    private List<Mat> imagePoints = new ArrayList<Mat>();
    private List<Mat> objectPoints = new ArrayList<Mat>();
    private MatOfPoint3f obj;
    private Mat savedImage;
    private Mat distCoeffs = new Mat();
    private MatOfPoint2f imageCorners;
    private int numCornersHor = 9;
    private int numCornersVer = 6;

    public Mat calibrateCamera(Mat input1) {
        input = input1;
        //  input = Highgui.imread("C:\\Users\\Sa User\\IdeaProjects\\JMonkeyTest3\\assets\\chessboard.png", Highgui.CV_LOAD_IMAGE_COLOR);

        Mat f = findAndDrawPoints(input);
        this.obj = new MatOfPoint3f();

        int numSquares = this.numCornersHor * this.numCornersVer;
        float squareSize = 2.5f;
        for (int i = 0; i < this.numCornersVer; ++i)
            for (int j = 0; j < this.numCornersHor; ++j)
                obj.push_back(new MatOfPoint3f(new Point3(((float) j) * squareSize, ((float) i) * squareSize, 0.0f)));

        this.imagePoints.add(imageCorners);
        this.objectPoints.add(obj);

      //  calibrate();

     //   undistort();

        return input;
    }

    private Mat findAndDrawPoints(Mat frame) {
        // init
        Mat grayImage = new Mat();
        imageCorners = new MatOfPoint2f();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
        // the size of the chessboard
        Size boardSize = new Size(this.numCornersHor, this.numCornersVer);
        // look for the inner chessboard corners
        boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
                Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
        // all the required corners have been found...
        if (found) {
            // optimization
            TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
            Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);
            // save the current frame for further elaborations

            // show the chessboard inner corners on screen
            Calib3d.drawChessboardCorners(input, boardSize, imageCorners, found);

            // enable the option for taking a snapshot
        }

        return frame;
    }

    public void undistort(Mat input) {
        Mat undistored = new Mat();
        Imgproc.undistort(input, undistored, intrinsic, distCoeffs);
        input=undistored;
    }

    public void calibrate() {
        // init needed variables according to OpenCV docs
        List<Mat> rvecs = new ArrayList<Mat>();
        List<Mat> tvecs = new ArrayList<Mat>();
        intrinsic.put(0, 0, 1);
        intrinsic.put(1, 1, 1);
        // calibrate!
        Calib3d.calibrateCamera(objectPoints, imagePoints, input.size(), intrinsic, distCoeffs, rvecs, tvecs);


        for (int i = 0; i < intrinsic.rows(); i++) {
            for (int j = 0; j < intrinsic.cols(); j++) {
                System.out.print(String.format("%.4f", intrinsic.get(i, j)[0]) + "\t ");
            }
            System.out.println();
        }

        System.out.println("------------------------");
        // you cannot take other snapshot, at this point...

    }


}
