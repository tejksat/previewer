package jet.task.previewer.ui;

import org.jetbrains.annotations.Nullable;

/**
 * Something that have informational status that could be updated.
 */
public interface StatusHolder {
    /**
     * Updates informational status.
     *
     * @param status new status string
     */
    void updateStatus(@Nullable String status);
}
