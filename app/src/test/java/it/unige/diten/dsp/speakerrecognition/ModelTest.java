package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import it.unige.diten.dsp.speakerrecognition.libsvm.*;


// This class is not really intended to test the code of the application, since it does not
// perform feature extraction using the same classes as the application.

public class ModelTest  extends TestCase
{
    double[] y_min;
    double[] y_max;
    public void setUp() throws Exception
    {
        super.setUp();
        y_min = new double[26];
        y_max = new double[26];
    }


    public void testModels() throws Exception
    {
        int[] results = {0,0,0};

        int modelCount = 16;
        int audioTrackCount = 10;

        String[] modelFilenames = new String[modelCount];
        String[] rangeFilenames = new String[modelCount];
        String[] audioTrackFilenames = new String[audioTrackCount];
        String[] actualSpeaker = new String[audioTrackCount];


        File folder = new File("C:\\Tests");
        File[] listOfFiles = folder.listFiles();

        int j = 0;
        for (int i = 0; i < listOfFiles.length; i++)
        {
            String fname = listOfFiles[i].getName();
            if(fname.endsWith(".model"))
            {
                modelFilenames[j] = "C:\\Tests\\" + fname;
                rangeFilenames[j] = "C:\\Tests\\" + fname.replace(".model",".range");
                j++;
            }
        }

        actualSpeaker[0] = "Emanuele";
        audioTrackFilenames[0] = "C:\\Tests\\Audio\\newEma0.wav";
        actualSpeaker[1] = "Emanuele";
        audioTrackFilenames[1] = "C:\\Tests\\Audio\\newEma1.wav";
        actualSpeaker[2] = "Emanuele";
        audioTrackFilenames[2] = "C:\\Tests\\Audio\\newEma2.wav";
        actualSpeaker[3] = "Emanuele";
        audioTrackFilenames[3] = "C:\\Tests\\Audio\\newEma3.wav";
        actualSpeaker[4] = "Emanuele";
        audioTrackFilenames[4] = "C:\\Tests\\Audio\\newEma4.wav";
        actualSpeaker[5] = "Emanuele";
        audioTrackFilenames[5] = "C:\\Tests\\Audio\\newEma5.wav";
        actualSpeaker[6] = "Emanuele";
        audioTrackFilenames[6] = "C:\\Tests\\Audio\\newEma6.wav";
        actualSpeaker[7] = "Emanuele";
        audioTrackFilenames[7] = "C:\\Tests\\Audio\\newEma7.wav";
        actualSpeaker[8] = "Emanuele";
        audioTrackFilenames[8] = "C:\\Tests\\Audio\\newEma8.wav";
        actualSpeaker[9] = "Emanuele";
        audioTrackFilenames[9] = "C:\\Tests\\Audio\\newEma9.wav";
        
        for(int M = 0; M < modelCount; M++)
        {
            // Load model and range
            svm_model model = svm.svm_load_model(modelFilenames[M]);
            readRange(rangeFilenames[M]);

            for(int T = 0; T < audioTrackCount; T++)
            {
                // Extract features
                double[][] MFCC, DeDe;
                MFCC = FeatureExtractor.extractMFCC(audioTrackFilenames[T]);
                DeDe = DD.computeDD(MFCC, 2); // 2 is precision

                // Unite matrices and scale
                int frameCount = MFCC.length;
                double[][] allFeatures = new double[frameCount][26];
                // Unite the two matrices
                for(int C = 0; C < frameCount; C++)
                {
                    int K;
                    for(K = 0; K < 13; K++ )
                        allFeatures[C][K] = MFCC[C][K];

                    for(; K < 26; K++)
                        allFeatures[C][K] = DeDe[C][K-13];
                }
                scaleMatrix(allFeatures);

                svm_node[][] features = new svm_node[frameCount][26 + 1];


                results[0] = 0;
                results[1] = 0;
                results[2] = 0;
                for (int F = 0; F < frameCount; F++)
                {
                    int C;
                    for(C = 0; C < 26; C++) {
                        features[F][C] = new svm_node();
                        features[F][C].value = allFeatures[F][C];
                        features[F][C].index = C+1;
                    }
                    features[F][C] = new svm_node();
                    features[F][C].index = -1;
                    features[F][C].value = 0;

                    results[(int)svm.svm_predict(model,features[F])]++;
                }

                writeResultFile("C:\\Tests\\results.txt",results,actualSpeaker[T],
                        modelFilenames[M].substring(modelFilenames[M].lastIndexOf('\\') + 1),
                        audioTrackFilenames[T].substring(audioTrackFilenames[T].lastIndexOf('\\')+1));

            }
        }
    }


    private void readRange(String fileName)
    {
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));
            int lineNumber = 1;
            while ((sCurrentLine = br.readLine()) != null)
            {
                if(lineNumber >= 3)
                {
                    String[] arr = sCurrentLine.split(" ");
                    y_min[lineNumber - 3] = Double.valueOf(arr[1]);
                    y_max[lineNumber - 3] = Double.valueOf(arr[2]);

                }
                lineNumber++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void scaleMatrix(double[][] input)
    {
        double y_lower = -1;
        double y_upper = 1;

        for(int C = 0; C < input.length; C++)
        {
            for (int J = 0; J < input[0].length; J++)
            {
                input[C][J] = y_lower + (y_upper - y_lower) * (input[C][J] - y_min[J]) / (y_max[J] - y_min[J]);
            }
        }
    }

    private void writeResultFile(String filename, int[] results, String actualSpeaker, String model, String audioFile)
    {
        try
        {
            File file = new File(filename);
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.append("Andrea = ");
            fileWriter.append(String.valueOf(results[0]));
            fileWriter.append("\tDavide = ");
            fileWriter.append(String.valueOf(results[1]));
            fileWriter.append("\tEmanuele = ");
            fileWriter.append(String.valueOf(results[2]));
            fileWriter.append("\tActualSpeaker = " + actualSpeaker);
            fileWriter.append("\tSeconds = 15");
            fileWriter.append("\tModel = " + model);
            fileWriter.append("\tAudioFile = " + audioFile);
            fileWriter.append("\n");
            fileWriter.close();
        }
        catch(Exception ew)
        {
            ew.printStackTrace();
        }
    }
}
