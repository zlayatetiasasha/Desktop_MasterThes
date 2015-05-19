package com.example.AndroidTests2.obj;

import obj.AnalyseMat;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;


/**
 * Created by Sa User on 28.03.2015.
 */
public class CameraFrame implements Serializable {
    private static final long serialVersionUID = 7526472295622776147L;
    public transient Mat mat;
    public transient AnalyseMat analyseMat;
    public transient int frame_number;

    public byte[] buf;

    public GyroVectorAngle angle; //angle when camera frame is ready and new is beeing captured
    public AccelVectorPath path; //path when camera frame is ready and new is beeing captured
    float focalLength;

    public GyroVectorAngle previousAngle;
    public ArrayList<GyroVectorAngle> frameAngles;
    public float[][] currentRotation;

    public CameraFrame(byte[] buf, GyroVectorAngle angle, AccelVectorPath path) {
//        this.mat = mat.clone();
        this.buf = buf;
        this.previousAngle = this.angle;

        this.angle = angle;
        this.path = path;
    }

    public CameraFrame(Mat m, GyroVectorAngle a, AccelVectorPath p){
        this.mat = m;
        this.previousAngle = this.angle;

        this.angle = a;
        this.path = p;
    }

    public CameraFrame(byte[]buf, float[][] currentRotation, AccelVectorPath path){
        this.buf = buf;
        this.previousAngle = this.angle;

        this.currentRotation=currentRotation;
        this.path=path;

    }

    public Mat decodeToMat() {
        byte buf[] = this.buf;

        return Highgui.imdecode(new MatOfByte(buf), 1);

    }

/*    public static String matToJson(Mat mat) {
        JsonObject obj = new JsonObject();

        if (mat.isContinuous()) {
            int cols = mat.cols();
            int rows = mat.rows();
            int elemSize = (int) mat.elemSize();
            int type = mat.type();

            obj.addProperty("rows", rows);
            obj.addProperty("cols", cols);
            obj.addProperty("type", type);

            // We cannot set binary data to a json object, so:
            // Encoding data byte array to Base64.
            String dataString = "";

            //  if (type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
            //       int[] data = new int[cols * rows * elemSize];
            //         mat.get(0, 0, data);
            //    dataString = new String(Base64.encode(SerializationUtils.toByteArray(data), Base64.DEFAULT));
            //    } else if (type == CvType.CV_32F || type == CvType.CV_32FC2) {
            //       float[] data1 = new float[cols * rows * elemSize];
            //      mat.get(0, 0, data1);
            //      dataString = new String(Base64.encode(SerializationUtils.toByteArray(data1), Base64.DEFAULT));
            //    } else if (type == CvType.CV_64F || type == CvType.CV_64FC2) {
            //      double[] data2 = new double[cols * rows * elemSize];
            //       mat.get(0, 0, data2);
            //        dataString = new String(Base64.encode(SerializationUtils.toByteArray(data2), Base64.DEFAULT));
            if (type == CvType.CV_8UC4) {
                byte[] data3 = new byte[cols * rows * elemSize];
                mat.get(0, 0, data3);
                dataString = new String(Base64.getEncoder().encode(data3));
            }
            //   else {

            //      throw new UnsupportedOperationException("unknown type");
            //  }
            obj.addProperty("data", dataString);

            Gson gson = new Gson();
            String json = gson.toJson(obj);

            return json;
        } else {
            System.out.println("Mat not continuous.");
        }
        return "{}";
    }

    public static Mat matFromJson(String json) {


        JsonParser parser = new JsonParser();
        JsonObject JsonObject = parser.parse(json).getAsJsonObject();

        int rows = JsonObject.get("rows").getAsInt();
        int cols = JsonObject.get("cols").getAsInt();
        int type = JsonObject.get("type").getAsInt();

        Mat mat = new Mat(rows, cols, type);

        String dataString = JsonObject.get("data").getAsString();
    /*    if (type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
            int[] data = SerializationUtils.toIntArray(Base64.getDecoder().decode(dataString.getBytes()));
            mat.put(0, 0, data);
        } else if (type == CvType.CV_32F || type == CvType.CV_32FC2) {
            float[] data = SerializationUtils.toFloatArray(Base64.getDecoder().decode(dataString.getBytes()));
            mat.put(0, 0, data);
        } else if (type == CvType.CV_64F || type == CvType.CV_64FC2) {
            double[] data = SerializationUtils.toDoubleArray(Base64.getDecoder().decode(dataString.getBytes()));
            mat.put(0, 0, data);
        } else if (type == CvType.CV_8U) {*/
    //         byte[] data = Base64.getDecoder().decode(dataString.getBytes());
    //         mat.put(0, 0, data);
      /*  } else {

            throw new UnsupportedOperationException("unknown type");
        }*/
    //    return mat;
    // }

    // */
}
