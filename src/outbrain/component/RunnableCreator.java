package outbrain.component;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.Map;

public class RunnableCreator implements Runnable {
    private ComponentCreator creator;

    public RunnableCreator(VirtualFile directory,  Map<String, Object> templateModel){
        creator = new ComponentCreator(directory, templateModel);
    }

    @Override
    public void run() {
        try {
            creator.create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
