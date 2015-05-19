package obj;

import com.example.AndroidTests2.obj.AccelVectorPath;
import com.example.AndroidTests2.obj.CameraFrame;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Sa User on 15.05.2015.
 */
public class FilterImages {
    public static ArrayList<PairOfFrames> filterImages(ArrayList<CameraFrame> frames) {
        ArrayList<PairOfFrames> result = new ArrayList<PairOfFrames>();
        for (int i = 0; i < frames.size(); i++) {
           /* if (i + 1 < frames.size()) {
                PairOfFrames pair = new PairOfFrames(frames.get(i), frames.get(i + 1));
                if (pair.comparePaths() == 0) {
                    //translation between frames == 0
                    //get previous and next frames

                 /*   if (i - 1 >= 0) {
                        PairOfFrames pair1 = new PairOfFrames(frames.get(i - 1), frames.get(i));
                        if (pair1.comparePaths() != 0) {
                            result.add(pair1);
                        }
                    }
*/
             /*       if (i + 2 < frames.size()) {
                        PairOfFrames pair2 = new PairOfFrames(frames.get(i + 1), frames.get(i + 2));
                        if(pair2.comparePaths()!=0) {
                            result.add(pair2);
                        }
                    }
                }
            }*/

            if (i + 1 < frames.size()) {
                result.add(new PairOfFrames(frames.get(i), frames.get(i + 1)));
            }
        }
        return result;
    }
}
