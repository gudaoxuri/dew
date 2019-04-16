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

package ms.dew.devops.it.todo.kernel.service;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Page;
import ms.dew.Dew;
import ms.dew.devops.it.todo.common.Constants;
import ms.dew.devops.it.todo.kernel.domain.Todo;
import ms.dew.devops.it.todo.kernel.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * To-do service.
 *
 * @author gudaoxuri
 */
@Service
public class TodoService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TodoRepository todoRepository;

    /**
     * List page.
     *
     * @param pageNumber page number，从1开始
     * @param pageSize   page size
     * @return the page
     */
    public Page<Todo> list(int pageNumber, int pageSize) {
        org.springframework.data.domain.Page<Todo> springPage =
                todoRepository.findAll(PageRequest.of(pageNumber - 1, pageSize, Sort.by("sort").ascending()));
        return Page.build(springPage.getNumber() + 1,
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getContent());
    }

    /**
     * Add int.
     *
     * @param content the content
     * @return id int
     */
    public Todo add(String content) {
        if (content.trim().startsWith("=")) {
            // 去掉 = 号
            content = content.trim().substring(1);
            // 此为幂等修改操作，故使用 put 方法
            // restTemplate 的 put 方法没有返回值，只能使用此方式
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            HttpEntity<String> entity = new HttpEntity<>(content, headers);
            // 使用Spring的 restTemplate 实现服务间 rest 调用
            content = restTemplate
                    .exchange("http://" + Constants.REST_COMPUTE_SERVICE + "/compute", HttpMethod.PUT, entity, String.class)
                    .getBody();
        }
        Todo todo = new Todo();
        todo.setContent(content);
        todo.setSort(System.currentTimeMillis());
        todo = todoRepository.save(todo);
        // 使用Dew的集群MQ功能实现消息点对点发送
        Dew.cluster.mq.request(Constants.MQ_NOTIFY_TODO_ADD, $.json.toJsonString(todo));
        return todo;
    }

    /**
     * Sort.
     *
     * @param id      the id
     * @param afterBy the after by
     * @return <b>true</b> if success
     */
    public boolean sort(int id, int afterBy) {
        long beforeTodoSort = todoRepository.getOne(afterBy).getSort();
        Todo todo = todoRepository.getOne(id);
        todo.setSort(beforeTodoSort + 1);
        todoRepository.save(todo);
        return true;
    }

    /**
     * Delete.
     *
     * @param id the id
     * @return <b>true</b> if success
     */
    public boolean delete(int id) {
        todoRepository.deleteById(id);
        Dew.cluster.mq.request(Constants.MQ_NOTIFY_TODO_DEL, id + "");
        return true;
    }
}
