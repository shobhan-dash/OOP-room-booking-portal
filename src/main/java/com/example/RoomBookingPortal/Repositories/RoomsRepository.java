package com.example.RoomBookingPortal.Repositories;

import com.example.RoomBookingPortal.Models.DatabaseTables.Room;
import com.example.RoomBookingPortal.Models.DatabaseTables.Booking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Transactional
public interface RoomsRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomName(String roomName);

    boolean existsByRoomName(String roomName);

    @Query("SELECT r FROM Room r WHERE r.roomID = :roomID")
    Optional<Room> findRoomById(Long roomID);

    @Query("SELECT r FROM Room r WHERE r.roomCapacity >= :roomCapacity")
    List<Room> findByCapacity(@Param("roomCapacity") Integer roomCapacity);

    @Query("SELECT b FROM Booking b")
    List<Booking> findAllBookings();

    @Query("SELECT r FROM Room r")
    List<Room> findAllRooms();

    @Modifying
    @Query("DELETE FROM Room r WHERE r.roomID = :roomID")
    void deleteRoomById(@Param("roomID") Long roomID);
}
