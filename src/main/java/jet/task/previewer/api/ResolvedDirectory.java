package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Directory with known content.
 */
public interface ResolvedDirectory<E extends DirectoryElement> {
    String getName();

    List<E> getContent();

    boolean hasParent();

    Future<ResolvedDirectory<?>> resolveParent(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException;

    void dispose();
}
