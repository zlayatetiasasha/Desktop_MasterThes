package bluetooth.init;

import bluetooth.EchoServer;

import javax.bluetooth.LocalDevice;

/**
 * Created with IntelliJ IDEA.
 * User: Alexandra Malakhova
 * Date: 08.04.14
 * Time: 22:30
 */

/*Class for initialization Bluetooth server*/
public class Init {

    public void initLocalDevice() {

        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            System.out.println("Address: " + localDevice.getBluetoothAddress());
            System.out.println("Name: " + localDevice.getFriendlyName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initServer() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EchoServer sampleSPPServer = new EchoServer();
                    sampleSPPServer.startServer();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        t.start();

    }

}
