package br.com.andersonsilva019.todolist.task;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andersonsilva019.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        Map<String, Object> responseData = new HashMap<String, Object>();

        var userId = request.getAttribute("userId");
        taskModel.setUserId((UUID) userId);

        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt())) {

            responseData.put("errorCode", 1);
            responseData.put("message", "The start date must be greater than the current date");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        if (currentDate.isAfter(taskModel.getEndAt())) {

            responseData.put("errorCode", 2);
            responseData.put("message", "The end date must be greater than the current date");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseData);
        }

        if (taskModel.getEndAt().isBefore(taskModel.getStartAt())) {
            responseData.put("errorCode", 3);
            responseData.put("message", "The end date must be greater than the start date");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseData);
        }

        var task = this.taskRepository.save(taskModel);

        responseData.put("task", task);

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @GetMapping
    public ResponseEntity<List<TaskModel>> list(HttpServletRequest request) {
        var userId = request.getAttribute("userId");

        var tasks = this.taskRepository.findByUserId((UUID) userId);

        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        var task = this.taskRepository.findById(id).orElse(null);

        if(task == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        var userId = request.getAttribute("userId");

        if(!task.getUserId().equals(userId)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");
        }

        Utils.copyNonNullProperties(taskModel, task);

        var taskUpdated = this.taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);

    }

}
