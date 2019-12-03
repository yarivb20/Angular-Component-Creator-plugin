package outbrain.util;

import com.intellij.openapi.vfs.VirtualFile;
import outbrain.component.FileComponent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static FileComponent getModuleFilesFolder(String filePath, final String destFolder) {
        FileComponent fileComponent = new FileComponent(filePath, destFolder, 2);
        findDir(fileComponent.getFile(), fileComponent);
        fileComponent.selectFiles(fileComponent.getDestFolderFile(), "module");
        return fileComponent;
    }

    private static void findDir(File filePath, FileComponent fileComponent)//drill down to find the path
    {
        if(filePath == null) {
            fileComponent.setDestPaths(true, null, null);
            return;
        }
        File[] files = filePath.listFiles();
        for (File f : files)
        {
            if(f.isDirectory() && f.getName().equals(fileComponent.getPaths()[0]))
            {
                String path = String.join("/", fileComponent.getPaths());
                File fullPath = checkPath(f, path.substring(path.indexOf('/')+1));
                if(fullPath != null) {
                    fileComponent.setDestPaths(false, f.getParentFile().getName(), fullPath);
                    return;
                }
                else
                    continue;
            }
        }
        fileComponent.incDrillDownCount();
        findDir(filePath.getParentFile(), fileComponent);
    }

    private static File checkPath(File currentPath, String path){//check if a relative path exists in the path
        String[] paths = path.split("/");
        File[] childFiles = currentPath.listFiles();
        for (File f : childFiles) {
            if (f.getName().equals(paths[0])) {
                return paths.length <= 1 ? FileUtils.removeFileExtension(f)  : checkPath(f, path.substring(path.indexOf('/')+1));
            }
        }
        return null;
    }

    public static void writeFile(String content, VirtualFile destinationFile) throws IOException {
        destinationFile.setBinaryContent(content.getBytes());
    }

    public static void addModuleToModulesFile(FileComponent modelFolder, String componentName, boolean isEntryComponent) {
        String componentNameCamel = FileUtils.setCamelCase(componentName+"Component");
        List<String> lines = new ArrayList<String>();
        String line = null;
        try {
            boolean inImports = false;
            boolean inDeclarations = false;
            boolean added = false;
            String prevLine = "";
            FileReader fr = new FileReader(modelFolder.getSelectedFile());
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                if(inDeclarations && line.contains("]")){
                    lines.set(lines.size() - 1, lines.get(lines.size() - 1) + ",");
                    lines.add(" ".repeat(getLineIndentation(prevLine)) + componentNameCamel);
                    added = true;
                    inDeclarations = false;
                }else if(inImports && line.trim().isEmpty()){
                    String path = modelFolder.getPathFromDestFolder() + "/" + componentName + "/" + componentName + ".cmp";
                    lines.add("import {" + componentNameCamel + "} from '" + path + "';");
                    inImports = false;
                }
                if(line.contains("declarations") || isEntryComponent && line.contains("entryComponents")){
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
                FileWriter fw = new FileWriter(modelFolder.getSelectedFile());
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

    private static File removeFileExtension(File file){
        String path = file.getPath();
        if(path.indexOf(".less") != -1)
            path = path.substring(0, path.indexOf(".less"));
        else if(path.indexOf(".ts") != -1)
            path = path.substring(0, path.indexOf(".ts"));
        return new File(path);
    }

    public static String removeFileExtension(String path){
        if(path.indexOf(".less") != -1)
            path = path.substring(0, path.indexOf(".less"));
        else if(path.indexOf(".ts") != -1)
            path = path.substring(0, path.indexOf(".ts"));
        return path;
    }

    public static String setCamelCase(String str) {
        String[] strArray = str.split("[_-]");
        return Stream.of(strArray)
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(""));
    }

    private static int getLineIndentation(String line) {
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
