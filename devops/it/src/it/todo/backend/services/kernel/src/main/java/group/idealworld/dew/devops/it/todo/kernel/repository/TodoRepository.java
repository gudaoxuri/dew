package group.idealworld.dew.devops.it.todo.kernel.repository;

import group.idealworld.dew.devops.it.todo.kernel.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.annotation.Resource;

/**
 * The interface To-do repository.
 *
 * @author gudaoxuri
 */
@Resource
public interface TodoRepository extends JpaRepository<Todo, Integer> {

}
