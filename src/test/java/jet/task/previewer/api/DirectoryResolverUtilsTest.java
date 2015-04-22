package jet.task.previewer.api;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link DirectoryResolverUtils}.
 */
public class DirectoryResolverUtilsTest {
    @Test
    public void testIsZipPathname() throws Exception {
        Assert.assertTrue(DirectoryResolverUtils.hasZipExtension("/usr/home/books.zip"));
        Assert.assertTrue(DirectoryResolverUtils.hasZipExtension("/usr/home/work.ZIP"));
        Assert.assertTrue(DirectoryResolverUtils.hasZipExtension("/usr/home/secret.Zip"));
        Assert.assertFalse(DirectoryResolverUtils.hasZipExtension("/usr/bin/bin.gzip"));
        Assert.assertFalse(DirectoryResolverUtils.hasZipExtension("/usr/project/readme"));
        Assert.assertFalse(DirectoryResolverUtils.hasZipExtension("/usr/home/oldie.zip.bak"));
    }
}