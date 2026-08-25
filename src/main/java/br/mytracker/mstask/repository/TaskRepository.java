package br.mytracker.mstask.repository;

public class TaskRepository {
}
package br.mytracker.mstask.repository;

import br.mytracker.mstask.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}