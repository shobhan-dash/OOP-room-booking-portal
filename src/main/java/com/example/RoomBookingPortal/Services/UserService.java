package com.example.RoomBookingPortal.Services;

import com.example.RoomBookingPortal.APIEndpoints.UserAuth.UserDTO;
import com.example.RoomBookingPortal.DatabaseTables.User;
import com.example.RoomBookingPortal.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> signUp(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Forbidden, Account already exists");
        }

        user.setEmail(user.getEmail());
        user.setName(user.getName());
        user.setPassword(user.getPassword());
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body("Account Creation Successful");
    }

    public ResponseEntity<?> login(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }

        User retrievedUser = optionalUser.get();
        if (!retrievedUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username/Password Incorrect");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Login Successful");
    }


    public ResponseEntity<?> getUser(Long userID) {
        Optional<User> optionalUser = userRepository.findById(userID);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }

        User user = optionalUser.get();
        UserDTO userDTO = new UserDTO();
        userDTO.setUserID(user.getUserID());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());

        return ResponseEntity.ok(userDTO);
    }


}