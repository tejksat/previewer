package jet.task.previewer.ui;

import javax.swing.SwingUtilities;

/**
 * Utility methods for convenient work with event dispatch thread.
 */
public class EventDispatchThreadUtils {
    private EventDispatchThreadUtils() {
    }

    /**
     * Runs {@code runnable} now if in event dispatch thread. Otherwise schedules it for execution with
     * {@link SwingUtilities#invokeLater(Runnable)}.
     *
     * @param runnable runnable
     */
    public static void invokeASAP(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }
}
