package com.example.RoomBookingPortal.APIEndpoints;

import com.example.RoomBookingPortal.Models.DTOs.BookingDTO;
import com.example.RoomBookingPortal.Services.BookingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookingsManager {

    private final BookingsService bookingsService;

    @Autowired
    public BookingsManager(BookingsService bookingsService) {
        this.bookingsService = bookingsService;
    }

    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO) {
        return bookingsService.createBooking(bookingDTO);
    }

    @PatchMapping("/book")
    public ResponseEntity<?> editRoom(@RequestBody BookingDTO bookingDTO) {
        return bookingsService.editBooking(bookingDTO);
    }

    @DeleteMapping("/book")
    public ResponseEntity<?> deleteBooking(@RequestParam Long bookingID) {
        return bookingsService.deleteBooking(bookingID);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingBookings(@RequestParam Long userID){
        return bookingsService.getUpcomingBookings(userID);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getBookingHistory(@RequestParam Long userID){
        return bookingsService.getBookingHistory(userID);
    }

}
