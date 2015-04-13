package jet.task.previewer.ftp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link FTPClientUtils}.
 */
public class FTPClientUtilsTest {
    @Test
    public void testFormatFTPReplyString() throws Exception {
        Assert.assertNull(FTPClientUtils.formatFTPReplyString(null));
        Assert.assertEquals("Reply code is 0", FTPClientUtils.formatFTPReplyString("Reply code is 0"));
        Assert.assertEquals("Error occurred|Timeout", FTPClientUtils.formatFTPReplyString("Error occurred\nTimeout"));
    }

    @Test
    public void testGetParentPathname() {
        Assert.assertEquals("/a/b/c", FTPClientUtils.getParentPathname("/a/b/c/d"));
        Assert.assertEquals("/abc", FTPClientUtils.getParentPathname("/abc/edg/"));
        Assert.assertEquals("/abc", FTPClientUtils.getParentPathname("/abc/edg//"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getParentPathnameOfRoot() {
        FTPClientUtils.getParentPathname("/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getParentPathnameOfDouble() {
        FTPClientUtils.getParentPathname("//");
    }

    @Test
    public void testRelativePath() throws Exception {
        Assert.assertEquals("one/two", FTPClientUtils.relativePath("one", "two"));
        Assert.assertEquals("/a/b", FTPClientUtils.relativePath("/a", "b"));
        Assert.assertEquals("/user", FTPClientUtils.relativePath("/", "user"));
    }
}