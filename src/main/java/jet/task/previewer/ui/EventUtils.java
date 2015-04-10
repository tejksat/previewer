package jet.task.previewer.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.JList;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 * Utility methods to deal with events from {@link JList}.
 */
public class EventUtils {
    private EventUtils() {
    }

    /**
     * Checks if {@code mouseEvent} is pure primary mouse button double click with no modifier keys pressed.
     *
     * @param mouseEvent mouse event
     * @return {@code true} if {@code mouseEvent} is pure primary mouse button double click with no modifier keys
     * pressed and {@code false} otherwise
     */
    public static boolean isPrimaryActionDoubleClick(@NotNull MouseEvent mouseEvent) {
        return mouseEvent.getButton() == MouseEvent.BUTTON1
                && mouseEvent.getClickCount() == 2
                && hasNoModifiers(mouseEvent);
    }

    private static boolean hasNoModifiers(@NotNull MouseEvent mouseEvent) {
        int offMask = MouseEvent.ALT_DOWN_MASK | MouseEvent.CTRL_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK;
        return (mouseEvent.getModifiersEx() & offMask) == 0;
    }

    /**
     * Looks for a list item for which specified {@code point} lies within its bounds.
     *
     * @param list  list to be searched
     * @param point point
     * @return optional index of a list item for which specified {@code point} lies within its bounds
     */
    @NotNull
    public static Optional<Integer> getListIndexAtPoint(@NotNull JList<?> list, @NotNull Point point) {
        if (list.getFirstVisibleIndex() != -1) {
            for (int i = list.getFirstVisibleIndex(); i <= list.getLastVisibleIndex(); i++) {
                if (list.getCellBounds(i, i).contains(point)) {
                    return Optional.of(i);
                }
            }
        }
        return Optional.empty();
    }
}
