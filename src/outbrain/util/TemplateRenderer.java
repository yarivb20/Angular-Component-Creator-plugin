package outbrain.util;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.util.Map;

public class TemplateRenderer {
    public String render(String file, Map<String, Object> context) {
        Template tmpl = Mustache.compiler().compile(FileUtils.getContent(file));
        return tmpl.execute(context);
    }
}
