package com.example.RoomBookingPortal.APIEndpoints.Rooms;

import com.example.RoomBookingPortal.Services.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/rooms")
public class RoomsManager {

    private final RoomsService roomsService;

    @Autowired
    public RoomsManager(RoomsService roomsService) {
        this.roomsService = roomsService;
    }

//    @GetMapping
//    public ResponseEntity<?> getRoom(@RequestParam(required = false) Date date,
//                                     @RequestParam(required = false) String time,
//                                     @RequestParam(required = false) Integer capacity) {
//         return roomsService.getRoom(date, time, capacity);
//    }

    @PostMapping
    public ResponseEntity<?> addRoom(@RequestBody RoomDTO roomDTO) {
        return roomsService.addRoom(roomDTO);
    }

    @PatchMapping
    public ResponseEntity<?> editRoom(@RequestBody RoomDTO roomDTO) {
        return roomsService.editRoom(roomDTO);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteRoom(@RequestBody RoomDTO roomDTO) {
        return roomsService.deleteRoom(roomDTO.getRoomID());
    }
}
