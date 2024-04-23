package com.example.RoomBookingPortal.Repositories;

import com.example.RoomBookingPortal.Models.DatabaseTables.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomsRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomName(String roomName);

    boolean existsByRoomName(String roomName);

//    List<RoomFiltersDTO> getRooms(Date date, String time, Integer capacity);
}
