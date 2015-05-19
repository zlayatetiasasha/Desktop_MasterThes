package obj;

import graphics.MainWindow;
import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Sa User on 12.05.2015.
 */
public class ReconstructionUtils {
    public static Mat createDisparityMap(PairOfFrames pair){
        Mat left=pair.frame.analyseMat.mat;
       // left=left.t();
        Imgproc.cvtColor(left, left, Imgproc.COLOR_BGR2GRAY);
        Mat right=pair.frame1.analyseMat.mat;
       // right=right.t();
        Imgproc.cvtColor(right, right, Imgproc.COLOR_BGR2GRAY);
        StereoBM stereo = new StereoBM(1, 16 /*ndisp*/, 15/*sadSize*/);
        Mat disparity=new Mat();
        stereo.compute(right, left, disparity);
       // Mat disp8=new Mat();
       // Core.normalize(disparity, disp8, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);

        return disparity;

    }

    public static Mat createDisparityMap(){
        Mat left=Highgui.imread("C:\\Users\\Sa User\\IdeaProjects\\JMonkeyTest3\\assets\\Yeuna9x.png");
     //   left=left.t();
        Imgproc.cvtColor(left, left, Imgproc.COLOR_BGR2GRAY);
        Mat right=Highgui.imread("C:\\Users\\Sa User\\IdeaProjects\\JMonkeyTest3\\assets\\SuXT483.png");
      //  right=right.t();
        Imgproc.cvtColor(right, right, Imgproc.COLOR_BGR2GRAY);
        StereoBM stereo = new StereoBM(1, 16 /*ndisp*/, 15/*sadSize*/);

        Mat disparity=new Mat();
        stereo.compute(right,left,disparity);

        Mat disp8=new Mat();
        Core.normalize(disparity, disp8, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
       // Imgproc.cvtColor(disp8, disp8, Imgproc.COLOR_GRAY2RGB);
        return disparity;

    }

    public void main(String[]args){


    }

}
