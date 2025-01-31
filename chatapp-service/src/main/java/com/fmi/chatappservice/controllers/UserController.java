package com.fmi.chatappservice.controllers;

import com.fmi.chatappservice.data.User;
import com.fmi.chatappservice.services.FriendshipService;
import com.fmi.chatappservice.services.UserService;
import com.fmi.chatappservice.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final FriendshipService friendshipService;

    @Autowired
    public UserController(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            var result = userService.registerUser(user);
            return Response.buildResponse(HttpStatus.CREATED, result);
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> getUser(@RequestBody User credentials) {
        try {
            var user = userService.getUserByEmailAndPassword(credentials.getEmail(), credentials.getPassword());
            return Response.buildResponse(HttpStatus.OK, user);
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getUsers(@RequestParam(name = "search", required = false) String search) {
        var result = userService.searchUsers(search);
        return Response.buildResponse(HttpStatus.OK, result);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFriend(@RequestParam Long currentUserId, @RequestParam Long friendId) {
        try {
            var result = friendshipService.addFriend(currentUserId, friendId);
            return Response.buildResponse(HttpStatus.CREATED, result);
        } catch (Exception ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
