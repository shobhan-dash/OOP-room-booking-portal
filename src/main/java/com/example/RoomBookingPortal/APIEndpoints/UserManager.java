package com.example.RoomBookingPortal.APIEndpoints;

import com.example.RoomBookingPortal.Models.DatabaseTables.User;
import com.example.RoomBookingPortal.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserManager {

    private final UserService userService;

    @Autowired
    public UserManager(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return userService.login(user);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        return userService.signUp(user);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@RequestParam Long userID) {
        return userService.getUser(userID);
    }
}
