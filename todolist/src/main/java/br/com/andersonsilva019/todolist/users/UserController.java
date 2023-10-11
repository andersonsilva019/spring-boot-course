package br.com.andersonsilva019.todolist.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired  
    private IUserRepository userRepository;

    @PostMapping
    public ResponseEntity create(@RequestBody  UserModel user){
        var userModel = this.userRepository.findByUsername(user.getUsername());

        if(userModel != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }

        var userCreate = this.userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreate);
    }
}
