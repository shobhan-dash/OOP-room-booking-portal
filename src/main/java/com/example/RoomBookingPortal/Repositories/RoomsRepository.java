package com.example.RoomBookingPortal.Repositories;

import com.example.RoomBookingPortal.Models.DTOs.RoomDTO;
import com.example.RoomBookingPortal.Models.DTOs.RoomFiltersDTO;
import com.example.RoomBookingPortal.Models.DatabaseTables.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomsRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomName(String roomName);

    boolean existsByRoomName(String roomName);

    @Query("SELECT r FROM Room r WHERE r.roomCapacity >= :roomCapacity")
    List<Room> findByCapacity(@Param("roomCapacity") Integer roomCapacity);

    @Query("SELECT r FROM Room r")
    List<Room> findAllRooms();
}
