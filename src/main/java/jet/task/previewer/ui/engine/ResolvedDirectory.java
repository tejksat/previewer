package jet.task.previewer.ui.engine;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 01.04.2015.
 */
public interface ResolvedDirectory<E extends DirectoryElement> {
    String getCurrentDirectory();

    List<E> getDirectoryContent();

    boolean hasParent();

    Future<ResolvedDirectory<?>> resolveParent(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException;
}
