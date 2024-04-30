package com.example.RoomBookingPortal.Repositories;

import com.example.RoomBookingPortal.Models.DTOs.UserDTO;
import com.example.RoomBookingPortal.Models.DatabaseTables.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u")
    List<User> findAllUsers();
}
