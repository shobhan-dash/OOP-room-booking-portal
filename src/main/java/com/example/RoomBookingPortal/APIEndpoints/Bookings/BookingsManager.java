package com.example.RoomBookingPortal.APIEndpoints.Bookings;

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
    public ResponseEntity<?> deleteBooking(@RequestBody BookingDTO bookingDTO) {
        return bookingsService.deleteBooking(bookingDTO.getBookingID());
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
