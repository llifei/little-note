package com.lifeifanzs.memorableintent.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyFileUtils {

    private static FileInputStream in;
    private static FileOutputStream out;

    public static void copyFile(File fromFile,File toFile) {
        try {
            in = new FileInputStream(fromFile);
            out = new FileOutputStream(toFile);

            byte[] bys = new byte[1024 * 1024];
            while (in.read(bys, 0, bys.length) != -1) {
                out.write(bys);
            }
        }catch (Exception e){}finally{
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
