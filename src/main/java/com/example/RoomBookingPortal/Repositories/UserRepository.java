package com.example.RoomBookingPortal.Repositories;

import com.example.RoomBookingPortal.DatabaseTables.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
