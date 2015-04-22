package jet.task.previewer.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests {@link FTPClientUtils}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FTPClientUtilsTest {
    @Mock FTPClient ftpClient;

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
        Assert.assertEquals("/", FTPClientUtils.getParentPathname("/a"));
        Assert.assertEquals("/", FTPClientUtils.getParentPathname("/b/"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getParentPathnameOfRoot() {
        FTPClientUtils.getParentPathname("/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getParentPathnameOfDouble() {
        FTPClientUtils.getParentPathname("//");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyPathnameParent() {
        FTPClientUtils.getParentPathname("  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPathnameWithoutParent() {
        FTPClientUtils.getParentPathname("no parent");
    }

    @Test
    public void testRelativePath() throws Exception {
        Assert.assertEquals("one/two", FTPClientUtils.relativePath("one", "two"));
        Assert.assertEquals("/a/b", FTPClientUtils.relativePath("/a", "b"));
        Assert.assertEquals("/user", FTPClientUtils.relativePath("/", "user"));
    }

    @Test
    public void testHasParent() throws Exception {
        Assert.assertTrue(FTPClientUtils.hasParent("a/b"));
        Assert.assertTrue(FTPClientUtils.hasParent("/a"));
        Assert.assertFalse(FTPClientUtils.hasParent("a"));
        Assert.assertFalse(FTPClientUtils.hasParent("/"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasParentOfEmptyPath() throws Exception {
        FTPClientUtils.hasParent(" ");
    }

    @Test
    public void testDisconnectQuietly() throws Exception {
        FTPClientUtils.disconnectQuietly(ftpClient);
        verify(ftpClient).disconnect();
        verifyNoMoreInteractions(ftpClient);
    }

    @Test
    public void testDisconnectQuietlyWithException() throws Exception {
        Mockito.doThrow(new IOException()).when(ftpClient).disconnect();
        FTPClientUtils.disconnectQuietly(ftpClient);
        verify(ftpClient).disconnect();
        verifyNoMoreInteractions(ftpClient);
    }

    @Test
    public void testCompletePendingCommandQuietlySuccess() throws Exception {
        when(ftpClient.completePendingCommand()).thenReturn(true);

        FTPClientUtils.completePendingCommandQuietly(ftpClient);
        verify(ftpClient).completePendingCommand();
    }

    @Test
    public void testCompletePendingCommandQuietlyFail() throws Exception {
        when(ftpClient.completePendingCommand()).thenReturn(false);

        FTPClientUtils.completePendingCommandQuietly(ftpClient);
        verify(ftpClient).completePendingCommand();
    }

    @Test
    public void testCompletePendingCommandQuietlyException() throws Exception {
        when(ftpClient.completePendingCommand()).thenThrow(new IOException());

        FTPClientUtils.completePendingCommandQuietly(ftpClient);
        verify(ftpClient).completePendingCommand();
    }

    @Test
    public void testGetServerReplyInformation() throws Exception {
        when(ftpClient.getReplyCode()).thenReturn(200);
        when(ftpClient.getReplyString()).thenReturn("OK");
        String information = FTPClientUtils.getServerReplyInformation(ftpClient);
        Assert.assertEquals("FTP server reply code 200, reply string OK", information);
    }
}