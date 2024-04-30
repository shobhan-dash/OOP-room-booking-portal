package com.example.RoomBookingPortal.Services;

import com.example.RoomBookingPortal.Models.DTOs.BookingDTO;
import com.example.RoomBookingPortal.Models.DTOs.RoomDTO;
import com.example.RoomBookingPortal.Models.DTOs.RoomFiltersDTO;
import com.example.RoomBookingPortal.Models.DatabaseTables.Booking;
import com.example.RoomBookingPortal.Models.DatabaseTables.Room;
import com.example.RoomBookingPortal.Repositories.RoomsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RoomsService {

    private final RoomsRepository roomsRepository;

    @Autowired
    public RoomsService(RoomsRepository roomsRepository) {
        this.roomsRepository = roomsRepository;
    }

    public ResponseEntity<?> getRoom(Integer roomCapacity) {
        if (roomCapacity != null && roomCapacity <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Invalid parameters"));
        }

        List<Room> rooms;

        if (roomCapacity != null) {
            rooms = roomsRepository.findByCapacity(roomCapacity);
        } else {
            rooms = roomsRepository.findAllRooms(); // Fetch all rooms if capacity not specified
        }

        List<Booking> allBookings;
        List<BookingDTO> bookingPerRoom = new ArrayList<>();
        List<RoomFiltersDTO> roomsFiltered = new ArrayList<>();

        allBookings = roomsRepository.findAllBookings();

        for(Room room : rooms){
            RoomFiltersDTO roomFiltered = new RoomFiltersDTO();
            roomFiltered.setRoomID(room.getRoomID());
            roomFiltered.setRoomName(room.getRoomName());
            roomFiltered.setRoomCapacity(room.getRoomCapacity());

            for(Booking book : allBookings){
                if(!Objects.equals(book.getRoom().getRoomID(), room.getRoomID())){
                    continue;
                }

                RoomFiltersDTO.Booked booking = new RoomFiltersDTO.Booked();
                booking.setBookingID(book.getBookingID());
                booking.setDateOfBooking(book.getDateOfBooking());
                booking.setTimeFrom(book.getTimeFrom());
                booking.setTimeTo(book.getTimeTo());
                booking.setPurpose(book.getPurpose());

                RoomFiltersDTO.Booked.User us = new RoomFiltersDTO.Booked.User();
                us.setUserID(book.getUser().getUserID());

                booking.setUser(us);
                roomFiltered.addBooked(booking);
            }

            roomsFiltered.add(roomFiltered);
        }

        return ResponseEntity.status(HttpStatus.OK).body(roomsFiltered);
    }

    public ResponseEntity<?> addRoom(RoomDTO roomDTO) {
        if (roomsRepository.existsByRoomName(roomDTO.getRoomName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Room already exists"));
        }

        if (roomDTO.getRoomCapacity() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Invalid capacity"));
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Room does not exist"));
        }

        if (roomDTO.getRoomCapacity() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Invalid capacity"));
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

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ObjectMapper().createObjectNode().put("Error", "Room does not exist"));
    }
}
