package br.com.shmull.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.shmull.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    try {
      var idUser = request.getAttribute("idUser");
      taskModel.setIdUser((UUID) idUser);

      var currentDateTime = LocalDateTime.now();
      if (currentDateTime.isAfter(taskModel.getStartAt()) || currentDateTime.isAfter(taskModel.getEndAt())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de inicio ou fim da task deve ser maior que a data atual");
      }

      if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de inicio da task deve ser menor que a data de fim");
      }

      var taskCreated = this.taskRepository.save(taskModel);
      return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/")
  public ResponseEntity list(HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    var tasks = this.taskRepository.findByIdUser((UUID) idUser);
    return ResponseEntity.ok(tasks);
  }

  @PutMapping("/{idTask}")
  public ResponseEntity<?> update(@RequestBody TaskModel taskModel, @PathVariable UUID idTask, HttpServletRequest request) {
    var task = this.taskRepository.findById(idTask).orElseThrow(() -> new RuntimeException("Task not found"));

    if (task == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
    }

    if (!task.getIdUser().equals(request.getAttribute("idUser"))) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User dont have permission");
    }

    Utils.copyNonNullProperties(taskModel, task);
    var taskUpdated = this.taskRepository.save(task);
    return ResponseEntity.ok().body(taskUpdated);
  }
}
