package outbrain.component;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import outbrain.util.AbstractCreator;
import outbrain.util.FileUtils;
import outbrain.util.TemplateRenderer;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentCreator extends AbstractCreator {
    private VirtualFile directory;
    private String componentName;
    private Map<String, Object> templateModel;
    private final String[] FILE_EXTENSIONS = {"cmp.ts","less","html"};
    private boolean withState;

    public ComponentCreator(VirtualFile directory, Map<String, Object> templateModel) {
        this.directory = directory;
        this.templateModel = templateModel;
        this.componentName = String.valueOf(templateModel.get("componentName"));
        this.withState = Boolean.TRUE.equals(this.templateModel.get("state"));

    }

    public void create() throws IOException {
        VirtualFile existingDirectory = VfsUtil.findRelativeFile(directory, componentName);
        if (existingDirectory != null) {
            return;
        }

        templateModel.put("componentNameCamel", FileUtils.setCamelCase(componentName));

        VirtualFile componentDirectory = directory.createChildDirectory(directory, componentName);
        setPaths(componentDirectory.getCanonicalPath());
        FileComponent modelFolder = (FileComponent) templateModel.get("modelFilesFolder");
        boolean isEntryComponent = Boolean.TRUE.equals(this.templateModel.get("entryComponent"));
        FileUtils.addModuleToModulesFile(modelFolder,componentName, isEntryComponent);
        TemplateRenderer renderer = new TemplateRenderer();

        for (String fileExtension: FILE_EXTENSIONS) {
            FileUtils.writeFile(renderer.render(getFileTemplateName(fileExtension), templateModel), componentDirectory.createChildData(componentDirectory, getFileName(fileExtension)));
        }
    }

    private void setPaths(String filePath){

        FileComponent styleFolder = new FileComponent(filePath, "style/variables.less", 2);
        templateModel.put("stylePath", styleFolder.getPathToDestFolder());
        if(this.withState) {
            FileComponent stateFolder = new FileComponent(filePath, "components/state-component/state-component.ts", 3);
            templateModel.put("stateComponentPath", stateFolder.getPathToDestFolder());
        }
    }

    private String getFileTemplateName(String fileExtension) {
        String stateExtension  = fileExtension.equals("cmp.ts") && this.withState ? "-with-state" : "";
        return "templates/component/component" + stateExtension + "."  + fileExtension + ".mustache";
    }

    private String getFileName(String fileExtension) {
        return componentName + "." + fileExtension;
    }

}
