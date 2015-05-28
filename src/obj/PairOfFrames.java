package obj;

import com.example.AndroidTests2.obj.AccelVectorPath;
import com.example.AndroidTests2.obj.CameraFrame;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.features2d.DMatch;

import java.util.List;

/**
 * Created by Sa User on 19.04.2015.
 */
public class PairOfFrames {
    public CameraFrame frame;
    public CameraFrame frame1;

    public Mat Ri, Rj, Rk, R, T, E;
    public Mat myFundamentalMat, algorithmFundamentalMat;
    public Mat External, myInternal;
    public Mat algorithmInternal;
    public MatOfDMatch allMatches;
    public List<DMatch> good_matches_by_cv;
    public Mat disparityMap;

    public List<DMatch> my_matches;

    public Mat Homography;

    public PairOfFrames(CameraFrame frame, CameraFrame frame1){
        this.frame=frame;
        this.frame1=frame1;
    }

    public int comparePaths(){
        AccelVectorPath f=this.frame.path;
        AccelVectorPath f1=this.frame1.path;

        AccelVectorPath r=f1.minus(f);
        if(r.x==0.0f && r.y==0.0f && r.z==0.0f){
            return 0;
        }
        else{
            return 1;
        }
    }

}
