package jet.task.previewer.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests {@link InitiallyResolvedResolverTest}.
 */
@RunWith(MockitoJUnitRunner.class)
public class InitiallyResolvedResolverTest {
    @Mock ResolvedDirectory<?> resolvedDirectory;

    @Test
    public void testSubmit() throws Exception {
        ResolvedDirectory<?> resultDirectory = InitiallyResolvedResolver.submit(resolvedDirectory, d -> {}).get();
        Assert.assertSame(resolvedDirectory, resultDirectory);
    }
}