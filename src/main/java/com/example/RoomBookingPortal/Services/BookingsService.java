package com.example.RoomBookingPortal.Services;

import com.example.RoomBookingPortal.Models.DTOs.BookingDTO;
import com.example.RoomBookingPortal.Models.DTOs.UserBookingRecordsDTO;
import com.example.RoomBookingPortal.Models.DatabaseTables.Booking;
import com.example.RoomBookingPortal.Models.DatabaseTables.Room;
import com.example.RoomBookingPortal.Models.DatabaseTables.User;
import com.example.RoomBookingPortal.Repositories.BookingsRepository;
import com.example.RoomBookingPortal.Repositories.RoomsRepository;
import com.example.RoomBookingPortal.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingsService {

    private final BookingsRepository bookingsRepository;
    private final RoomsRepository roomsRepository;
    private final UserRepository usersRepository;

    @Autowired
    public BookingsService(BookingsRepository bookingsRepository, RoomsRepository roomsRepository, UserRepository usersRepository) {
        this.bookingsRepository = bookingsRepository;
        this.roomsRepository = roomsRepository;
        this.usersRepository = usersRepository;
    }

    public ResponseEntity<?> createBooking(BookingDTO bookingDTO) {
        // Check if the room exists
        Optional<Room> optionalRoom = roomsRepository.findById(bookingDTO.getRoomID());
        if (optionalRoom.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room does not exist");
        }

        Room room = optionalRoom.get();

        // Check if the user exists
        Optional<User> optionalUser = usersRepository.findById(bookingDTO.getUserID());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }

        User user = optionalUser.get();

        // Validate date format
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            String formattedDate = dateFormat.format(bookingDTO.getDateOfBooking());
            if (!formattedDate.equals(bookingDTO.getDateOfBooking().toString())) {
                // The formatted date does not match the input date string exactly (Eg: 2024-04-35)
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid date/time");
        }

        // Validate time format
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            timeFormat.setLenient(false);
            timeFormat.parse(bookingDTO.getTimeFrom());
            timeFormat.parse(bookingDTO.getTimeTo());
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid date/time");
        }

        // Check if timeFrom is before timeTo
        if (bookingDTO.getTimeFrom().compareTo(bookingDTO.getTimeTo()) >= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid date/time");
        }

        // Check room availability
        List<Booking> conflictingBookings = bookingsRepository.findConflictingBookings(
                bookingDTO.getRoomID(),
                bookingDTO.getDateOfBooking(),
                bookingDTO.getTimeFrom(),
                bookingDTO.getTimeTo()
        );

        if (!conflictingBookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room unavailable");
        }

        // Create and save the booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setDateOfBooking(bookingDTO.getDateOfBooking());
        booking.setTimeFrom(bookingDTO.getTimeFrom());
        booking.setTimeTo(bookingDTO.getTimeTo());
        booking.setPurpose(bookingDTO.getPurpose());
        bookingsRepository.save(booking);

        return ResponseEntity.status(HttpStatus.OK).body("Booking created successfully");
    }

    public ResponseEntity<?> editBooking(BookingDTO bookingDTO) {
        // Check if the booking exists
        Booking booking = bookingsRepository.findById(bookingDTO.getBookingID()).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking does not exist");
        }

        // Check if the user exists
        Optional<User> optionalUser = usersRepository.findById(bookingDTO.getUserID());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }

        User user = optionalUser.get();

        // Check if the room exists
        Optional<Room> optionalRoom = roomsRepository.findById(bookingDTO.getRoomID());
        if (optionalRoom.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room does not exist");
        }

        Room room = optionalRoom.get();

        // Validate date format
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            String formattedDate = dateFormat.format(bookingDTO.getDateOfBooking());
            if (!formattedDate.equals(bookingDTO.getDateOfBooking().toString())) {
                // The formatted date does not match the input date string exactly (Eg: 2024-04-35)
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid date/time");
        }


        // Validate time format
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            timeFormat.setLenient(false);
            timeFormat.parse(bookingDTO.getTimeFrom());
            timeFormat.parse(bookingDTO.getTimeTo());
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid date/time");
        }

        // Check if timeFrom is before timeTo
        if (bookingDTO.getTimeFrom().compareTo(bookingDTO.getTimeTo()) >= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid date/time");
        }

        // Check room availability
        List<Booking> conflictingBookings = bookingsRepository.findConflictingBookingsExcludeCurrent(
                bookingDTO.getRoomID(),
                bookingDTO.getDateOfBooking(),
                bookingDTO.getTimeFrom(),
                bookingDTO.getTimeTo(),
                booking.getBookingID()
        );

        if (!conflictingBookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room unavailable");
        }

        // Update booking details
        booking.setUser(user);
        booking.setRoom(room);
        booking.setDateOfBooking(bookingDTO.getDateOfBooking());
        booking.setTimeFrom(bookingDTO.getTimeFrom());
        booking.setTimeTo(bookingDTO.getTimeTo());
        booking.setPurpose(bookingDTO.getPurpose());
        bookingsRepository.save(booking);

        return ResponseEntity.status(HttpStatus.OK).body("Booking modified successfully");
    }


    public ResponseEntity<?> deleteBooking(Long bookingID) {
        try {
            if (bookingsRepository.existsById(bookingID)) {
                bookingsRepository.deleteById(bookingID);
                return ResponseEntity.status(HttpStatus.OK).body("Booking deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking does not exist");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking does not exist");
        }
    }


    public ResponseEntity<?> getUpcomingBookings(Long userID) {
        Optional<User> optionalUser = usersRepository.findById(userID);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }

        List<Booking> upcomingBookings = bookingsRepository.findUpcomingBookings(userID);

        List<UserBookingRecordsDTO> bookingDTOs = new ArrayList<>();
        for (Booking booking : upcomingBookings) {
            UserBookingRecordsDTO bookingDTO = new UserBookingRecordsDTO();

            // Fetch the Room entity or RoomDTO and set roomName
            Optional<Room> optionalRoom = roomsRepository.findById(booking.getRoom().getRoomID());
            if (optionalRoom.isPresent()) {
                bookingDTO.setRoomName(optionalRoom.get().getRoomName());
                bookingDTO.setRoomID(optionalRoom.get().getRoomID());
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room does not exist");
            }

            bookingDTO.setBookingID(booking.getBookingID());
            bookingDTO.setDateOfBooking(booking.getDateOfBooking());
            bookingDTO.setTimeFrom(booking.getTimeFrom());
            bookingDTO.setTimeTo(booking.getTimeTo());
            bookingDTO.setPurpose(booking.getPurpose());
            bookingDTOs.add(bookingDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(bookingDTOs);
    }



    public ResponseEntity<?> getBookingHistory(Long userID) {
        Optional<User> optionalUser = usersRepository.findById(userID);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }

        List<Booking> bookingHistory = bookingsRepository.findBookingHistory(userID);

        List<UserBookingRecordsDTO> bookingDTOs = new ArrayList<>();
        for (Booking booking : bookingHistory) {
            UserBookingRecordsDTO bookingDTO = new UserBookingRecordsDTO();

            // Fetch the Room entity or RoomDTO and set roomName
            Optional<Room> optionalRoom = roomsRepository.findById(booking.getRoom().getRoomID());
            if (optionalRoom.isPresent()) {
                bookingDTO.setRoomName(optionalRoom.get().getRoomName());
                bookingDTO.setRoomID(optionalRoom.get().getRoomID());
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room does not exist");
            }

            bookingDTO.setBookingID(booking.getBookingID());
            bookingDTO.setDateOfBooking(booking.getDateOfBooking());
            bookingDTO.setTimeFrom(booking.getTimeFrom());
            bookingDTO.setTimeTo(booking.getTimeTo());
            bookingDTO.setPurpose(booking.getPurpose());
            bookingDTOs.add(bookingDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(bookingDTOs);
    }

}
