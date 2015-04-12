package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Represents an element of resolved directory that could be resolved (for example, it could FS directory or ZIP file)
 * or consumed as input stream (for example, an image or text file for showing a preview).
 */
public interface DirectoryElement {
    String getName();

    boolean isDirectory();

    boolean isFile();

    boolean canBeResolvedToDirectory();

    Future<ResolvedDirectory<?>> resolve(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException;

    <R> R consumeInputStream(InputStreamConsumer<R> consumer) throws IOException;
}
