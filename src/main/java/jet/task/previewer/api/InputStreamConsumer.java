package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Like {@link java.util.function.Consumer}.
 */
public interface InputStreamConsumer<R> {
    R accept(@NotNull InputStream inputStream) throws IOException;
}
