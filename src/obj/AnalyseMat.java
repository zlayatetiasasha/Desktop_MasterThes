package obj;

import graphics.MainWindow;
import org.opencv.core.*;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sa User on 13.04.2015.
 */
public class AnalyseMat {
    public Mat mat;
    public Mat descriptors;
    public MatOfKeyPoint keyPoints;
    public MatOfDMatch matchesWithNextMat;

    public List<Point> matchPoints;
    public Point[] matchPoints_array;
    public Mat epipolarLines;

    public AnalyseMat(Mat mat, MatOfKeyPoint keyPoints, Mat descriptors) {
        this.mat = mat;
        this.descriptors = descriptors;
        this.keyPoints = keyPoints;
    }


    static DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

    public static MatOfDMatch matchDescriptors(PairOfFrames pair) {
        AnalyseMat oneMat = pair.frame.analyseMat, anotherMat = pair.frame1.analyseMat;
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(oneMat.descriptors, anotherMat.descriptors, matches);

        List<KeyPoint> kp1 = oneMat.keyPoints.toList();
        List<KeyPoint> kp2 = anotherMat.keyPoints.toList();

        List<Point> pts1 = new ArrayList<Point>(), pts2 = new ArrayList<Point>();
        List<DMatch> good_matches = new ArrayList<DMatch>();
        List<DMatch> matches_list = matches.toList();
        for (int i = 0; i < oneMat.descriptors.rows(); i++) {
            good_matches.add(matches_list.get(i));
            pts2.add(kp2.get(matches_list.get(i).trainIdx).pt);
            pts1.add(kp1.get(matches_list.get(i).queryIdx).pt);
        }


        //  List<DMatch> matches_list = matches.toList();
/*      double max_dist = 0;
        double min_dist = 100;
        for (int i = 0; i < oneMat.descriptors.rows(); i++) {
            double dist = matches_list.get(i).distance;
            if (dist < min_dist) min_dist = dist;
            if (dist > max_dist) max_dist = dist;
        }
       // List<KeyPoint> kp1 = oneMat.keyPoints.toList();
     //   List<KeyPoint> kp2 = anotherMat.keyPoints.toList();

 //      List<Point> pts1 = new ArrayList<Point>(), pts2 = new ArrayList<Point>();
 //      List<DMatch> good_matches = new ArrayList<DMatch>();
       for (int i = 0; i < oneMat.descriptors.rows(); i++) {
            if (matches_list.get(i).distance <= Math.max(3 * min_dist, 0.02)) {
                good_matches.add(matches_list.get(i));
                pts2.add(kp2.get(matches_list.get(i).trainIdx).pt);
                pts1.add(kp1.get(matches_list.get(i).queryIdx).pt);
            }
        }
        */

        //without fiiltering by distance
        //Fundamental Mat by Opencv
        // -0,000002 -0,000085 0,053682
        //  0,000089 0,000002 -0,051115
        //   -0,060792 0,053784 1,000000

        //with filtering
        //0,000004 0,000091 -0,082523
        // -0,000092 -0,000002 0,058961
        // 0,087654 -0,062426 1,000000

        oneMat.matchPoints_array = new Point[pts1.size()];
        anotherMat.matchPoints_array = new Point[pts2.size()];
        pts1.toArray(oneMat.matchPoints_array);
        pts2.toArray(anotherMat.matchPoints_array);

        oneMat.matchPoints = pts1;
        anotherMat.matchPoints = pts2;

        MatOfDMatch good = new MatOfDMatch();
        good.fromList(good_matches);

        pair.allMatches = good;

        return good;


    }


    public static MatOfDMatch filterMatchesByAlgorithmFundamentalMat(PairOfFrames pair) {
        AnalyseMat oneMat = pair.frame.analyseMat;
        AnalyseMat anotherMat = pair.frame1.analyseMat;
        List<KeyPoint> kp1 = oneMat.keyPoints.toList();
        List<KeyPoint> kp2 = anotherMat.keyPoints.toList();

        List<Point> pts1 = new ArrayList<Point>(), pts2 = new ArrayList<Point>();
        List<DMatch> good_matches = new ArrayList<DMatch>();
        List<DMatch> matches_list = pair.allMatches.toList();

        Mat F = pair.algorithmFundamentalMat;


        for (int i = 0; i < oneMat.descriptors.rows(); i++) {
            Point p1 = pair.frame.analyseMat.matchPoints_array[i], p2 = pair.frame1.analyseMat.matchPoints_array[i];

            Mat mp1, mp2;
            mp1 = new Mat(1, 3, CvType.CV_64FC1);
            mp1.put(0, 0, p1.x);
            mp1.put(0, 1, p1.y);
            mp1.put(0, 2, 1.0);

            mp2 = new Mat(3, 1, CvType.CV_64FC1);
            mp2.put(0, 0, p2.x);
            mp2.put(1, 0, p2.y);
            mp2.put(2, 0, 1.0);

            Mat temp = new Mat();
            Core.gemm(mp1, F, 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, temp);

            Mat result = new Mat();
            Core.gemm(temp, mp2, 1, Mat.zeros(1, 1, CvType.CV_64FC1), 1, result);
            // GyroUtils.printMat32F(result, "m^T * F * m'=");

            double d = result.get(0, 0)[0];
            if (Math.abs(d) == 0.0f) {
                good_matches.add(matches_list.get(i));
                pts2.add(kp2.get(matches_list.get(i).trainIdx).pt);
                pts1.add(kp1.get(matches_list.get(i).queryIdx).pt);
            }
            //  double d=result.get(0,0)[0];
            // if(d==0.0){
            //       good_matches.add(matches_list.get(i));
            //         pts2.add(kp2.get(matches_list.get(i).trainIdx).pt);
            //        pts1.add(kp1.get(matches_list.get(i).queryIdx).pt);
            //   }
        }

        //   oneMat.matchPoints_array = new Point[pts1.size()];
        //   anotherMat.matchPoints_array = new Point[pts2.size()];
        //   pts1.toArray(oneMat.matchPoints_array);
        //   pts2.toArray(anotherMat.matchPoints_array);

        //   oneMat.matchPoints = pts1;
        // anotherMat.matchPoints = pts2;

        MatOfDMatch good = new MatOfDMatch();
        good.fromList(good_matches);

        Mat img_matches_cv = new Mat();
        Features2d.drawMatches(oneMat.mat, oneMat.keyPoints, anotherMat.mat, anotherMat.keyPoints, good, img_matches_cv, new Scalar(0.1, 0.2, 0.3),
                new Scalar(0.1, 0.2, 0.3),
                new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);

        MainWindow.app.setImage_cv(img_matches_cv);

        return good;
        // GyroUtils.printMat32F(result,"m^T * F * m' = ");
    }

    static int i = 0;

    public static MatOfDMatch filterMatchesByGyroFundamentalMat(PairOfFrames pair) {
        AnalyseMat oneMat = pair.frame.analyseMat;
        AnalyseMat anotherMat = pair.frame1.analyseMat;
        List<KeyPoint> kp1 = oneMat.keyPoints.toList();
        List<KeyPoint> kp2 = anotherMat.keyPoints.toList();

        List<Point> pts1 = new ArrayList<Point>(), pts2 = new ArrayList<Point>();
        List<DMatch> good_matches = new ArrayList<DMatch>();
        List<DMatch> matches_list = pair.allMatches.toList();

        BufferedWriter writer=createEpipolarConstraintFile(i);
        Mat F = pair.myFundamentalMat;
        writeMatrix(writer,F,"My Fundamental Matrix");
        writeEpipolarConstraintResult(writer,0.001f+"","epsilon=");


        Mat E = pair.E;

        for (int i = 0; i < pair.frame.analyseMat.matchPoints_array.length; i++) {
            Point p1 = pair.frame.analyseMat.matchPoints_array[i], p2 = pair.frame1.analyseMat.matchPoints_array[i];
          /*
            Mat mp1=new Mat(3, 1, CvType.CV_64FC1);
            Core.gemm(pair.myInternal,mp1k, 1, Mat.zeros(3,1,CvType.CV_64FC1),1,mp1);


            mp2k=new Mat(3, 1, CvType.CV_64FC1);
            mp2k.put(0, 0, p2.x);
            mp2k.put(1, 0, p2.y);
            mp2k.put(2, 0, 1.0);

            Mat mp2=new Mat(3, 1, CvType.CV_64FC1);
            Core.gemm(pair.myInternal,mp2k, 1, Mat.zeros(3, 1, CvType.CV_64FC1), 1, mp2);

            Mat temp = new Mat();
            Core.gemm(mp1.t(), E , 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, temp);
            Mat result = new Mat();
            Core.gemm(temp, mp2, 1, Mat.zeros(1, 1, CvType.CV_64FC1), 1, result);
*/

            Mat mp1, mp2;

            mp1 = new Mat(3, 1, CvType.CV_64FC1);
            mp1.put(0, 0, p1.x);
            mp1.put(1, 0, p1.y);
            mp1.put(2, 0, 1.0);

            mp2 = new Mat(3, 1, CvType.CV_64FC1);
            mp2.put(0, 0, p2.x);
            mp2.put(1, 0, p2.y);
            mp2.put(2, 0, 1.0);

            Mat mp1k = new Mat(), mp2k = new Mat();
            Mat K = pair.myInternal.inv();
            Core.gemm(mp1.t(), K.t(), 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, mp1k);
            Mat m = new Mat();
            Core.gemm(mp1k, E, 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, m);
            Mat m1 = new Mat();
            Core.gemm(m, K, 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, m1);

            // Core.gemm(K, mp1,1,Mat.zeros(3,1,CvType.CV_64FC1),1,mp1k);
            // Core.gemm(K, mp2,1,Mat.zeros(3,1,CvType.CV_64FC1),1,mp2k);

            Mat temp = new Mat();
            //  Core.gemm(mp1k, E, 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, temp);
            //  Core.gemm(mp1k.t(), E, 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, temp);
            Mat result = new Mat();
            Core.gemm(m1, mp2, 1, Mat.zeros(1, 1, CvType.CV_64FC1), 1, result);
            //  Core.gemm(temp, mp2k, 1, Mat.zeros(1, 1, CvType.CV_64FC1), 1, result);



            double d = result.get(0, 0)[0];
            writeEpipolarConstraintResult(writer, "{"+p1.x+", "+p1.y+"}", "p1=");
            writeEpipolarConstraintResult(writer, "{"+p2.x+", "+p2.y+"}", "p2=");
            writeEpipolarConstraintResult(writer, d+"", "p1*F*p2 = ");
            writeEpipolarConstraintResult(writer, "--------------", "p1*F*p2");
            //System.out.println("m*F*m1=" + d);
            if (Math.abs(d) < 0.001f) {
                good_matches.add(matches_list.get(i));
                pts2.add(kp2.get(matches_list.get(i).trainIdx).pt);
                pts1.add(kp1.get(matches_list.get(i).queryIdx).pt);
            }
            //  double d=result.get(0,0)[0];
            // if(d==0.0){
            //       good_matches.add(matches_list.get(i));
            //         pts2.add(kp2.get(matches_list.get(i).trainIdx).pt);
            //        pts1.add(kp1.get(matches_list.get(i).queryIdx).pt);
            //   }
        }

        try {
            writer.close();
        }catch(Exception ex){ex.printStackTrace();}

        pair.frame.analyseMat.matchPoints_array = new Point[pts1.size()];
        pair.frame1.analyseMat.matchPoints_array = new Point[pts2.size()];
        pts1.toArray(oneMat.matchPoints_array);
        pts2.toArray(anotherMat.matchPoints_array);

        pair.frame.analyseMat.matchPoints = pts1;
        pair.frame1.analyseMat.matchPoints = pts2;

        MatOfDMatch good = new MatOfDMatch();
        good.fromList(good_matches);

        pair.my_matches = good_matches;

        Mat img_matches = new Mat();
        Features2d.drawMatches(oneMat.mat, oneMat.keyPoints, anotherMat.mat, anotherMat.keyPoints, good, img_matches, Scalar.all(-1),
                Scalar.all(-1),
                new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);

        MainWindow.app.setImage(img_matches);
        saveImageToFile(i++, img_matches, pair);

        return good;
        // GyroUtils.printMat32F(result,"m^T * F * m' = ");
    }


    static String folder="slychayanaya_zapis";

    public static void saveImageToFile(int n, Mat m, PairOfFrames pair) {

        Highgui.imwrite("C:\\Users\\Sa User\\IdeaProjects\\JMonkeyTest3\\tests\\" + folder + "\\test" + n + ".jpg", m);
        try {
            BufferedWriter writer = new BufferedWriter
                    (new FileWriter("C:\\Users\\Sa User\\IdeaProjects\\JMonkeyTest3\\tests\\" + folder + "\\test" + n + "_matrices.txt"));


            writeMatrix(writer, pair.R,"Rotation matrix");
            writeMatrix(writer, pair.T, "Translation");
            writeMatrix(writer, pair.myFundamentalMat, "My Fundamental mat");

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public static void writeMatrix(BufferedWriter writer, Mat m, String title) {
        try {
            writer.append("-------");
            writer.newLine();
            writer.append(title);
            writer.newLine();
            for (int i = 0; i < m.rows(); i++) {
                for (int j = 0; j < m.cols(); j++) {
                    double[] temp = m.get(i, j);
                   writer.append(String.format("%.6f", temp[0]) + " ");
                }
                writer.newLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static BufferedWriter createEpipolarConstraintFile(int n){
        try {
            BufferedWriter writer = new BufferedWriter
                    (new FileWriter("C:\\Users\\Sa User\\IdeaProjects\\JMonkeyTest3\\tests\\" + folder + "\\test" + n + "_epipolarConstraintRes"+".txt"));
            return writer;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static void writeEpipolarConstraintResult(BufferedWriter writer, String text, String title) {
        try {
            writer.append(title + " "+text);
            writer.newLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


}
