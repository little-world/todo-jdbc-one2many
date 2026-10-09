package com.example.todo.controller;

import com.example.todo.model.Todo;
import com.example.todo.model.User;
import com.example.todo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public User findById(@PathVariable("id") Long userId) {
        return userService.findById(userId);
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PostMapping("/user/{id}/todo")
    @ResponseStatus(HttpStatus.CREATED)
    public Todo addTodo(@PathVariable("id") Long userId, @RequestBody Todo todo) {
        return userService.addTodo(userId, todo);
    }
}
