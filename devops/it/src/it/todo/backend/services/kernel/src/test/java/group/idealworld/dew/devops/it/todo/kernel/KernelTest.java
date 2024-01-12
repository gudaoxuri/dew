package group.idealworld.dew.devops.it.todo.kernel;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Page;
import group.idealworld.dew.devops.it.todo.kernel.domain.Todo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Kernel test.
 *
 * @author gudaoxuri
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TodoKernelApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class KernelTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * Test all.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testAll() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            Todo todo = testRestTemplate.postForObject("/api", "Item " + i, Todo.class);
            Assert.assertEquals("Item " + i, todo.getContent());
            Assert.assertNotNull(todo.getCreateTime());
            Thread.sleep(1);
            if (i >= 15) {
                testRestTemplate.delete("/api/" + todo.getId());
            }
        }
        Page page = testRestTemplate.getForObject("/api", Page.class);
        Assert.assertEquals(15, page.getRecordTotal());
        Assert.assertEquals(10, page.getPageSize());
        Assert.assertEquals("Item 0", $.json.toObject(page.getObjects().get(0), Todo.class).getContent());
        Assert.assertTrue(page.hasNextPage());
        page = testRestTemplate.getForObject("/api?pageNumber=2", Page.class);
        Assert.assertEquals(15, page.getRecordTotal());
        Assert.assertEquals("Item 10", $.json.toObject(page.getObjects().get(0), Todo.class).getContent());
        Assert.assertFalse(page.hasNextPage());
    }
}
