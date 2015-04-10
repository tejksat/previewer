package jet.task.previewer.common;

import org.jetbrains.annotations.Nullable;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by Alex Koshevoy on 30.03.2015.
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

    public static Path getUserHomePath() {
        String userHome = System.getProperty("user.home");
        return userHome == null ? null : FileSystems.getDefault().getPath(userHome);
    }
}
