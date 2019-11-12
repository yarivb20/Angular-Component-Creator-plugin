package outbrain.component;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import outbrain.util.AbstractCreatorAction;
import outbrain.util.FileUtils;

import java.awt.*;

public class ComponentCreatorAction extends AbstractCreatorAction {
    private final String[] MODULE_FILES = {"app.modules.ng4.ts","reports-shared.module.ts","shared.modules.ts"};
    @Override
    public void update(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

        Navigatable navigatable = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
        anActionEvent.getPresentation().setEnabledAndVisible(project != null && navigatable != null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        ComponentCreatorDialog dialog = new ComponentCreatorDialog();
        VirtualFile selectedLocation = e.getData(CommonDataKeys.VIRTUAL_FILE);
        VirtualFile targetLocation = getLocation(selectedLocation);
        dialog.setModelFilesList(MODULE_FILES);

        final int width = dialog.getWidth();
        final int height = dialog.getHeight();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width / 2) - (width / 2);
        int y = (screenSize.height / 2) - (height / 2);
        dialog.setLocation(x, y);

        dialog.pack();
        dialog.setVisible(true);

        final String componentName = dialog.getComponentName();

        if (dialog.isCanceled()) {
            return;
        }

        ApplicationManager.getApplication().runWriteAction(
                new RunnableCreator(targetLocation, dialog.getTemplateVars())
        );
    }
}
