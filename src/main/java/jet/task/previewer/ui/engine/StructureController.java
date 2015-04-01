package jet.task.previewer.ui.engine;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;

/**
 * Created by akoshevoy on 01.04.2015.
 */
public interface StructureController<E> {
    boolean isDirectory(E element);

    StructureController<?> changeDirectory(E element) throws ChangeDirectoryException;
}
