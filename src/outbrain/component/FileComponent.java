package outbrain.component;

import outbrain.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileComponent {
    private static ArrayList<File> selectedFileList;
    private static File selectedFile;
    private final File file;
    private final String destFolderRelativePath;
    private File destFolderFile;
    private final String[] paths;
    private String pathToDestFolder;
    private String pathFromDestFolder;
    private final int defaultDrillDownCount;
    private int drillDownCount = 0;

    public String getComponentName() {
        return componentName;
    }

    private String componentName;

    public FileComponent(final String filePath, final String destFolder, final int defaultDrillDownCount) {
        this.file = new File(filePath);
        componentName = file.getName();
        this.destFolderRelativePath = destFolder;
        paths = destFolder.split("/");
        this.defaultDrillDownCount = defaultDrillDownCount;
        FileUtils.findFilePath(this);
    }

    public File getFile() { return file; }

    public File getDestFolderFile() { return destFolderFile; }

    public String[] getPaths() {
        return paths;
    }

    public String getDestFolderRelativePath() {
        return destFolderRelativePath;
    }

    public String getPathToDestFolder() {
        return pathToDestFolder;
    }

    public void setPathToDestFolder(String pathToDestFolder) {
        this.pathToDestFolder = pathToDestFolder;
    }

    public String getPathFromDestFolder() {
        return pathFromDestFolder;
    }

    public int getDrillDownCount() {
        return drillDownCount;
    }

    public void setDestPaths(boolean setFromDefault, String destFolderParent, File destFolder) {
        drillDownCount =  setFromDefault ? defaultDrillDownCount : drillDownCount;
        setPathToDestFolder("../".repeat(drillDownCount) + FileUtils.removeFileExtension(destFolderRelativePath));
        this.destFolderFile = destFolder;
        setPathFromDestFolder(destFolderParent);
    }

    private void setPathFromDestFolder(String parentFolderName){
        if(parentFolderName == null || parentFolderName.isEmpty())
            return;
        pathFromDestFolder = this.paths.length == 0 ? "./" : "../".repeat(this.paths.length) ;
        List<File> fileList = new ArrayList<File>();

        File currentFile = this.file;
        fileList.add(currentFile);
        while(!parentFolderName.equals(currentFile.getParentFile().getName())){
            currentFile = currentFile.getParentFile();
            fileList.add(currentFile);
        }
        Collections.reverse(fileList);
        pathFromDestFolder = fileList.stream()
                .map(f -> f.getName())
                .collect(Collectors.joining("/", pathFromDestFolder,""));
    }

    public static ArrayList<File> getSelectedFileList() {
        return selectedFileList;
    }

    public static void selectFile(int idx) {
        FileComponent.selectedFile = selectedFileList.get(idx);
    }

    public static File getSelectedFile() {
        return FileComponent.selectedFile;
    }

    public static List<File> selectFiles(File folder, String fileSearchString){
        selectedFileList = new ArrayList<File>();
        for (File f : folder.listFiles()) {
            if (!f.isDirectory() && f.getName().contains(fileSearchString)) {
                selectedFileList.add(f);
            }
        }
        return selectedFileList;
    }


    public void incDrillDownCount() {
        ++drillDownCount;
    }
}
