package it.unige.diten.dsp.speakerrecognition.svmModeling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *  Abstract class that labels feature files with keys provided by the user or
 *  obtained from the filename
 */

public abstract class LabelFeatureFile {

    static String[] names;
    static long startTime, endTime;
    /**
     * This function is called when the labelling is performed on .ff files created by the application
     * @param params    an array containing paths of the files to be labeled
     *
     * @return          path to the merged file
     */
    public static String label(String[] params) throws Exception
    {
        // TODO: replace this line with names selected
        names = new String[]{"Andrea", "Davide"};

        String[] sameNamePath = groupFilesByName(params);
        String toBeMerged = "";
        String[][] path = new String[sameNamePath.length][];

        for(int i = 0; i < path.length; i++)
            path[i] = sameNamePath[i].split(",");

        for(int i = 0; i < sameNamePath.length; i++) {

            File[] files;
            FileWriter fileWriter;
            BufferedWriter bufferedWriter;

            files = new File[path[i].length];
            // Initialize a file for every path
            for (int j = 0; j < files.length; j++)
                files[j] = new File(path[i][j]);

            // Whatever is inside, brackets included
            String bracketsContent =
                    path[i][0].substring(path[i][0].lastIndexOf("["),
                            path[i][0].lastIndexOf("]") + 1);
            // The name says it all
            String pathWithoutBrackets = path[i][0].replace(bracketsContent, "");
            // The name of the merged feature file is <speaker_name>.ff.unique
            String mergedFile = pathWithoutBrackets.replace(".ff", ".labeled");
            // Container for paths to labeled files, needed by MergeFiles
            toBeMerged += mergedFile + ",";

            fileWriter = new FileWriter(mergedFile);
            bufferedWriter = new BufferedWriter(fileWriter);

            //startTime = System.nanoTime();

// Using line-by-line reading/writing

            for (File file : files) {
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(i + " " + line);
                    bufferedWriter.newLine();
                }
            }

            // Using chunk-by-chunk reading/writing
/*
            for (File file : files) {
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                char[] line = new char[(int) file.length()];
                int chunkSize = 8192;
                int iterations = (int)file.length() / chunkSize;
                int remainder = (int) file.length() % chunkSize;
                int index = 0;

                for(int C = 0; C < iterations; C++)
                {
                    bufferedReader.read(line, C * chunkSize, chunkSize);
                }
                bufferedReader.read(line, iterations * chunkSize, remainder);

                StringBuilder st = new StringBuilder();
                st.append(line);

                do {
                    st.insert(index, i + " ");
                    index = st.indexOf("\n", index + 2) + 1;
                }
                while(index != st.lastIndexOf("\n") + 1);

                String string = st.toString();
                iterations = string.length() / chunkSize;
                remainder = string.length() % chunkSize;

                for(int C = 0; C < iterations; C++)
                {
                    bufferedWriter.write(string, C * chunkSize, chunkSize);
                }
                bufferedWriter.write(string, (iterations * chunkSize), remainder);
                bufferedWriter.flush();
            }
*/


            bufferedWriter.close();
            //endTime = System.nanoTime();
        }

        System.out.println("\n\n\n Time elapsed: " + (endTime - startTime));

        // Constructing the filename
        String mergedFileName = path[0][0].substring(0, path[0][0].lastIndexOf('/') + 1);
        mergedFileName += "[" + getCurrentDate() + "]";
        for(String name : names)
            mergedFileName += name;
        mergedFileName += ".unscaled";

        MergeFile.mergeFiles(toBeMerged, mergedFileName);

        return mergedFileName;
    }

    private static String getCurrentDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ITALY);
        Date date = new Date();
        return(dateFormat.format(date));
    }


    private static String[] groupFilesByName(String[] paths)
    {

        String[] sameNamePath = new String[names.length];
        int i = 0;

        for(String name: names)
        {
            sameNamePath[i] = "";
            // For each feature file
            for(String filepath: paths)
            {
                if(filepath.contains(name))
                {
                    // Prepare the array of files that have to be joined
                    sameNamePath[i] += filepath + ",";
                }
            }
            i++;
        }

        return sameNamePath;
    }

    private static class MergeFile
    {
        /**
         * This function is used when you want to merge multiple files with a similar filename.
         * Each row of {@code groupOfPaths} should contain a string with one or multiple paths
         * divided by a comma and the filenames must have a common {@code identifier}.
         * @param groupOfPaths
         *                  array of strings grouped by filename
         * @param identifier
         *                  array of strings used to identify every subgroup of {@code groupOfPaths}
         *
         * @return          An array of paths: each one of them points to one merged file
         * @throws IOException
         */
        protected static String[] mergeFiles(final String[] groupOfPaths, final String[] identifier) throws IOException
        {
            File[]          files;

            String[] newPaths = new String[identifier.length];

            // For each group of paths
            for(int i = 0; i < groupOfPaths.length; i++) {
                String[] path = groupOfPaths[0].split(",");

                files = new File[path.length];
                // Initialize a file for every path
                for (int j = 0; j < files.length; j++)
                    files[j] = new File(path[j]);

                String mergedFile =
                        path[0].substring(0, path[0].lastIndexOf('/')) + identifier[i] + ".merged";

                newPaths[i] = mergedFile;

                merge(files, mergedFile);
            }
            return newPaths;
        }

        /**
         * Merge together multiple files appending them one after the other. The order is determined
         * by the order of the paths in {@code paths}.
         * @param paths
         *                  string of paths comma-separated
         * @param filename
         *                  name of the file to be saved
         *
         * @return          A string with the path to the merged file
         * @throws IOException
         */
        private static String mergeFiles(final String paths, final String filename) throws IOException {
            File[] files;

            String[] path = paths.split(",");

            files = new File[path.length];
            // Initialize a file for every path
            for (int i = 0; i < files.length; i++)
                files[i] = new File(path[i]);

            merge(files, filename);

            return filename;
        }

        private static void merge(File[] files, String outFilename) throws IOException
        {
            FileOutputStream fileOutputStream = new FileOutputStream(outFilename);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            for (File file : files) {
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

                int chunkSize = 8192;
                byte[] line = new byte[chunkSize];
                long iterations = file.length() / chunkSize;
                int remainder = (int) file.length() % chunkSize;

                for(int C = 0; C < iterations; C++)
                {
                    bufferedInputStream.read(line);
                    bufferedOutputStream.write(line);
                }
                line = new byte[remainder];
                bufferedInputStream.read(line);
                bufferedOutputStream.write(line);
            }


            bufferedOutputStream.close();
        }
    }
}
