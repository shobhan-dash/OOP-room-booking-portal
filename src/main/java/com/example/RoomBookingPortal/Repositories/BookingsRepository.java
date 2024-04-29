package com.example.RoomBookingPortal.Repositories;

import com.example.RoomBookingPortal.Models.DatabaseTables.Booking;
import jakarta.persistence.TemporalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface BookingsRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.room.roomID = :roomID " +
            "AND b.dateOfBooking = :dateOfBooking " +
            "AND ((b.timeFrom <= :timeTo AND b.timeTo >= :timeFrom) " +
            "OR (b.timeFrom >= :timeFrom AND b.timeFrom <= :timeTo))")
    List<Booking> findConflictingBookings(
            @Param("roomID") Long roomID,
            @Param("dateOfBooking") Date dateOfBooking,
            @Param("timeFrom") String timeFrom,
            @Param("timeTo") String timeTo
    );

    @Query("SELECT b FROM Booking b WHERE b.room.roomID = :roomID " +
            "AND b.dateOfBooking = :dateOfBooking " +
            "AND ((b.timeFrom < :timeTo AND b.timeTo > :timeFrom) " +
            "OR (b.timeFrom > :timeFrom AND b.timeFrom < :timeTo)) " +
            "AND b.bookingID != :currentBookingID")
    List<Booking> findConflictingBookingsExcludeCurrent(
            @Param("roomID") Long roomID,
            @Param("dateOfBooking") Date dateOfBooking,
            @Param("timeFrom") String timeFrom,
            @Param("timeTo") String timeTo,
            @Param("currentBookingID") Long currentBookingID
    );


    @Query("SELECT b FROM Booking b WHERE b.user.userID = :userID " +
            "AND (b.dateOfBooking < CURRENT_DATE OR " + // Past bookings
            "(b.dateOfBooking = CURRENT_DATE AND CONCAT(b.dateOfBooking, ' ', b.timeTo) <= CONCAT(CURRENT_DATE, ' ', CURRENT_TIME)) OR " + // Finished today
            "(b.dateOfBooking = CURRENT_DATE AND CONCAT(b.dateOfBooking, ' ', b.timeFrom) <= CONCAT(CURRENT_DATE, ' ', CURRENT_TIME))) " + // Ongoing today
            "ORDER BY b.dateOfBooking DESC")
    List<Booking> findBookingHistory(@Param("userID") Long userID);



    @Query("SELECT b FROM Booking b WHERE b.user.userID = :userID " +
            "AND b.dateOfBooking >= CURRENT_DATE " +  // Check for upcoming bookings
            "AND CONCAT(b.dateOfBooking, ' ', b.timeFrom) >= CONCAT(CURRENT_DATE, ' ', CURRENT_TIME)")
    List<Booking> findUpcomingBookings(@Param("userID") Long userID);


}
