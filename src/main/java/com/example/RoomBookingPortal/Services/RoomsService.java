package com.example.RoomBookingPortal.Services;

import com.example.RoomBookingPortal.Models.DTOs.RoomDTO;
import com.example.RoomBookingPortal.Models.DatabaseTables.Room;
import com.example.RoomBookingPortal.Repositories.RoomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RoomsService {

    private final RoomsRepository roomsRepository;

    @Autowired
    public RoomsService(RoomsRepository roomsRepository) {
        this.roomsRepository = roomsRepository;
    }

//    public ResponseEntity<?> getRoom(Date date, String time, Integer capacity){
//        if (date == null && time == null && capacity == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters");
//        }
//
//        List<RoomFiltersDTO> rooms = roomsRepository.getRooms(date, time, capacity);
//        return ResponseEntity.status(HttpStatus.OK).body(rooms);
//    }
    public ResponseEntity<?> addRoom(RoomDTO roomDTO) {
        if (roomsRepository.existsByRoomName(roomDTO.getRoomName())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room already exists");
        }

        if (roomDTO.getRoomCapacity() <= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid capacity");
        }

        Room room = new Room();
        room.setRoomName(roomDTO.getRoomName());
        room.setRoomCapacity(roomDTO.getRoomCapacity());
        roomsRepository.save(room);

        return ResponseEntity.status(HttpStatus.OK).body("Room created successfully");
    }

    public ResponseEntity<?> editRoom(RoomDTO roomDTO) {
        Room room = roomsRepository.findById(roomDTO.getRoomID()).orElse(null);
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room does not exist");
        }

        if (roomDTO.getRoomCapacity() <= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid capacity");
        }

        room.setRoomName(roomDTO.getRoomName());
        room.setRoomCapacity(roomDTO.getRoomCapacity());
        roomsRepository.save(room);

        return ResponseEntity.status(HttpStatus.OK).body("Room edited successfully");
    }

    public ResponseEntity<?> deleteRoom(Long roomID) {
        if (roomsRepository.existsById(roomID)) {
            roomsRepository.deleteById(roomID);
            return ResponseEntity.status(HttpStatus.OK).body("Room deleted successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room does not exist");
    }
}
