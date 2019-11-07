package outbrain.component;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import outbrain.util.AbstractCreator;
import outbrain.util.FileUtils;
import outbrain.util.TemplateRenderer;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ComponentCreator extends AbstractCreator {
    private VirtualFile directory;
    private String componentName;
    private Map<String, Object> templateModel;
    private final String[] FILE_EXTENSIONS = {"cmp.ts","less","html"};

    public ComponentCreator(VirtualFile directory, String componentName, Map<String, Object> templateModel) {
        this.directory = directory;
        this.componentName = componentName;
        this.templateModel = templateModel;

    }

    public void create() throws IOException {
        VirtualFile existingDirectory = VfsUtil.findRelativeFile(directory, componentName);
        if (existingDirectory != null) {
            return;
        }

        templateModel.put("componentNameCamel", setCamelCase(componentName));

        VirtualFile componentDirectory = directory.createChildDirectory(directory, componentName);
        setPaths(new File(componentDirectory.getCanonicalPath()));


        FileUtils utils = new FileUtils();
        TemplateRenderer renderer = new TemplateRenderer();

        for (String fileExtension: FILE_EXTENSIONS) {
            utils.writeFile(renderer.render(getFileTemplateName(fileExtension), templateModel), componentDirectory.createChildData(componentDirectory, getFileName(fileExtension)));
        }

    }

    private String setCamelCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void setPaths(File filePath){
        templateModel.put("stylePath", findFilePath(filePath, "style/variables", 2));
        templateModel.put("stateComponentPath", findFilePath(filePath, "components/state-component/state-component", 3));
    }

    private String findFilePath(File filePath, final String destFolder, final int defaultDrillDownCount) {
        int drillDownCount = findDir(filePath.getParentFile(), destFolder.split("/"), 1);
        String path = "../../" + destFolder;
        drillDownCount = drillDownCount != -1 ? drillDownCount : defaultDrillDownCount;
        return "../".repeat(drillDownCount) + destFolder;
    }


    private int findDir(File filePath, String[] paths, int drillDownCount)
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
                if(checkPath(f, path.substring(path.indexOf('/'))))
                    return drillDownCount;
                else
                    continue;
            }
        }
        return findDir(filePath.getParentFile(), paths, ++drillDownCount);
    }

    private String getFileTemplateName(String fileExtension) {
        return "templates/component/component." + fileExtension + ".mustache";
    }

    private boolean checkPath(File currentPath, String path){
        String[] paths = path.split("/");
        File[] childFiles = currentPath.listFiles();
        for (File f : childFiles) {
            if (f.isDirectory() && f.getName().equals(paths[0])) {
                return paths.length <= 1 || checkPath(f, path.substring(path.indexOf('/')));
            }
        }
        return false;
    }

    private String getFileName(String fileExtension) {
        return componentName + "." + fileExtension;
    }

}
