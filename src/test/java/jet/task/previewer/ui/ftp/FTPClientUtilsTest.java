package jet.task.previewer.ui.ftp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by akoshevoy on 06.04.2015.
 */
public class FTPClientUtilsTest {
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
}