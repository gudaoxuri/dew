/*
 * Copyright 2019. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ms.dew.devops.it.todo.kernel;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Page;
import ms.dew.devops.it.todo.kernel.domain.Todo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TodoKernelApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class KernelTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testAll() throws IOException, InterruptedException {
        for (int i = 0; i < 20; i++) {
            Todo todo = testRestTemplate.postForObject("/api", "Item " + i, Todo.class);
            Assert.assertEquals("Item " + i, todo.getContent());
            Assert.assertTrue(todo.getCreateTime() != null);
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
