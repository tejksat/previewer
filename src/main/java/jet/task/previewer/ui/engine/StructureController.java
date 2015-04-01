package jet.task.previewer.ui.engine;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 01.04.2015.
 */
public interface StructureController<E> {
    boolean isDirectory(@NotNull E element);

    boolean isFile(@NotNull E element);

    boolean canBeEntered(@NotNull E element);

    Future<StructureController<?>> changeDirectory(@NotNull E element,
                                                   @NotNull DoneCallback<StructureController<?>> doneCallback) throws IOException;

    InputStream getInputStream(@NotNull E element) throws IOException;

    String getCurrentDirectory();

    List<E> getListedElements();
}
