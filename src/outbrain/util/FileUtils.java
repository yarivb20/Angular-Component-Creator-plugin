package outbrain.util;

import com.intellij.openapi.vfs.VirtualFile;
import outbrain.component.FileComponent;

import java.io.*;
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

    public static void findFilePath(FileComponent fileComponent) {
       findDir(fileComponent.getFile(), fileComponent);
    }


//    public static List<File> getModuleFilesList(String filePath, final String destFolder) {
//        File file = new File(filePath).getParentFile();
//        String[] paths = destFolder.split("/");
//        int numberOfDrillDown = findDir(file, destFolder.split("/"), 1);
//        File newFile;
//        do{
//            newFile = file.getParentFile();
//            --numberOfDrillDown;
//        }
//        while(numberOfDrillDown >= 0);
//        for (String path: paths) {
//            File[] files = newFile.listFiles();
//            for (File f : files)
//            {
//                if(f.isDirectory() && f.getName().equals(path))
//                {
//                    newFile = f;
//                }
//            }
//        }
//        return findModuleFile(newFile);
//    }

    private static void findDir(File filePath, FileComponent fileComponent)//drill down to find the path
    {
        if(filePath == null) {
            fileComponent.setDestPaths(true, null);
            return;
        }
        File[] files = filePath.listFiles();
        for (File f : files)
        {
            if(f.isDirectory() && f.getName().equals(fileComponent.getPaths()[0]))
            {
                String path = String.join("/", fileComponent.getPaths());
                if(checkPath(f, path.substring(path.indexOf('/')+1))) {
                    fileComponent.setDestPaths(false, f.getParentFile().getName());
                    return;
                }
                else
                    continue;
            }
        }
        fileComponent.incDrillDownCount();
        findDir(filePath.getParentFile(), fileComponent);
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

    public static void addModuleToModulesFile(File moduleFile, String componentName, String componentPath) {
        List<String> lines = new ArrayList<String>();
        String line = null;
        try {
            boolean inImports = false;
            boolean inDeclarations = false;
            boolean added = false;
            String prevLine = "";
            FileReader fr = new FileReader(moduleFile);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                if(inDeclarations && line.contains("]")){
                    lines.set(lines.size() - 1, lines.get(lines.size() - 1) + ",");
                    lines.add(" ".repeat(getIndentation(prevLine)) + componentName);
                    added = true;
                    inDeclarations = false;
                }else if(inImports && line.trim().isEmpty()){
                    lines.add("import {" + componentName + "} from '" + componentPath + "';");
                    inImports = false;
                }
                if(line.contains("declarations")){
                    inDeclarations = true;
                }
                else if(line.startsWith("import")){
                    inImports = true;
                }
                prevLine = line;
                lines.add(line);

            }
            fr.close();
            br.close();

            if(added){
                FileWriter fw = new FileWriter(moduleFile);
                BufferedWriter out = new BufferedWriter(fw);
                for(String s : lines) {
                    out.write(s);
                    out.newLine();
                }
                out.flush();
                out.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int getIndentation(String line) {
        int indentation = 0;
        char[] characters = line.toCharArray();
        for(int i = 0; i < line.length(); i++){
            if(!Character.isWhitespace(characters[i])){
                break;
            }
            indentation++;
        }
        return indentation;
    }
}
