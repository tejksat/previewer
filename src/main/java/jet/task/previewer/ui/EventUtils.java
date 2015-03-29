package jet.task.previewer.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.JList;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class EventUtils {
    private EventUtils() {
    }

    public static boolean isPrimaryActionDoubleClick(@NotNull MouseEvent mouseEvent) {
        return mouseEvent.getButton() == MouseEvent.BUTTON1
                && mouseEvent.getClickCount() == 2
                && hasNoModifiers(mouseEvent);
    }

    public static boolean hasNoModifiers(@NotNull MouseEvent mouseEvent) {
        int offMask = MouseEvent.ALT_DOWN_MASK | MouseEvent.CTRL_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK;
        return (mouseEvent.getModifiersEx() & offMask) == 0;
    }

    public static Optional<Integer> getListIndexAtPoint(@NotNull JList<?> list, @NotNull Point point) {
        // todo make it fancy with java stream api
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
