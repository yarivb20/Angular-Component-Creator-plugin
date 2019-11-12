package outbrain.component;

import outbrain.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileComponent {
    private final File file;
    private final String destFolder;
    private final String[] paths;
    private String pathToDestFolder;
    private String pathFromDestFolder;
    private final int defaultDrillDownCount;
    private int drillDownCount = 1;

    public FileComponent(final File file, final String destFolder, final int defaultDrillDownCount) {
        this.file = file;
        this.destFolder = destFolder;
        paths = destFolder.split("/");
        this.defaultDrillDownCount = defaultDrillDownCount;
        FileUtils.findFilePath(this);
    }
    public FileComponent(final String filePath, final String destFolder, final int defaultDrillDownCount) {
        this.file = new File(filePath);
        this.destFolder = destFolder;
        paths = destFolder.split("/");
        this.defaultDrillDownCount = defaultDrillDownCount;
        FileUtils.findFilePath(this);
    }

    public File getFile() {
        return file;
    }

    public String[] getPaths() {
        return paths;
    }

    public String getDestFolder() {
        return destFolder;
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

    public void setDestPaths(boolean setFromDefault, String destFolderParent) {
        drillDownCount =  setFromDefault ? defaultDrillDownCount : drillDownCount;
        setPathToDestFolder("../".repeat(drillDownCount) + destFolder);
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

    public void incDrillDownCount() {
        ++drillDownCount;
    }
}
