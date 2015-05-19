package graphics;

import com.jme3.math.Vector3f;
import graphics.Main;
import tests.ExcelFileWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Alexandra Malakhova
 * Date: 08.04.14
 * Time: 22:36
 */

/*Class that implements the methods for scaling the movements and rotation from original phone measurements*/
public class Translation {
    public static boolean X_MOVE = true, Y_MOVE = true, Z_MOVE = true;
    public static boolean X_ROT = true, Y_ROT = true, Z_ROT = true;

    public static float x = 0f, y = 10f, z = -20f;
    public static float ang_x = 0f, ang_y = 0f, ang_z = 0f;
   /* public static final float CENTER_X = 20f;
    public static final float CENTER_Y = 7f;*/

    public static final float SCALE_X = 50f;
    public static final float SCALE_Y = -30f;

    /*Here the translation values are stated*/
    public void setMoveCoord(float x_new, float y_new, float z_new) {
        /*Now the translation is omitted*/



        /*Uncomment to state the translation values*/

        if (X_MOVE) {
            x = -100f * x_new;
        } else {
            x = 0f;
        }
        if (Y_MOVE) {
            y = 60.0f * y_new;
        } else {
            y = 0f;
        }
        if (Z_MOVE) {
            z = -80.0f * z_new;
        } else {
            z = 0f;
        }

        Main.translateGeometry(new Vector3f(x, y, z), Main.MOVE);
    }


    /*Here the rotation angle is stated*/

    public void setRotateCoord(float x_new, float y_new, float z_new) {
        /* Uncomment to omit the rotation angle*/
       /* ang_x = 0f; */


        //  ang_x = x_new;
        //  ang_y = y_new;
        //   ang_z = z_new;

        //System.out.println(x_new + ", " + y_new + ", " + z_new);

        if (Math.abs(x_new) > 7f) ang_x = 0f;
        else
        if (X_ROT) {
            ang_x = x_new;
        } else {
            ang_x = 0f;
        }
       if (Math.abs(y_new) > 7f) ang_y = 0f;
        else
        if (Y_ROT) {
            ang_y = y_new;
        } else {
            ang_y = 0f;
        }
        if (Math.abs(z_new) > 7f) ang_z = 0f;
        else
        if (Z_ROT) {
            ang_z = z_new;
        } else {
            ang_z = 0f;
        }

        Main.translateGeometry(new Vector3f(ang_x, ang_y, ang_z), Main.ROTATE);

    }
}
