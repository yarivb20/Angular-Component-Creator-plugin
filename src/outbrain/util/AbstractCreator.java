package outbrain.util;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class AbstractCreator {
    public abstract void create() throws IOException, URISyntaxException;
}
