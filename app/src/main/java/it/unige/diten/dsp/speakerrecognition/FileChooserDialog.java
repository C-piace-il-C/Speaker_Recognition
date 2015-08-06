package it.unige.diten.dsp.speakerrecognition;

/**
 * http://www.ninthavenue.com.au/simple-android-file-chooser
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class FileChooserDialog {
    private static final String PARENT_DIR = "..";

    private final Activity activity;
    private ListView list;
    private AlertDialog dialog;
    private File currentPath;
    private File[] selectedFiles;

    // filter on file extension
    private String extension = null;
    public void setExtension(String extension) {
        this.extension = (extension == null) ? null :
                extension.toLowerCase();
    }

    private String chosenPath = Environment.getExternalStorageDirectory().getPath();

    public void setChosenPath(String chosenPath) {
        if(chosenPath == null)
            return;
        else
            this.chosenPath = chosenPath;
    }

    // file selection event handling
    public interface FileSelectedListener {
        void fileSelected(File[] file);
    }
    public FileChooserDialog setFileListener(FileSelectedListener fileListener) {
        this.fileListener = fileListener;
        return this;
    }
    private FileSelectedListener fileListener;

    public FileChooserDialog(Activity activity) {
        this.activity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SparseBooleanArray sparsy = list.getCheckedItemPositions();
                // Temporary, incorrect

                if(list.getCheckedItemCount() == 0)
                    return;

                selectedFiles = new File[list.getCheckedItemCount()];
                int j = 0;
                for(int i = 0; i < list.getAdapter().getCount(); i++ )
                {
                    if(sparsy.get(i)) {
                        String fileChosen = (String) list.getItemAtPosition(i);
                        selectedFiles[j++] = (getChosenFile(fileChosen));
                    }
                }
                fileListener.fileSelected(selectedFiles);
            }
        });

        dialog = builder.create();
        list = new ListView(activity);

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int which, long id) {
                String fileChosen = (String) list.getItemAtPosition(which);
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    list.setItemChecked(which, false);
                    refresh(chosenFile);
                } else {
                    // Item is checked, do something
                }
            }
        });
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        dialog.setView(list);
        //dialog.setContentView(list);

        refresh(new File(chosenPath));
    }

    public void showDialog() {
        dialog.show();
    }


    /**
     * Sort, filter and display the files for the given path.
     */
    private void refresh(File path) {
        this.currentPath = path;
        if (path.exists()) {
            File[] dirs = path.listFiles(new FileFilter() {
                @Override public boolean accept(File file) {
                    return (file.isDirectory() && file.canRead());
                }
            });
            File[] files = path.listFiles(new FileFilter() {
                @Override public boolean accept(File file) {
                    if (!file.isDirectory()) {
                        if (!file.canRead()) {
                            return false;
                        } else if (extension == null) {
                            return true;
                        } else {
                            return file.getName().toLowerCase().endsWith(extension);
                        }
                    } else {
                        return false;
                    }
                }
            });

            // convert to an array
            int i = 0;
            String[] fileList, dirList;
            if (path.getParentFile() == null) {
                dirList = new String[dirs.length];
                fileList = new String[files.length];
            } else {
                dirList = new String[dirs.length + 1];
                fileList = new String[files.length];
                dirList[i++] = PARENT_DIR;
            }
            Arrays.sort(dirs);
            Arrays.sort(files);
            for (File dir : dirs) { dirList[i++] = dir.getName(); }
            i = 0;
            for (File file : files ) { fileList[i++] = file.getName(); }

            // refresh the user interface
            dialog.setTitle(currentPath.getPath());

            ArrayAdapter<String> dirsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, dirList) {
                @Override public View getView(int pos, View view, ViewGroup parent) {
                    view = super.getView(pos, view, parent);
                    ((TextView) view).setSingleLine(true);
                    return view;
                }
            };

            ArrayAdapter<String> filesAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_multiple_choice, fileList) {
                @Override public View getView(int pos, View view, ViewGroup parent) {
                    view = super.getView(pos, view, parent);
                    ((TextView) view).setSingleLine(true);
                    return view;
                }
            };

            MergeAdapter mergeAdapter = new MergeAdapter();

            mergeAdapter.addAdapter(dirsAdapter);
            mergeAdapter.addAdapter(filesAdapter);

            list.setAdapter(mergeAdapter);
        }
    }


    /**
     * Convert a relative filename into an actual File object.
     */
    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) {
            return currentPath.getParentFile();
        } else {
            return new File(currentPath, fileChosen);
        }
    }
}
