package graphics;

import bluetooth.EchoServer;
import com.example.AndroidTests2.obj.CameraFrame;
import com.example.AndroidTests2.obj.GyroVectorAngle;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.*;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.niftygui.RenderImageJme;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.*;
import com.jme3.scene.shape.*;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.texture.*;
import com.jme3.texture.Image;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;
import com.jme3.util.SkyFactory;
import bluetooth.init.Init;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.*;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.Color;
import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import obj.AnalyseMat;
import obj.GyroUtils;
import obj.PairOfFrames;
import org.lwjgl.opengl.ContextAttribs;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Sa User on 28.03.2015.
 */
public class MainWindow extends SimpleApplication implements ScreenController {
    public static MainWindow app;
    static Nifty nifty;
    private Sphere sphereMesh = new Sphere(32, 32, 10, false, true);
    private Geometry sphere = new Geometry("Sky", sphereMesh);
    static BulletAppState bulletAppState;
    static Screen screen;

    public static int camx, camy, camz;

    public static void main(String[] args) {
        app = new MainWindow();
        app.start();

    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public void simpleInitApp() {
        Init init = new Init();
        init.initLocalDevice();
        init.initServer();

        initializeAllFor3D();
    }

    public int pair_number = 0;
    public CameraFrame current_frame;
    public PairOfFrames current_pair;

    public void setImageAndAngle() {

        if (EchoServer.pairs != null) {
            set3DButtonEnabled(false);
            if (pair_number >= EchoServer.pairs.size()) {
                pair_number = 0;
            }

            PairOfFrames pair;


            pair = EchoServer.pairs.get(pair_number);
            current_pair = pair;
            //   byte buf[] = frame.buf;
            //      byte buf1[] = frame1.buf;

                  /*  Mat mat = new Mat();
                    mat.create(960, 720, CvType.CV_8UC1);
                    mat.put(0, 0, buf);
                    Highgui.imdecode(mat, 1);
                    */
            //     Mat m = Highgui.imdecode(new MatOfByte(buf), 1);
            //      Mat m1 = Highgui.imdecode(new MatOfByte(buf1), 1);

            //   Core.flip(m.t(), m, 1);
            //   AnalyseMat analyseMat = MainWindow.app.sift(m);
            //    m = analyseMat.mat;


            System.out.println("--------------------------------------------");
            //System.out.println("analysing frames # " + frame.frame_number + " and " + frame1.frame_number);
            Mat result;
            try {
                GyroUtils.calculateGyroFundamentalMat(pair);
                result = doDescriptorsMatching(pair);
                // findFundamentalMat(pair);
                if (pair.algorithmFundamentalMat != null) {
                    GyroUtils.printMat32F(pair.algorithmFundamentalMat, "Fundamental Mat by Opencv");
                }


                //  result = ReconstructionUtils.createDisparityMap(pair);
//                MainWindow.app.setImage_cv(result);
                //  MainWindow.app.setImage(frame.mat);
                //  MainWindow.app.setCameraView(frame.angle);
                set3DButtonEnabled(true);

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
        pair_number++;


    }


    public void setNiftyImage(Mat input) {
        NiftyImage nimage = convertMatToNiftyImage(input);
        Element niftyElement = nifty.getCurrentScreen().findElementByName("image");
// swap old with new image
        niftyElement.getRenderer(ImageRenderer.class).setImage(nimage);
    }

    public Mat doDescriptorsMatching(PairOfFrames pair) {

        Mat input = pair.frame.decodeToMat();
        Mat input1 = pair.frame1.decodeToMat();

        Core.flip(input.t(), input, 1);
        AnalyseMat anMat = sift(input);

        Core.flip(input1.t(), input1, 1);
        AnalyseMat anMat1 = sift(input1);

        pair.frame.analyseMat = anMat;
        pair.frame1.analyseMat = anMat1;

        MatOfDMatch matches = AnalyseMat.matchDescriptors(pair);


        findFundamentalMat(pair);


        matches = AnalyseMat.filterMatchesByGyroFundamentalMat(pair);

        // System.out.println(F);
        // Mat E = K.t() * F * K; //according to HZ (9.12)


        Mat img_matches = new Mat();


        return img_matches;
    }

    public Mat doDescriptorsMatching(Mat input, Mat input1) {
        Core.flip(input.t(), input, 1);
        AnalyseMat anMat = sift(input);

        Core.flip(input1.t(), input1, 1);
        AnalyseMat anMat1 = sift(input1);

        MatOfDMatch matches = null;
        //= AnalyseMat.matchDescriptors(anMat, anMat1);

      /*  KeyPoint[] points = anMat.keyPoints.toArray();
        KeyPoint[] points1 = anMat1.keyPoints.toArray();

       org.opencv.core.Point[] points_point = new Point[points.length];
        org.opencv.core.Point[] points_point1 = new Point[points1.length];
        for (int i = 0; i < points.length; i++) {
            points_point[i] = points[i].pt;
        }
        for (int i = 0; i < points1.length; i++) {
            points_point1[i] = points1[i].pt;
        }
*/


        MatOfPoint2f points2f = new MatOfPoint2f(anMat.matchPoints_array), points2f1 = new MatOfPoint2f(anMat1.matchPoints_array);

        Mat F = Calib3d.findFundamentalMat(points2f, points2f1, Calib3d.RANSAC, 0.1, 0.99);
        //System.out.println(F);
        // Mat E = K.t() * F * K; //according to HZ (9.12)

        Mat img_matches = new Mat();
        Features2d.drawMatches(anMat.mat, anMat.keyPoints, anMat1.mat, anMat1.keyPoints, matches, img_matches, new Scalar(0.1, 0.2, 0.3),
                new Scalar(0.1, 0.2, 0.3),
                new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);


        return img_matches;

    }

    public void findFundamentalMat(PairOfFrames pair) {
        AnalyseMat anMat, anMat1;
        anMat = pair.frame.analyseMat;
        anMat1 = pair.frame1.analyseMat;

        List<DMatch> matches_list = pair.allMatches.toList();
        double max_dist = 0;
        double min_dist = 100;
        for (int i = 0; i < anMat.descriptors.rows(); i++) {
            double dist = matches_list.get(i).distance;
            if (dist < min_dist) min_dist = dist;
            if (dist > max_dist) max_dist = dist;
        }
        List<KeyPoint> kp1 = anMat.keyPoints.toList();
        List<KeyPoint> kp2 = anMat1.keyPoints.toList();

        List<Point> pts1 = new ArrayList<Point>(), pts2 = new ArrayList<Point>();
        List<DMatch> good_matches = new ArrayList<DMatch>();

        for (int i = 0; i < anMat.descriptors.rows(); i++) {
            if (matches_list.get(i).distance <= Math.max(3 * min_dist, 0.02)) {
                good_matches.add(matches_list.get(i));
                pts2.add(kp2.get(matches_list.get(i).trainIdx).pt);
                pts1.add(kp1.get(matches_list.get(i).queryIdx).pt);
            }
        }
        Point[] matchPoints_array1 = new Point[pts1.size()];
        Point[] matchPoints_array2 = new Point[pts2.size()];
        pts1.toArray(matchPoints_array1);
        pts1.toArray(matchPoints_array2);

        MatOfPoint2f points2f = new MatOfPoint2f(matchPoints_array1), points2f1 = new MatOfPoint2f(matchPoints_array2);

        Mat F = Calib3d.findFundamentalMat(points2f, points2f1, Calib3d.FM_RANSAC, 0.1, 0.99);

        pair.algorithmFundamentalMat = F;

        //MatOfDMatch matches_cv = AnalyseMat.filterMatchesByAlgorithmFundamentalMat(pair);

      /*  List<Point> pts11 = new ArrayList<Point>(), pts22 = new ArrayList<Point>();

        for (int i = 0; i < matchPoints_array1.length; i++) {
            Point p1 = matchPoints_array1[i], p2 = matchPoints_array2[i];

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
                temp = new Mat();
                Core.gemm(mp1, pair.myFundamentalMat, 1, Mat.zeros(1, 3, CvType.CV_64FC1), 1, temp);

                result = new Mat();
                Core.gemm(temp, mp2, 1, Mat.zeros(1, 1, CvType.CV_64FC1), 1, result);
                GyroUtils.printMat32F(result, "m^T * my F * m'=");

                good_matches.add(matches_list.get(i));
                pts22.add(kp2.get(matches_list.get(i).trainIdx).pt);
                pts11.add(kp1.get(matches_list.get(i).queryIdx).pt);
            }
        }

*/
        MatOfDMatch good = new MatOfDMatch();
        good.fromList(good_matches);
        pair.good_matches_by_cv = good_matches;

        Mat img_matches_cv = new Mat();
        Features2d.drawMatches(anMat.mat, anMat.keyPoints, anMat1.mat, anMat1.keyPoints, good, img_matches_cv, new Scalar(0.1, 0.2, 0.3),
                new Scalar(0.1, 0.2, 0.3),
                new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);

        MainWindow.app.setImage_cv(img_matches_cv);
    }

    public Mat computeEpipolarGeometry(PairOfFrames pair) {
//        Mat lines = new Mat(), lines1 = new Mat();
//        try {
//
//            Calib3d.computeCorrespondEpilines(, 1, pair.algorithmFundamentalMat, lines);
//            Calib3d.computeCorrespondEpilines(/*pair.frame.analyseMat.keyPoints.reshape(1, 2)*/ , 1, pair.algorithmFundamentalMat, lines);
//            Calib3d.computeCorrespondEpilines(/*pair.frame1.analyseMat.keyPoints.reshape(1, 2)*/ , 2, pair.algorithmFundamentalMat, lines1);
//            pair.frame.analyseMat.epipolarLines = lines;
//            pair.frame1.analyseMat.epipolarLines = lines1;
//
//            Mat result = drawEpiLines(pair.frame.analyseMat.mat, pair.frame.analyseMat.epipolarLines, pair.frame1.analyseMat.epipolarLines);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public Mat drawEpiLines(Mat outImg, Mat epilinesSrc, Mat epilinesDst) {

        int epiLinesCount = epilinesSrc.rows();

        double a, b, c;

        for (int line = 0; line < epiLinesCount; line++) {
            a = epilinesSrc.get(line, 0)[0];
            b = epilinesSrc.get(line, 0)[1];
            c = epilinesSrc.get(line, 0)[2];

            int x0 = 0;
            int y0 = (int) (-(c + a * x0) / b);
            int x1 = outImg.cols() / 2;
            int y1 = (int) (-(c + a * x1) / b);

            Point p1 = new Point(x0, y0);
            Point p2 = new Point(x1, y1);
            Scalar color = new Scalar(255, 255, 255);
            Core.line(outImg, p1, p2, color);

        }

        for (int line = 0; line < epiLinesCount; line++) {
            a = epilinesDst.get(line, 0)[0];
            b = epilinesDst.get(line, 0)[1];
            c = epilinesDst.get(line, 0)[2];

            int x0 = outImg.cols() / 2;
            int y0 = (int) (-(c + a * x0) / b);
            int x1 = outImg.cols();
            int y1 = (int) (-(c + a * x1) / b);

            Point p1 = new Point(x0, y0);
            Point p2 = new Point(x1, y1);
            Scalar color = new Scalar(255, 255, 255);
            Core.line(outImg, p1, p2, color);

        }
        return outImg;
    }


    public static void showResult(Mat img) {
        Imgproc.resize(img, img, new Size(640, 480));
        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int n = 0;

    private static Screen createIntroScreen(final Nifty nifty, final ScreenController controller) {
        return new ScreenBuilder("start") {{
            controller(controller);
            layer(new LayerBuilder("layer") {{
                childLayoutVertical();
                backgroundColor(Color.WHITE);
                panel(new PanelBuilder("panelText") {{
                    childLayoutHorizontal();
                    text(new TextBuilder("textSensor") {{
                        text("Sensors");
                        style("nifty-label");
                        marginLeft("100");
                        color(Color.BLACK);
                    }});

                    text(new TextBuilder("textCv") {{
                        text("Opencv");
                        style("nifty-label");
                        marginLeft("500");
                        color(Color.BLACK);
                    }});
                }});


                panel(new PanelBuilder("panelImage") {{
                    childLayoutHorizontal();
                    //  width("400");
                    // width("540");
                    backgroundColor(Color.WHITE);

                    image(new ImageBuilder("image") {{
                        width("400");
                        height("470");
                        text("Sensors");
                        //width("540");
                        //   width("720");
                        //  height("1280");
                    }});


                    image(new ImageBuilder("image_cv") {{
                        // width("400");
                        height("470");

                        //width("540");
                        //   width("720");
                        //  height("1280");
                    }});


                }});
                panel(new PanelBuilder("panelButtons") {
                    {
                        childLayoutHorizontal();
                        backgroundColor(Color.WHITE);
                        marginTop("40");
                        control(new ButtonBuilder("ApplyButton", "Next Image") {{
                            alignLeft();
                            valignCenter();
                            marginRight("120px");
                            height("40");
                            width("100");
                            visibleToMouse(true);

                            interactOnClick("setImageAndAngle()");

                        }});
                        control(new ButtonBuilder("Show3DButton", "Show 3D") {{
                            alignLeft();
                            valignCenter();
                            marginLeft("5px");
                            //marginRight("70px");
                            height("40");
                            width("100");
                            visibleToMouse(true);
                            interactOnClick("create3D()");

                        }});
                    }
                });
                text(new TextBuilder("textProgress") {{
                    text("Please, connect your device...");
                    style("nifty-label");
                    alignLeft();
                    marginLeft("50");
                    color(Color.BLACK);
                }});

             /*   panel(new PanelBuilder() {{
                    childLayoutCenter();
                    text(new TextBuilder() {{
                        text("Nifty 1.4 Core Hello World woow wowowowowowowowow");
                        style("base-font");
                        color(Color.WHITE);
                        alignCenter();
                        valignCenter();
                    }});
                }});*/
            }});

        }}.build(nifty);
    }

    public static Screen create3dscene(Nifty nifty, final ScreenController controller) {
        return new ScreenBuilder("3dscene") {
            {
                controller(controller);
                layer(new LayerBuilder("3Dmodellayer") {
                    {
                        backgroundColor("#FFFAFA");
                        childLayoutCenter();
                        onStartScreenEffect(new EffectBuilder("fade") {{
                            length(1000);
                            startDelay(1000);
                            effectParameter("start", "#0");
                            effectParameter("end", "#f");
                        }});
                        visible(false);
                        control(new ButtonBuilder("BacktoImages", "Back") {{
                            alignLeft();
                            valignCenter();
                            marginLeft("5px");
                            //marginRight("70px");
                            height("40");
                            width("100");
                            visibleToMouse(true);
                            interactOnClick("make3dInVisible()");

                        }});
                    }
                });
            }
        }.build(nifty);
    }

    private void initInputs() {
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_HIDE_STATS);

        inputManager.addMapping("showSettings", new KeyTrigger(KeyInput.KEY_P));

        ActionListener actionListener = new ActionListener() {
            public void onAction(String name, boolean keyPressed, float tpf) {

                if (name.equals("showSettings") && keyPressed) {
                    try {
                        //  WindowControl c = screen.findNiftyControl("myWindow", WindowControl.class);
                        nifty.gotoScreen("start");
                        //   c.getElement().setVisible(!c.getElement().isVisible());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            }
        };

        inputManager.addListener(actionListener, "showSettings");


    }


    // Mat input;
    Mat grayImage;

    public AnalyseMat sift(Mat input) {
        grayImage = new Mat(input.rows(), input.cols(), input.type());
        if (!grayImage.empty() && !input.empty()) {
            Imgproc.cvtColor(input, grayImage, Imgproc.COLOR_BGR2GRAY);
            Core.normalize(grayImage, grayImage, 0, 255, Core.NORM_MINMAX);
        }
        //FeatureDetector siftDetector = FeatureDetector.create(FeatureDetector.SIFT);
        FeatureDetector siftDetector = FeatureDetector.create(FeatureDetector.HARRIS);
        DescriptorExtractor siftExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);

        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
        siftDetector.detect(grayImage, keyPoint);


        Scalar color = new Scalar(0.1, 0.2, 0.3);
        //  Features2d.drawKeypoints(input, keyPoint, input, color, 3);

        Mat descriptor = new Mat();
        siftExtractor.compute(input, keyPoint, descriptor);

        AnalyseMat an = new AnalyseMat(input, keyPoint, descriptor);

        return an;
    }


    @Override
    public void bind(Nifty nifty, Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }


    public static BufferedImage mat2Img(Mat input) {
        MatOfByte mob = new MatOfByte();
        Highgui.imencode(".jpg", input, mob);

        byte[] byteArray = mob.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImage;

    }

    public static NiftyImage convertMatToNiftyImage(Mat input) {
        AWTLoader loader = new AWTLoader();
        BufferedImage inputBuf = mat2Img(input);
        Image imageJME = loader.load(inputBuf, true);
        Texture2D t2d = new Texture2D();
        t2d.setImage(imageJME);
        RenderImage ri = new RenderImageJme(t2d);
        NiftyImage nimage = new NiftyImage(nifty.getRenderEngine(), ri);

        return nimage;
    }

    public void setImage(Mat mat) {
        NiftyImage nimage = convertMatToNiftyImage(mat);
// find old image
        Element niftyElement = nifty.getCurrentScreen().findElementByName("image");
// swap old with new image
        if (niftyElement != null)
            niftyElement.getRenderer(ImageRenderer.class).setImage(nimage);


    }

    public void setImage_cv(Mat mat) {
        NiftyImage nimage = convertMatToNiftyImage(mat);
// find old image
        Element niftyElement = nifty.getCurrentScreen().findElementByName("image_cv");
// swap old with new image
        if (niftyElement != null)
            niftyElement.getRenderer(ImageRenderer.class).setImage(nimage);


    }


    public void setTextProgress(String t) {
        Element textProgress = nifty.getCurrentScreen().findElementByName("textProgress");
        textProgress.getRenderer(TextRenderer.class).setText(t);
    }

    static int i = 0;


    public void setCameraView(GyroVectorAngle angle) {
        //   camera.setLocalRotation(new Quaternion(angle.x, angle.y, angle.z, 0f));
        //  translateGeometry(new Vector3f(angle.x, angle.y, angle.z), ROT, pivotCamera);
        //System.out.println(angle.toString());
        //  MainWindow.app.pivotCamera.setLocalRotation(new Quaternion().fromAngles(angle.x, angle.y, angle.z));
        //    MainWindow.app.cam1.setRotation(pivotCamera.getLocalRotation());
        //   MainWindow.app.cam1.lookAt(pivot.getLocalTranslation(), new Vector3f(0.0f, 1.0f, 0f));
    }

    com.jme3.scene.shape.Box floor, grid;
    Sphere sphere_ball;
    public Camera cam1;

    public void initializeAllFor3D() {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();


        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        screen = createIntroScreen(nifty, new MyScreenController());
        nifty.gotoScreen("start");

        guiViewPort.addProcessor(niftyDisplay);

        flyCam.setMoveSpeed(40f);
        flyCam.setDragToRotate(true);


        sphere.setQueueBucket(RenderQueue.Bucket.Sky);
        sphere.setCullHint(Spatial.CullHint.Never);
        assetManager.registerLocator("assets/", FileLocator.class);

        // rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        rootNode.setCullHint(Spatial.CullHint.Never);


        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //  sphere_ball = new Sphere(32, 32, 0.8f);
        //   sphere_ball.setTextureMode(Sphere.TextureMode.Projected);

        //     floor = new com.jme3.scene.shape.Box(15f, 0.11f, 28f);
        //   grid = new com.jme3.scene.shape.Box(15f, 2f, 0.15f);

        initLight();
        //  initCameraandObjectModel();


        inputManager.setCursorVisible(true);
        setDisplayStatView(false);
        setDisplayFps(false);

        viewPort.setBackgroundColor(ColorRGBA.Black);


        cam.setViewPort(.6f, 1f, .5f, 1f);
        // cam.setViewPort(1f, 1f, 0f, 0f);
        //   cam.setLocation(new Vector3f(8f, 2f, 12f));


        cam1 = cam.clone();
        cam1.setViewPort(.6f, 1f, 0f, .5f);
        //cam1.setViewPort(1f, 1f, 0f, 0f);
        //  cam1.setLocation(new Vector3f(0f, -2f, -4f));

/*        cam.lookAt(new Vector3f(object.getLocalTranslation().getX(),
                object.getLocalTranslation().getY() - 2f,
                object.getLocalTranslation().getZ())
                , new Vector3f(0.0f, 0.0f, 0.0f));
                */
        //  cam1.setLocation(new Vector3f(-12, 0f, 18f));
        //  cam1.lookAt(new Vector3f(0.0f, 0.0f, 5f), new Vector3f(0.0f, 0.0f, 0.0f));
        ViewPort viewPort2 = renderManager.createMainView("PiP", cam1);
        viewPort2.setClearFlags(true, true, true);
        viewPort2.attachScene(rootNode);

        set3DButtonEnabled(false);
        set3DButtonEnabled(true);

        initInputs();

        forMesh = new Geometry("line", new Mesh());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        forMesh.setMaterial(mat);


        rootNode.attachChild(forMesh);

    }

    static Geometry forMesh;

    public void set3DButtonEnabled(boolean b) {
        Button button3D = nifty.getCurrentScreen().findElementByName("Show3DButton").getNiftyControl(Button.class);
        if (button3D != null)
            button3D.setEnabled(b);
    }

    static final int MOVE = 1;
    static final int ROT = 2;
    static Node pivot;

    public void translateGeometry(final Vector3f translation, final int type, final Node pivot) {
        app.enqueue(new Callable<Spatial>() {
            public Spatial call() throws Exception {
                if (type == MOVE) {

                    // if (pivot.getLocalTranslation().getX() + translation.getX() >= 10f) translation.setX(0f);

                    return pivot.move(translation);
                } else {

                    return pivot.rotate(translation.getX(), translation.getY(), translation.getZ());
                }
            }
        });

    }


    public Node pivotCamera;
    Spatial object;
    Spatial camera;

    public void initCameraandObjectModel() {
        //    FrameBuffer fb = new FrameBuffer(1024, 768, 0);
        //    fb.setDepthBuffer(Image.Format.Depth);
        //     Texture2D niftytex = new Texture2D(1024, 768, Image.Format.RGB8);
        //     fb.setColorTexture(niftytex);

        com.jme3.scene.shape.Box b = new com.jme3.scene.shape.Box(1, 1, 1);

        //  Geometry camera = new Geometry("Camera", b);
        //   Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setTexture("m_ColorMap", niftytex); /** Here comes the texture! */
        //   camera.setMaterial(mat);
     /*   Dome pyramid = new Dome(2, 4, 2f);
        Geometry camera = new Geometry("Camera", pyramid);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        camera.setMaterial(mat); */
        assetManager.registerLocator("assets/", FileLocator.class);
        camera = assetManager.loadModel("Models/Samsung-pivot-4/Galaxy_Young.obj.j3o");
        camera.setLocalScale(0.05f);

      /*  Geometry object = new Geometry("Object", b);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap",dddddddddddddddddddd
                assetManager.loadTexture("Textures/diff.jpg"));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        object.setQueueBucket(RenderQueue.Bucket.Transparent);
        object.setMaterial(mat1); */

        object = assetManager.loadModel("Models/Cola/Cola.j3o");
        object.setLocalScale(0.8f);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", assetManager.loadTexture("Textures/mpm_vol.09_p35_can_red_diff.jpg"));
        object.setMaterial(mat1);

        pivot = new Node();
        pivot.setLocalTranslation(0f, -5f, -10f);
        //model.setLocalTranslation(0f, -5f, -0.05f);
        //  geom.setLocalTranslation(0f, -2.5f, -0.05f);
         /*координаты относительно этого pivot, а не начала координат!
         т.е. модель находится по сути в координатах x-5=3*/
        // g.setLocalTranslation(pivot.getLocalTranslation());
        //  rootNode.attachChild(pivot);

        pivotCamera = new Node();
        rootNode.attachChild(pivot);
        //  rootNode.attachChild(pivotCamera);
        pivotCamera.attachChild(camera);
        pivot.attachChild(pivotCamera);
        //  pivot.attachChild(camera);

        pivot.attachChild(object);

        //  translateGeometry(new Vector3f(0f, 8f, 3f), MOVE, pivotCamera);
        //  translateGeometry(new Vector3f(0.0f, 0f, 0f), ROT, pivotCamera);
        //object.setLocalTranslation(0f, 0f, -13f);
        // pivotCamera.setLocalTranslation(0f, 0f, 10f);
        pivotCamera.setLocalTranslation(object.getLocalTranslation());
        camera.setLocalTranslation(0f, 0f, 5f);


    }

    private void initLight() {
        /** A white ambient light source. */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

    }

    public Iterator iterator;


    public void create3DModel() {
        //    Element niftyElement = nifty.getCurrentScreen().findElementByName("image_cv");
        //    Element panel = nifty.getCurrentScreen().findElementByName("panelImage");
        //   panel.setWidth(panel.getWidth()-niftyElement.getWidth());
        // niftyElement.setWidth(0);

        //   panel.setVisible(false);
        cam.setViewPort(0f, 1f, 0f, 1f);
        cam1.setViewPort(1f, 1f, 1f, 1f);

        create3dscene(nifty, new MyScreenController());
        nifty.gotoScreen("3dscene");

        // Mat homogeniousCoords= calculate3DPoints();
        //   createMesh(homogeniousCoords);
        createMesh(null);

        //   Element panel3d = nifty.getCurrentScreen().findElementByName("3Dmodellayer");
        //   panel3d.setVisible(true);
    }

    public void make3DInVisible() {
        //  Element niftyElement = nifty.getCurrentScreen().findElementByName("3Dmodellayer");
        //   niftyElement.setVisible(false);
    }

    public void createMesh(Mat homogeniousCoords) {
        // Vector3f[] lineVerticies = new Vector3f[homogeniousCoords.cols()];
   /*     Mat resPoints=new Mat(homogeniousCoords.rows()-1,homogeniousCoords.cols(), CvType.CV_32FC1);
            for(int j=0;j<homogeniousCoords.cols();j++){
                    if( homogeniousCoords.get(3, j)[0]!=0) {
                      //  resPoints.put(, j, homogeniousCoords.get(, j)[0] / homogeniousCoords.get(3, j)[0]);
                        lineVerticies[j]= new Vector3f((float)(homogeniousCoords.get(0, j)[0] / homogeniousCoords.get(3, j)[0]),
                                (float)(homogeniousCoords.get(1, j)[0] / homogeniousCoords.get(3, j)[0]),
                                (float)(homogeniousCoords.get(2, j)[0] / homogeniousCoords.get(3, j)[0]));
                    }
            }

*/

        Vector3f[] lineVerticies = new Vector3f[100];
        try {
            Workbook workbook = Workbook.getWorkbook(new File("C:\\Users\\Sa User\\IdeaProjects\\JMonkeyTest3\\TestAccelerometer_constant.xls"));
            Sheet sheet = workbook.getSheet(0);
            double x=0, y=0, z=0;
            for (int i = 1; i < 100; i++) {
                Cell c = sheet.getCell(1, i);
                NumberCell nc = (NumberCell) c;
                x += nc.getValue();

                c = sheet.getCell(2, i);
                nc = (NumberCell) c;
                 y += nc.getValue();

                c = sheet.getCell(3, i);
                nc = (NumberCell) c;
                z += nc.getValue();
                lineVerticies[i - 1] = new Vector3f((float) x, (float) y, (float) z);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        Mesh mesh = new Mesh();
        mesh.setMode(Mesh.Mode.Points);


        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(lineVerticies));

        mesh.setPointSize(2f);
        mesh.updateBound();
        mesh.updateCounts();
        //  mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

    /*    final Mesh me=mesh;

       ParticleEmitter emit = new ParticleEmitter("Emitter", ParticleMesh.Type.Point, 10000);
        ArrayList<Mesh> a=new ArrayList<Mesh>(); a.add(me);
        emit.setShape(new EmitterMeshFaceShape(a));
        emit.setGravity(0, 0, 0);
        emit.setLowLife(60);
        emit.setHighLife(60);
        emit.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
        emit.setImagesX(15);
        emit.setStartSize(0.05f);
        emit.setEndSize(0.05f);
        emit.setStartColor(ColorRGBA.White);
        emit.setEndColor(ColorRGBA.White);
        emit.setSelectRandomImage(true);
        emit.emitAllParticles();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setBoolean("PointSprite", true);
        emit.setMaterial(mat);
        rootNode.attachChild(emit);*/
        forMesh.setMesh(mesh);


    }


    public Mat calculate3DPoints() {
        Mat E = current_pair.myFundamentalMat;
        Mat w = new Mat(), u = new Mat(), vt = new Mat();
        Core.SVDecomp(E, w, u, vt);

        Mat W = new Mat(3, 3, CvType.CV_64FC1);
        W.put(0, 0, 0);
        W.put(0, 1, -1);
        W.put(0, 2, 0);
        W.put(1, 0, 1);
        W.put(1, 1, 0);
        W.put(1, 2, 0);
        W.put(2, 0, 0);
        W.put(2, 1, 0);
        W.put(2, 2, 1);

        Mat W_inv = W.inv();

        Mat R1_temp = new Mat();
        Mat R1 = new Mat();
        Core.gemm(u, W, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, R1_temp);
        Core.gemm(R1_temp, vt, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, R1);

        Mat T1 = u.col(2);

        Mat R2_temp = new Mat();
        Mat R2 = new Mat();
        Core.gemm(u, W_inv, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, R2_temp);
        Core.gemm(R2_temp, vt, 1, Mat.zeros(3, 3, CvType.CV_64FC1), 1, R2);

        Mat T2 = new Mat();
        Core.multiply(u.col(2), new Scalar(-1), T2);

        Mat P1 = Mat.eye(3, 4, CvType.CV_64FC1);
        Mat P2 = new Mat(3, 4, CvType.CV_64FC1);

        P2.put(0, 0, R1.get(0, 0));
        P2.put(0, 1, R1.get(0, 1));
        P2.put(0, 2, R1.get(0, 2));
        P2.put(0, 3, T1.get(0, 0));
        P2.put(1, 0, R1.get(1, 0));
        P2.put(1, 1, R1.get(1, 1));
        P2.put(1, 2, R1.get(1, 2));
        P2.put(1, 3, T1.get(1, 0));

        P2.put(2, 0, R1.get(2, 0));
        P2.put(2, 1, R1.get(2, 1));
        P2.put(2, 2, R1.get(2, 2));
        P2.put(2, 3, R1.get(2, 0));
        Mat Q = new Mat();

        //  Core.gemm(current_pair.myInternal,P2,1,Mat.zeros(3,4,CvType.CV_64FC1),1,Q);

        Mat lp = Converters.vector_Point2d_to_Mat(current_pair.frame.analyseMat.matchPoints);//should be 1*N, CV_64FC2
        Mat rp = Converters.vector_Point2d_to_Mat(current_pair.frame1.analyseMat.matchPoints); //should be 1 *N, CV_64FC2

        System.out.println(lp.rows());
        System.out.println(lp.cols());
        System.out.println(lp.type());

        Mat out = new Mat(/*4,lp.cols(),CvType.CV_64FC1*/);
        try {
            Mat out_img = new Mat();
            //       Calib3d.reprojectImageTo3D(current_pair.disparityMap,out_img,Q);
            Calib3d.triangulatePoints(P1, P2, lp, rp, out);
            GyroUtils.printMat32F(out, "triangulation");
            return out;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;


    }


}


