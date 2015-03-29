package jet.task.previewer.common;

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
}
