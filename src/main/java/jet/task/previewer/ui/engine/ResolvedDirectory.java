package jet.task.previewer.ui.engine;

import java.util.List;

/**
 * Created by akoshevoy on 01.04.2015.
 */
public interface ResolvedDirectory<E extends DirectoryElement> {
    String getCurrentDirectory();

    List<E> getDirectoryContent();
}
