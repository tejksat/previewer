package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public interface DirectoryElement {
    String getName();

    boolean isDirectory();

    boolean isFile();

    boolean canBeResolvedToDirectory();

    Future<ResolvedDirectory<?>> resolve(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException;

    <R> R consumeInputStream(InputStreamConsumer<R> consumer) throws IOException;
}
