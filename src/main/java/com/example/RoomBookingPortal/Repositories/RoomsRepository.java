package com.example.RoomBookingPortal.Repositories;

import com.example.RoomBookingPortal.APIEndpoints.Rooms.RoomFiltersDTO;
import com.example.RoomBookingPortal.DatabaseTables.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RoomsRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomName(String roomName);

    boolean existsByRoomName(String roomName);

//    List<RoomFiltersDTO> getRooms(Date date, String time, Integer capacity);
}
