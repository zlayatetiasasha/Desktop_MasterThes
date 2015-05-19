package tests;


import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Alexandra Malakhova
 * Date: 22.05.14
 * Time: 21:02
 */
public class ExcelFileWriter {
    String fileName;
    WritableWorkbook workbook;
    WritableSheet sheet;

    private int writtenNumbers;


    public ExcelFileWriter(String fileName) {
        this.fileName = fileName;
        try {
            workbook = Workbook.createWorkbook(new File(fileName));
            sheet = workbook.createSheet("TestResults", 0);
            Label label = new Label(1, 0, "x");
            sheet.addCell(label);
            Label label1 = new Label(2, 0, "y");
            sheet.addCell(label1);
            Label label2 = new Label(3, 0, "z");
            sheet.addCell(label2);
            writtenNumbers++;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void write(float x, float y, float z) {
        try {
            if (writtenNumbers == 101) {

                workbook.write();
                workbook.close();
                System.out.println("I finished");
                return;
            }
            if (writtenNumbers > 101 ) {
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            jxl.write.Number numberX = new Number(1, writtenNumbers, x);
            sheet.addCell(numberX);
            jxl.write.Number numberY = new Number(2, writtenNumbers, y);
            sheet.addCell(numberY);
            jxl.write.Number numberZ = new Number(3, writtenNumbers, z);
            sheet.addCell(numberZ);

            writtenNumbers++;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
