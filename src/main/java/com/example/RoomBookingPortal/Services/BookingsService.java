package com.example.RoomBookingPortal.Services;

import com.example.RoomBookingPortal.Models.DTOs.BookingDTO;
import com.example.RoomBookingPortal.Models.DTOs.UserBookingRecordsDTO;
import com.example.RoomBookingPortal.Models.DatabaseTables.Booking;
import com.example.RoomBookingPortal.Models.DatabaseTables.Room;
import com.example.RoomBookingPortal.Models.DatabaseTables.User;
import com.example.RoomBookingPortal.Repositories.BookingsRepository;
import com.example.RoomBookingPortal.Repositories.RoomsRepository;
import com.example.RoomBookingPortal.Repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Room does not exist"));
        }

        Room room = optionalRoom.get();

        // Check if the user exists
        Optional<User> optionalUser = usersRepository.findById(bookingDTO.getUserID());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ObjectMapper().createObjectNode().put("Error", "User does not exist"));
        }

        User user = optionalUser.get();

        ObjectNode response = new ObjectMapper().createObjectNode();


        // Prevent Bookings in the past
        Instant instant = bookingDTO.getDateOfBooking().toInstant();
        LocalDateTime dateOfBooking = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime timeFrom = LocalTime.parse(bookingDTO.getTimeFrom(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime timeTo = LocalTime.parse(bookingDTO.getTimeTo(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalDateTime dateTimeFrom = LocalDateTime.of(dateOfBooking.toLocalDate(), timeFrom);
        LocalDateTime dateTimeTo = LocalDateTime.of(dateOfBooking.toLocalDate(), timeTo);

        if (dateTimeFrom.isBefore(currentDateTime) || dateTimeTo.isBefore(currentDateTime)) {
            response.put("Error", "Invalid date/time");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }


        // Validate time format
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            timeFormat.setLenient(false);
            timeFormat.parse(bookingDTO.getTimeFrom());
            timeFormat.parse(bookingDTO.getTimeTo());
        } catch (ParseException e) {
            response.put("Error", "Invalid date/time");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Check if timeFrom is before timeTo
        if (bookingDTO.getTimeFrom().compareTo(bookingDTO.getTimeTo()) >= 0) {
            response.put("Error", "Invalid date/time");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Check room availability
        List<Booking> conflictingBookings = bookingsRepository.findConflictingBookings(
                bookingDTO.getRoomID(),
                bookingDTO.getDateOfBooking(),
                bookingDTO.getTimeFrom(),
                bookingDTO.getTimeTo()
        );

        if (!conflictingBookings.isEmpty()) {
            response.put("Error", "Room unavailable");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

        // Check if the user exists
        Optional<User> optionalUser = usersRepository.findById(bookingDTO.getUserID());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ObjectMapper().createObjectNode().put("Error", "User does not exist"));
        }

        User user = optionalUser.get();

        // Check if the room exists
        Optional<Room> optionalRoom = roomsRepository.findById(bookingDTO.getRoomID());
        if (optionalRoom.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Room does not exist"));
        }

        // Check if the booking exists
        Booking booking = bookingsRepository.findById(bookingDTO.getBookingID()).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Booking does not exist"));
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Invalid date/time"));
        }


        // Validate time format
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            timeFormat.setLenient(false);
            timeFormat.parse(bookingDTO.getTimeFrom());
            timeFormat.parse(bookingDTO.getTimeTo());
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Invalid date/time"));        }

        // Check room availability
        List<Booking> conflictingBookings = bookingsRepository.findConflictingBookingsExcludeCurrent(
                bookingDTO.getRoomID(),
                bookingDTO.getDateOfBooking(),
                bookingDTO.getTimeFrom(),
                bookingDTO.getTimeTo(),
                booking.getBookingID()
        );

        if (!conflictingBookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Room unavailable"));        }

        // Check if timeFrom is before timeTo
        if (bookingDTO.getTimeFrom().compareTo(bookingDTO.getTimeTo()) >= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Invalid date/time"));        }

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
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ObjectMapper().createObjectNode().put("Error", "Booking does not exist"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ObjectMapper().createObjectNode().put("Error", "Booking does not exist"));
        }
    }


    public ResponseEntity<?> getUpcomingBookings(Long userID) {
        Optional<User> optionalUser = usersRepository.findById(userID);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ObjectMapper().createObjectNode().put("Error", "User does not exist"));
        }

        List<Booking> upcomingBookings = bookingsRepository.findUpcomingBookings(userID);

        List<UserBookingRecordsDTO> bookingDTOs = new ArrayList<>();
        for (Booking booking : upcomingBookings) {
            UserBookingRecordsDTO bookingDTO = new UserBookingRecordsDTO();

            // Fetch the Room entity or RoomDTO and set roomName
            Optional<Room> optionalRoom = roomsRepository.findById(booking.getUser().getUserID());
            if (optionalRoom.isPresent()) {
                bookingDTO.setRoomName(optionalRoom.get().getRoomName());
                bookingDTO.setRoomID(optionalRoom.get().getRoomID());
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ObjectMapper().createObjectNode().put("Error", "User does not exist"));
        }

        List<Booking> bookingHistory = bookingsRepository.findBookingHistory(userID);

        List<UserBookingRecordsDTO> bookingDTOs = new ArrayList<>();
        for (Booking booking : bookingHistory) {
            UserBookingRecordsDTO bookingDTO = new UserBookingRecordsDTO();

            // Fetch the Room entity or RoomDTO and set roomName
            Optional<Room> optionalRoom = roomsRepository.findById(booking.getUser().getUserID());
            if (optionalRoom.isPresent()) {
                bookingDTO.setRoomName(optionalRoom.get().getRoomName());
                bookingDTO.setRoomID(optionalRoom.get().getRoomID());
            }

            // No need to check for room. Was edited out by sir
//            else{
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room does not exist");
//            }

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
