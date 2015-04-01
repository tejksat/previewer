package jet.task.previewer.ui.engine;

import java.util.concurrent.Future;

/**
 * Created by Alex Koshevoy on 01.04.2015.
 */
public interface DoneCallback<E> {
    void done(Future<E> future);
}
