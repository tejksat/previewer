package jet.task.previewer.common;

import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for dealing with strings.
 */
public class StringUtils {
    private StringUtils() {
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    @Nullable
    public static String defaultIfEmpty(@Nullable String string, @Nullable String defaultString) {
        return isNotEmpty(string) ? string : defaultString;
    }
}
