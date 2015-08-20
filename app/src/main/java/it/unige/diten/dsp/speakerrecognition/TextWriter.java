package it.unige.diten.dsp.speakerrecognition;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public abstract class TextWriter
{

    private static String fileName;

    public static void setFilename(String filename)
    {
        fileName = filename;
    }

    public static boolean appendText(String filename, String text)
    {
        return writeText(filename,text,true);
    }
    public static boolean appendText(String text)
    {
        return writeText(fileName,text,true);
    }
    public static boolean writeText(String filename, String text)
    {
        return writeText(filename,text,false);
    }
    public static boolean writeText(String text)
    {
        return writeText(fileName, text, false);
    }

    private static boolean writeText(String filename, String text, boolean append)
    {
        try {
            FileWriter fw = new FileWriter(filename, append);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(text);
            out.close();
            bw.close();
            fw.close();
        }catch (Exception e) {
            return false;
        }
        return true;
    }
}
