package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import it.unige.diten.dsp.speakerrecognition.libsvm.*;
public class MySVMTest extends TestCase
{



    public void setUp() throws Exception {
        super.setUp();

    }

    public void testRecognizeSpeaker() throws Exception
    {
        int[] results = new int[3];
        svm_model modello = svm.svm_load_model("C:\\Tests\\model_new" +
                ".model");


        // create and initialize feature vector
        svm_node[] feat = new svm_node[27];
        for(int C=0; C < 27; C++)
            feat[C] = new svm_node();

        double frameCount = 0;
        double correctCount = 0;

        try {
            File file = new File("C:\\Tests\\feature_sbagliate" +
                    ".txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line, number;

            while ((line = bufferedReader.readLine()) != null)
            {

                int expected = Integer.valueOf(line.substring(0,1));
                int pos = 0;
                int index = 1;
                while(true)
                {

                    pos = line.indexOf(':');
                    if(pos == -1)
                        break;
                    line = line.substring(pos + 1);
                    number = line.substring(0, line.indexOf(' '));
                    feat[index-1].index = index;
                    feat[index-1].value = Double.valueOf(number);
                    index++;
                }
                feat[index-1].index = -1;
                feat[index-1].value = .0;

                frameCount++;
                int predicted = (int)svm.svm_predict(modello,feat);
                if(predicted == expected)
                    correctCount++;
                results[predicted]++;
            }
            fileReader.close();
        } catch (IOException ewww) {
            ewww.printStackTrace();
        }


        double correctPerc = correctCount/frameCount * 100;
        int a = 32;
        throw new Exception("lol");
    }


    public void testLoadMatrixAndScale() throws Exception
    {
        // load MATRIX from FILE
        double[][] MFCC = new double[1875][26];


        try {
            File file = new File("C:\\Tests\\davide1MatlabFeatures" +
                    ".txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line, number;
            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null)
            {
                line = line+" ";
                //int expected = Integer.valueOf(line.substring(0,1));
                int pos = 0;
                int index = 0;
                while(true)
                {
                    pos = line.indexOf(':');
                    if(pos == -1)
                        break;
                    line = line.substring(pos + 1);
                    number = line.substring(0, line.indexOf(' '));
                    MFCC[lineNumber][index] = Double.valueOf(number);
                    index++;
                }
                lineNumber++;
            }
            fileReader.close();
        } catch (IOException ewww) {
            ewww.printStackTrace();
        }


        MySVM.scaleMatrix(MFCC);
        int a = 2;
    }
}