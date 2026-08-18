package br.mytracker.mstask.service;

import br.mytracker.mstask.domain.Task;
import br.mytracker.mstask.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> listar() {
        return taskRepository.findAll();
    }

    public Optional<Task> buscar(Long id) {
        return taskRepository.findById(id);
    }

    public Task salvar(Task task) {
        task.setId(null);
        return taskRepository.save(task);
    }

    public Optional<Task> atualizar(Long id, Task dados) {
        return taskRepository.findById(id).map(atual -> {
            atual.setTitulo(dados.getTitulo());
            atual.setDescricao(dados.getDescricao());
            atual.setPrioridade(dados.getPrioridade());
            atual.setConcluida(dados.isConcluida());
            return taskRepository.save(atual);
        });
    }

    public boolean deletar(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        return true;
    }
}