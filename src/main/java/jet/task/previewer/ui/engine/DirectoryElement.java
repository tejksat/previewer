package jet.task.previewer.ui.engine;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public interface DirectoryElement<E> {
    boolean isDirectory();

    boolean isFile();

    boolean canBeResolvedToDirectory();

    Future<ResolvedDirectory<?>> resolve(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException;

    InputStream newInputStream() throws IOException;

    String getName();
}
