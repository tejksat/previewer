package jet.task.previewer.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link FileSystemUtils}.
 */
public class FileSystemUtilsTest {
    @Test
    public void testGetFilenameExtension() throws Exception {
        Assert.assertNull(FileSystemUtils.getFilenameExtension(""));
        Assert.assertNull(FileSystemUtils.getFilenameExtension("."));
        Assert.assertNull(FileSystemUtils.getFilenameExtension("before."));
        Assert.assertEquals("after", FileSystemUtils.getFilenameExtension(".after"));
        Assert.assertEquals("png", FileSystemUtils.getFilenameExtension("image.png"));
    }
}