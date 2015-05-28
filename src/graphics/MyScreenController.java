package graphics;

import bluetooth.EchoServer;
import com.example.AndroidTests2.obj.CameraFrame;
import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import sun.applet.*;
import tests.CalibChessBoard;

import java.util.Iterator;

/**
 * Created by Sa User on 04.04.2015.
 */
public class MyScreenController extends AbstractAppState implements ScreenController {
    @Override
    public void bind(Nifty nifty, Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    int i = 1;

    public void setImageAndAngle() {


        i += 1;
        // MainWindow.app.translateGeometry(new Vector3f(i, 0f, 0f), MainWindow.ROT, MainWindow.app.pivotCamera);
       // Mat input = Highgui.imread("C:\\Users\\Sa User\\IdeaProjects\\JMonkeyTest3\\assets\\chess2"+i+".jpg", Highgui.CV_LOAD_IMAGE_COLOR);
        //new CalibChessBoard().calibrateCamera(input);
      //  MainWindow.app.setNiftyImage(input);
            MainWindow.app.setImageAndAngle();
        // MainWindow.app.camera.rotate(i, 0f, 0f);
        //MainWindow.app.pivotCamera.setLocalRotation(new Quaternion().fromAngles(i, 0f, 0f));

    }

    public void create3D(){
        MainWindow.app.create3DModel();
    }

    public void make3dInVisible(){
        MainWindow.app.make3DInVisible();
    }
}
