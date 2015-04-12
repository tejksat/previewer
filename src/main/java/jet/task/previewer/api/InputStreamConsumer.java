package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Consumes provided input stream and returns result of the consumption.
 */
public interface InputStreamConsumer<R> {
    /**
     * Consumes provided input stream and returns result of the consumption. Consumer need not close provided input
     * stream, resources cleaning is the responsibility of the caller.
     *
     * @param inputStream input stream to be consumed
     * @return result of the consumption
     * @throws IOException
     */
    R accept(@NotNull InputStream inputStream) throws IOException;
}
