package graphics;

import com.jme3.math.Vector3f;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * Created with IntelliJ IDEA.
 * User: Alexandra Malakhova
 * Date: 29.05.14
 * Time: 23:46
 */
public class MyButtonController implements ScreenController {
    @Override
    public void bind(Nifty nifty, Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    public void applyAxis() {
        CheckBox xDirection = Main.screen.findNiftyControl("XDirection", CheckBox.class);
        Translation.X_MOVE = xDirection.isChecked();
        CheckBox yDirection = Main.screen.findNiftyControl("YDirection", CheckBox.class);
        Translation.Y_MOVE = yDirection.isChecked();
        CheckBox zDirection = Main.screen.findNiftyControl("ZDirection", CheckBox.class);
        Translation.Z_MOVE = zDirection.isChecked();
        CheckBox xRotation = Main.screen.findNiftyControl("XRotation", CheckBox.class);
        Translation.X_ROT = xRotation.isChecked();
        CheckBox yRotation = Main.screen.findNiftyControl("YRotation", CheckBox.class);
        Translation.Y_ROT = yRotation.isChecked();
        CheckBox zRotation = Main.screen.findNiftyControl("ZRotation", CheckBox.class);
        Translation.Z_ROT = zRotation.isChecked();
    }


    public void resetPositions() {
       Main.resetPositions();
    }
}
