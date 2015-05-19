package bluetooth;

/**
 * Created by Sa User on 28.03.2015.
 */

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.AndroidTests2.obj.CameraFrame;
import com.sun.javaws.Main;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import graphics.MainWindow;
import obj.FilterImages;
import obj.PairOfFrames;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Class that implements an SPP Server which accepts single line of
 * message from an SPP client
 */
public class EchoServer {

    public static final String MOVE = "mov";
    public static final String ROTATE = "rot";

    public static final float CENTER_X = 20f;
    public static final float CENTER_Y = 7f;
    public static final float SCALE_X = 10;
    public static final float SCALE_Y = 10;

    float x, y, z;

    private static ArrayList<CameraFrame> frames;
    public static ArrayList<PairOfFrames> pairs;

    //start server
    public void startServer() throws IOException {
        //ExcelFileWriter excelWriter = new ExcelFileWriter("TestGyroscope-move.xls");

        //Create a UUID for SPP
        //  UUID uuid = new UUID("1101", true);

        //  UUID uuid = new UUID("1001", true);
        UUID uuid = new UUID("11001", true);
        //Create the service url
        String connectionString = "btspp://localhost:" + uuid + ";name=Sample SPP Server";

        //open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        StreamConnection connection = streamConnNotifier.acceptAndOpen();

        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        System.out.println("connected");
        MainWindow.app.setTextProgress("Connected device! Waiting for images to display...");
        //read string from spp client

        InputStream inStream = connection.openInputStream();
//        DataInputStream stream = new DataInputStream(inStream);

        ObjectInputStream stream = new ObjectInputStream(new DataInputStream(inStream));


        while (true) {
            try {
                //    stream = new ObjectInputStream(new DataInputStream(inStream));
                Object o = stream.readObject();

                frames = (ArrayList<CameraFrame>) o;
                pairs = FilterImages.filterImages(frames);
                //CameraFrame c = (frames.iterator().next());

                MainWindow.app.setTextProgress("Got some images! Click Next!");
                //MainWindow.app.iterator=frames.iterator();
                // MainWindow.app.current_frame= (CameraFrame)MainWindow.app.iterator.next();


                //  Mat m = Highgui.imdecode(new MatOfByte(c.buf), 1);
                //  Core.flip(m.t(), m, 1);
                //   MainWindow.app.setImage(m);
                //    MainWindow.app.setCameraView(c.angle);

            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }


        }


    }


}