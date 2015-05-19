package graphics;

import bluetooth.init.Init;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.*;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.CheckBoxStateChangedEvent;
import de.lessvoid.nifty.controls.NiftyControl;
import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.checkbox.CheckboxControl;
import de.lessvoid.nifty.controls.checkbox.builder.CheckboxBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.controls.window.builder.WindowBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import sun.plugin.javascript.navig4.Layer;

import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys.
 */
public class Main extends SimpleApplication implements ScreenController {
    static Main app;
    static BulletAppState bulletAppState;

    public static Spatial model;
    public static Geometry ball;

    Sphere sphere_ball;
    Box floor, grid;

    static float camlocX = -0f;
    static float camlocY = 12f;
    static float camlocZ = 48f;

    static float initX = camlocX;
    static float initY = camlocY - 5f;
    static float initZ = camlocZ - 20f;

    public final static int ROTATE = 1;
    public final static int MOVE = 2;

    public static void main(String[] args) {
        app = new Main();
        app.start();

    }

    static Screen screen;


    /*Test variables for path*/
    private MotionPath path;
    private MotionEvent motionControl;
    static Nifty nifty;

    Material ball_material, floor_material, grid_material;

    @Override
    public void simpleInitApp() {
      //  System.setProperty("org.lwjgl.util.Debug", "true");

        /*To init Bluetooth server*/
        Init init = new Init();
        init.initLocalDevice();
        init.initServer();


        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Nifty Gui.xml", "start", this);

        screen = createIntroScreen(nifty);

        nifty.gotoScreen("start");
        guiViewPort.addProcessor(niftyDisplay);

        flyCam.setMoveSpeed(100f);
        flyCam.setDragToRotate(true);
        cam.setLocation(new Vector3f(camlocX, camlocY, camlocZ));
        System.out.println(cam.getUp());
        cam.lookAt(new Vector3f(initX, initY, initZ), new Vector3f(0.0f, 1.0f, 0.0f));

        sphere.setQueueBucket(RenderQueue.Bucket.Sky);
        sphere.setCullHint(Spatial.CullHint.Never);
        assetManager.registerLocator("assets/", FileLocator.class);
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        rootNode.setCullHint(Spatial.CullHint.Never);

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        sphere_ball = new Sphere(32, 32, 0.8f);
        sphere_ball.setTextureMode(Sphere.TextureMode.Projected);

        floor = new Box(15f, 0.11f, 28f);
        grid = new Box(15f, 2f, 0.15f);
        //floor.scaleTextureCoordinates(new Vector2f(3, 6));

        initLight();
        initMaterials();
        initModel();
        initScene();
        // initPath();
        initInputs();
        initTennisCourt();
        // initGrid();
        initModelBodyControl();

        inputManager.setCursorVisible(true);

        setDisplayStatView(false);
        setDisplayFps(false);

    }

    //Uncomment for box:
    // private static Box box;
    // public static Geometry model;
    private void initLight() {
        /** A white ambient light source. */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

    }

    private void initMaterials() {
        ball_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        assetManager.registerLocator("assets/", FileLocator.class);
        TextureKey key2 = new TextureKey("Textures/dirt.jpg");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        ball_material.setTexture("ColorMap", tex2);

        floor_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        assetManager.registerLocator("assets/", FileLocator.class);
        TextureKey key3 = new TextureKey("Textures/Tennis_court.JPG");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(Texture.WrapMode.Repeat);
        floor_material.setTexture("ColorMap", tex3);

        grid_material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        grid_material.setColor("Color", ColorRGBA.Gray);
        assetManager.registerLocator("assets/", FileLocator.class);
        //TextureKey key4 = new TextureKey("Textures/Tennis_court.JPG");
        //key3.setGenerateMips(true);
        //Texture tex4 = assetManager.loadTexture(key4);
        // tex3.setWrap(Texture.WrapMode.Repeat);
        // grid_material.setTexture("ColorMap", tex4);
    }

    private void initModel() {

        assetManager.registerLocator("assets/", FileLocator.class);
        model = assetManager.loadModel("Models/Samsung-pivot-4/Galaxy_Young.obj.j3o");
        model.setLocalScale(0.05f);
        //model = assetManager.loadModel("Models/TennisRacket/TennisRacket.j3o");
        //model.setLocalScale(0.001f);

        Translation.x = initX;
        Translation.y = initY;
        Translation.z = initZ;
        model.rotate(0f, 0f, 0f);
        //model.setLocalTranslation(initX, initY, initZ);
        // model.setLocalTranslation(0f, -7.7f, 0.5f);

        rootNode.attachChild(model);


        // rootNode.attachChild(ball);
    }

    private void initInputs() {
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_HIDE_STATS);
        inputManager.addMapping("play_stop", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("showSettings", new KeyTrigger(KeyInput.KEY_P));

        ActionListener actionListener = new ActionListener() {
            public void onAction(String name, boolean keyPressed, float tpf) {
                if (name.equals("play_stop") && keyPressed) {
                    makeCannonBall();
                   /* if (playing) {
                        playing = false;
                        motionControl.stop();
                    } else {
                        playing = true;
                        motionControl.play();
                    }*/
                }
                if (name.equals("showSettings") && keyPressed) {
                    try {
                        WindowControl c = screen.findNiftyControl("myWindow", WindowControl.class);
                        c.getElement().setVisible(!c.getElement().isVisible());

                        CheckBox xDirection = screen.findNiftyControl("XDirection", CheckBox.class);
                        xDirection.setChecked(Translation.X_MOVE);
                        CheckBox yDirection = screen.findNiftyControl("YDirection", CheckBox.class);
                        yDirection.setChecked(Translation.Y_MOVE);
                        CheckBox zDirection = screen.findNiftyControl("ZDirection", CheckBox.class);
                        zDirection.setChecked(Translation.Z_MOVE);
                        CheckBox xRotation = screen.findNiftyControl("XRotation", CheckBox.class);
                        xRotation.setChecked(Translation.X_ROT);
                        CheckBox yRotation = screen.findNiftyControl("YRotation", CheckBox.class);
                        yRotation.setChecked(Translation.Y_ROT);
                        CheckBox zRotation = screen.findNiftyControl("ZRotation", CheckBox.class);
                        zRotation.setChecked(Translation.Z_ROT);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            }
        };

        inputManager.addListener(actionListener, "play_stop");
        inputManager.addListener(actionListener, "showSettings");


    }

    static Node pivot;

    private void initScene() {

        // Box b = new Box(0.3f, 0.3f, 0.3f);
        //Geometry g = new Geometry("Box", b);

        /// Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //  mat.setColor("Color", ColorRGBA.Blue);
        //   g.setMaterial(mat);
        //   rootNode.attachChild(g);

        pivot = new Node();
        pivot.setLocalTranslation(initX, initY, initZ);
        //model.setLocalTranslation(0f, -5f, -0.05f);
        model.setLocalTranslation(0f, -2.5f, -0.05f);
         /*координаты относительно этого pivot, а не начала координат!
         т.е. модель находится по сути в координатах x-5=3*/
        // g.setLocalTranslation(pivot.getLocalTranslation());
        rootNode.attachChild(pivot);
        pivot.attachChild(model);

        assetManager.registerLocator("assets/", FileLocator.class);

        //   Spatial sceneModel = assetManager.loadModel("Scenes/Court.j3o");
        Spatial sceneModel = assetManager.loadModel("Models/Stadium2/Stadium2scene.j3o");
        sceneModel.setLocalScale(100f);
        sceneModel.rotate(0f, 90f * FastMath.DEG_TO_RAD, 0f);

        rootNode.attachChild(sceneModel);

    }

    private void initTennisCourt() {
        Geometry floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_material);
        floor_geo.setLocalTranslation(0, 0.5f, 0);
        this.rootNode.attachChild(floor_geo);
    /* Make the floor physical with mass 0.0f! */
        RigidBodyControl floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);

    }

    private void initGrid() {
        Geometry grid_geo = new Geometry("Grid", grid);
        grid_geo.setMaterial(grid_material);
        grid_geo.setLocalTranslation(0, 2.65f, 0);
        this.rootNode.attachChild(grid_geo);
    /* Make the floor physical with mass 0.0f! */
        RigidBodyControl grid_phy = new RigidBodyControl(0.0f);
        grid_geo.addControl(grid_phy);
        bulletAppState.getPhysicsSpace().add(grid_phy);

    }

    static public RigidBodyControl model_phy;

    private void initModelBodyControl() {

    /* Make the floor physical with mass 0.0f! */
        model_phy = new RigidBodyControl(0.0f);
        pivot.addControl(model_phy);
        bulletAppState.getPhysicsSpace().add(model_phy);

    }


    private void initPath() {
        path = new MotionPath();
        path.addWayPoint(new Vector3f(10, 3, -40));
        path.addWayPoint(new Vector3f(10, 5, -35));
        path.addWayPoint(new Vector3f(10, 7, -30));
        path.addWayPoint(new Vector3f(10, 9, -25));
        path.addWayPoint(new Vector3f(10, 10, -22));
        path.addWayPoint(new Vector3f(0, 15, -5));


       /* path.addWayPoint(new Vector3f(10, 3, 0));
        path.addWayPoint(new Vector3f(10, 3, 10));
        path.addWayPoint(new Vector3f(-40, 3, 10));
        path.addWayPoint(new Vector3f(-40, 3, 0));
        path.addWayPoint(new Vector3f(-40, 8, 0));
        path.addWayPoint(new Vector3f(10, 8, 0));
        path.addWayPoint(new Vector3f(10, 8, 10));
        path.addWayPoint(new Vector3f(15, 8, 10));  */
        path.enableDebugShape(assetManager, rootNode);

        motionControl = new MotionEvent(ball, path);
        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionControl.setInitialDuration(10f);
        motionControl.setSpeed(2f);

        path.addListener(new MotionPathListener() {

            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
                if (path.getNbWayPoints() == wayPointIndex + 1) {
                    System.out.println(control.getSpatial().getName() + "Finished!!! ");
                } else {
                    System.out.println(control.getSpatial().getName() + " Reached way point " + wayPointIndex);
                }

            }
        });
    }

    static boolean playing = false;


    private void makeCannonBall() {

        ball = new Geometry("cannon ball", sphere_ball);
        //model.setLocalTranslation(Translation.x, Translation.y, Translation.z);
        ball.setMaterial(ball_material);
        ball.setLocalTranslation(cam.getLocation());
        rootNode.attachChild(ball);

        RigidBodyControl ball_physics = new RigidBodyControl(1f);
        ball.addControl(ball_physics);
        bulletAppState.getPhysicsSpace().add(ball_physics);

        ball_physics.setPhysicsLocation(new Vector3f(camlocX, camlocY - 3f, camlocZ - 50f));


        //ball_physics.setLinearVelocity(cam.getDirection().mult(-80f));
        ball_physics.setLinearVelocity(new Vector3f(0, 0, 40));
    }

    private Sphere sphereMesh = new Sphere(32, 32, 10, false, true);
    private Geometry sphere = new Geometry("Sky", sphereMesh);

    @Override
    public void simpleUpdate(float tpf) {
        sphere.setLocalTranslation(cam.getLocation());
        model_phy.setKinematic(true);
        model_phy.setPhysicsLocation(pivot.getWorldTranslation());
        /***********Emulation of rotation*/

        //   translateGeometry(new Vector3f(0.005f, 0f, 0f), ROTATE);


    }


    public static void translateGeometry(final Vector3f translation, final int type) {

        app.enqueue(new Callable<Spatial>() {
            public Spatial call() throws Exception {
                if (type == MOVE) {

                    if (pivot.getLocalTranslation().getX() + translation.getX() >= 10f) translation.setX(0f);

                    return pivot.move(translation);
                } else {

                    return pivot.rotate(translation.getX(), translation.getY(), translation.getZ());
                }
            }
        });

    }

    /**
     * **********************************NIFTY GUI
     */

    private static Screen createIntroScreen(final Nifty nifty) {
        Screen screen = new ScreenBuilder("start") {{
          /*  controller(new DefaultScreenController() {
                @Override
                public void onStartScreen() {
                    nifty.gotoScreen("demo");
                }
            }); */
            controller(new MyButtonController());
            layer(new LayerBuilder("layer") {{
                childLayoutCenter();
                onStartScreenEffect(new EffectBuilder("fade") {{
                    length(3000);
                    effectParameter("start", "#0");
                    effectParameter("end", "#f");
                }});
                panel(new PanelBuilder() {{
                    alignCenter();
                    valignCenter();
                    childLayoutHorizontal();
                    width("856px");
                    panel(new PanelBuilder() {{
                        width("300px");
                        height("256px");
                        childLayoutCenter();
                        text(new TextBuilder() {{
                            text("SA INC. PRESENTS....");
                            style("base-font");
                            alignCenter();
                            valignCenter();
                            onStartScreenEffect(new EffectBuilder("fade") {{
                                length(1000);
                                effectValue("time", "1700", "value", "0.0");
                                effectValue("time", "2000", "value", "1.0");
                                effectValue("time", "2600", "value", "1.0");
                                effectValue("time", "3200", "value", "0.0");
                                post(false);
                                neverStopRendering(true);
                            }});
                        }});
                    }});
                    panel(new PanelBuilder() {{
                        alignCenter();
                        valignCenter();
                        childLayoutOverlay();
                        width("256px");
                        height("256px");
                        onStartScreenEffect(new EffectBuilder("shake") {{
                            length(250);
                            startDelay(1300);
                            inherit();
                            effectParameter("global", "false");
                            effectParameter("distance", "10.");
                        }});
                        onStartScreenEffect(new EffectBuilder("imageSize") {{
                            length(600);
                            startDelay(3000);
                            effectParameter("startSize", "1.0");
                            effectParameter("endSize", "2.0");
                            inherit();
                            neverStopRendering(true);
                        }});
                        onStartScreenEffect(new EffectBuilder("fade") {{
                            length(600);
                            startDelay(3000);
                            effectParameter("start", "#f");
                            effectParameter("end", "#0");
                            inherit();
                            neverStopRendering(true);
                        }});
                        image(new ImageBuilder() {{
                            // filename("yin.png");
                            onStartScreenEffect(new EffectBuilder("move") {{
                                length(1000);
                                startDelay(300);
                                timeType("exp");
                                effectParameter("factor", "6.f");
                                effectParameter("mode", "in");
                                effectParameter("direction", "left");
                            }});
                        }});
                        image(new ImageBuilder() {{
                            //filename("yang.png");
                            onStartScreenEffect(new EffectBuilder("move") {{
                                length(1000);
                                startDelay(300);
                                timeType("exp");
                                effectParameter("factor", "6.f");
                                effectParameter("mode", "in");
                                effectParameter("direction", "right");
                            }});
                        }});
                    }});
                    panel(new PanelBuilder() {{
                        width("300px");
                        height("256px");
                        childLayoutCenter();
                        text(new TextBuilder() {{
                            text("SA INC. PRESENTS.....");
                            style("base-font");
                            alignCenter();
                            valignCenter();
                            onStartScreenEffect(new EffectBuilder("fade") {{
                                length(1000);
                                effectValue("time", "1700", "value", "0.0");
                                effectValue("time", "2000", "value", "1.0");
                                effectValue("time", "2600", "value", "1.0");
                                effectValue("time", "3200", "value", "0.0");
                                post(false);
                                neverStopRendering(true);
                            }});
                        }});
                    }});
                }});
            }});
            layer(new LayerBuilder("tennisCourt") {{
                // backgroundColor("#FFFAFA");
                childLayoutCenter();
                onStartScreenEffect(new EffectBuilder("fade") {{
                    length(1000);
                    startDelay(3000);
                    effectParameter("start", "#0");
                    effectParameter("end", "#f");
                }});

                control(new WindowBuilder("myWindow", "Settings") {
                    {

                        width("320px"); // windows will need a size
                        height("320px");
                        backgroundColor(Color.WHITE);
                        alignRight();
                        valignTop();
                        visible(false);
                        controller(new WindowControl());
                        childLayoutVertical();

                        /************************MOVE*/
                        panel(new PanelBuilder("movePanel") {{
                            width("320px");
                            height("140px");
                            valignTop();
                            childLayoutVertical();
                            text(new TextBuilder() {{
                                text("Move");
                                style("base-font");
                                color("#eeef");
                                valignTop();
                                width("100%");
                                color(Color.BLACK);
                            }});
                            panel(new PanelBuilder() {{
                                marginLeft("5px");
                                childLayoutHorizontal();

                                control(new CheckboxBuilder("XDirection") {{      /*X CheckBox*/
                                    name("checkbox");
                                    checked(true); // start with uncheck
                                    marginRight("10px");
                                    marginTop("8px");
                                    controller(new CheckboxControl());

                                }});
                                control(new LabelBuilder("XDirectionL", "X Direction") {{
                                    marginTop("8px");
                                    color(Color.BLACK);
                                }});
                            }});
                            panel(new PanelBuilder() {{
                                childLayoutHorizontal();
                                marginLeft("5px");

                                control(new CheckboxBuilder("YDirection") {{   /*Y Checkbox*/
                                    name("checkbox");
                                    checked(true); // start with uncheck
                                    marginRight("10px");
                                    marginTop("8px");

                                }});
                                control(new LabelBuilder("YDirectionL", "Y Direction") {{
                                    marginTop("8px");
                                    color(Color.BLACK);
                                }});
                            }});

                            panel(new PanelBuilder() {{
                                childLayoutHorizontal();
                                marginLeft("5px");

                                control(new CheckboxBuilder("ZDirection") {{   /*Z Checkbox*/
                                    name("checkbox");
                                    checked(true); // start with uncheck
                                    marginRight("10px");
                                    marginTop("8px");
                                }});
                                control(new LabelBuilder("ZDirectionL", "Z Direction") {{
                                    marginTop("8px");
                                    color(Color.BLACK);
                                }});
                            }});

                        }});
                        /************************ROTATION*/
                        panel(new PanelBuilder("rotatePanel") {{
                            width("320px"); // windows will need a size
                            height("150px");
                            childLayoutVertical();
                            marginTop("80px");

                            text(new TextBuilder() {{
                                text("Rotation");
                                style("base-font");
                                color("#eeef");
                                valignTop();
                                width("100%");
                                color(Color.BLACK);
                            }});
                            panel(new PanelBuilder() {{
                                marginLeft("5px");
                                childLayoutHorizontal();


                                control(new CheckboxBuilder("XRotation") {{      /*X CheckBox*/
                                    name("checkbox");
                                    checked(true); // start with uncheck
                                    marginRight("10px");
                                    marginTop("8px");

                                }});
                                control(new LabelBuilder("XRotationL", "X Rotation") {{
                                    marginTop("8px");
                                    color(Color.BLACK);
                                }});
                            }});
                            panel(new PanelBuilder() {{
                                childLayoutHorizontal();
                                marginLeft("5px");

                                control(new CheckboxBuilder("YRotation") {{   /*Y Checkbox*/
                                    name("checkbox");
                                    checked(true); // start with uncheck
                                    marginRight("10px");

                                    marginTop("8px");


                                }});
                                control(new LabelBuilder("YRotationL", "Y Rotation") {{
                                    marginTop("8px");
                                    color(Color.BLACK);
                                }});
                            }});

                            panel(new PanelBuilder() {{
                                childLayoutHorizontal();
                                marginLeft("5px");

                                control(new CheckboxBuilder("ZRotation") {{   /*Z Checkbox*/
                                    name("checkbox");
                                    checked(true); // start with uncheck
                                    marginRight("10px");
                                    marginTop("8px");

                                }});
                                control(new LabelBuilder("ZRotationL", "Z Rotation") {{
                                    marginTop("8px");
                                    color(Color.BLACK);
                                }});
                            }});
                            panel(new PanelBuilder() {{
                                childLayoutHorizontal();
                                marginLeft("5px");
                                control(new ButtonBuilder("ApplyButton", "Apply") {{
                                    alignLeft();
                                    valignCenter();
                                    marginRight("120px");
                                    height("60%");
                                    width("30%");
                                    visibleToMouse(true);
                                    interactOnClick("applyAxis()");

                                }});

                                control(new ButtonBuilder("ResetButton", "Reset motions") {{
                                    alignRight();
                                    valignCenter();
                                    height("60%");
                                    width("30%");
                                    visibleToMouse(true);
                                    interactOnClick("resetPositions()");

                                }});

                            }});

                        }});
                    }
                });
            }});

        }}.build(nifty);

        return screen;
    }

    public static void resetPositions() {

        pivot.setLocalTranslation(-0.0f, 7.0f, 28.0f);
        pivot.setLocalRotation(new Quaternion(0f, 0f, 0f, 1f));
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
}