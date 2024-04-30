package com.example.RoomBookingPortal.APIEndpoints;

import com.example.RoomBookingPortal.Models.DTOs.RoomDTO;
import com.example.RoomBookingPortal.Services.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
public class RoomsManager {

    private final RoomsService roomsService;

    @Autowired
    public RoomsManager(RoomsService roomsService) {
        this.roomsService = roomsService;
    }

    @GetMapping
    public ResponseEntity<?> getRoom(@RequestParam(required = false) Integer roomCapacity) {
         return roomsService.getRoom(roomCapacity);
    }

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
