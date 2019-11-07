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
        setStylePath(new File(componentDirectory.getCanonicalPath()));


        FileUtils utils = new FileUtils();
        TemplateRenderer renderer = new TemplateRenderer();

        for (String fileExtension: FILE_EXTENSIONS) {
            utils.writeFile(renderer.render(getFileTemplateName(fileExtension), templateModel), componentDirectory.createChildData(componentDirectory, getFileName(fileExtension)));
        }

    }

    private String setCamelCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void setStylePath(File filePath) {
        final int drillDownCount = findDir(filePath.getParentFile(), "style", 1);
        String path = "../../style/variables";
        if(drillDownCount != -1) {
            path = "../".repeat(drillDownCount) + "style/variables";
        }

    }


    private int findDir(File filePath, String name, int drillDownCount)
    {
        if(filePath == null) {
            return -1;
        }
        File[] files = filePath.listFiles();
        for (File f : files)
        {
            if(f.isDirectory() && f.getName().equals(name))
            {
                return drillDownCount;
            }
        }
        return findDir(filePath.getParentFile(), name, ++drillDownCount);
    }

    private String getFileTemplateName(String fileExtension) {
        return "templates/component/component." + fileExtension + ".mustache";
    }

    private String getFileName(String fileExtension) {
        return componentName + "." + fileExtension;
    }

}
