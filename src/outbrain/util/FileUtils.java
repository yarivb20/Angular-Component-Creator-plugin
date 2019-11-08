package outbrain.util;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class FileUtils {
    private FileUtils() {
    }

    public static String getContent(String fileName) {
        StringBuilder result = new StringBuilder("");
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        InputStream resourceStream = classLoader.getResourceAsStream(fileName);

        Scanner scanner = new Scanner(resourceStream);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            result.append(line).append("\n");
        }

        scanner.close();
        return result.toString();
    }

    public static String findFilePath(String filePath, final String destFolder, final int defaultDrillDownCount) {
        File file = new File(filePath);
        int drillDownCount = findDir(file.getParentFile(), destFolder.split("/"), 1);
        String path = "../../" + destFolder;
        drillDownCount = drillDownCount != -1 ? drillDownCount : defaultDrillDownCount;
        return "../".repeat(drillDownCount) + destFolder;
    }

    public static List<File> getModuleFilesList(String filePath, final String destFolder) {
        File file = new File(filePath).getParentFile();
        String[] paths = destFolder.split("/");
        int numberOfDrillDown = findDir(file, destFolder.split("/"), 1);
        File newFile;
        do{
            newFile = file.getParentFile();
            --numberOfDrillDown;
        }
        while(numberOfDrillDown >= 0);
        for (String path: paths) {
            File[] files = newFile.listFiles();
            for (File f : files)
            {
                if(f.isDirectory() && f.getName().equals(path))
                {
                    newFile = f;
                }
            }
        }
        return findModuleFile(newFile);
    }

    private static int findDir(File filePath, String[] paths, int drillDownCount)//drill down to find the path
    {
        if(filePath == null) {
            return -1;
        }
        File[] files = filePath.listFiles();
        for (File f : files)
        {
            if(f.isDirectory() && f.getName().equals(paths[0]))
            {
                String path = String.join("/", paths);
                if(checkPath(f, path.substring(path.indexOf('/')+1))) {
                    filePath = f;
                    return drillDownCount;
                }
                else
                    continue;
            }
        }
        return findDir(filePath.getParentFile(), paths, ++drillDownCount);
    }

    private static boolean checkPath(File currentPath, String path){//check if a relative path exists in the path
        String[] paths = path.split("/");
        File[] childFiles = currentPath.listFiles();
        for (File f : childFiles) {
            if (f.isDirectory() && f.getName().equals(paths[0])) {
                return paths.length <= 1 || checkPath(f, path.substring(path.indexOf('/')+1));
            }
        }
        return false;
    }

    private static List<File> findModuleFile(File currentPath){
        File[] childFiles = currentPath.listFiles();
        List<File> moduleFiles = new ArrayList<File>();
        for (File f : childFiles) {
            if (!f.isDirectory() && f.getName().contains("module")) {
                moduleFiles.add(f);
            }
        }
        return moduleFiles;
    }

    public static void writeFile(String content, VirtualFile destinationFile) throws IOException {
        destinationFile.setBinaryContent(content.getBytes());
    }
}
