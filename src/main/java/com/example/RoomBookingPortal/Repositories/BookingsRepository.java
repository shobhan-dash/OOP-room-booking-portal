package com.example.RoomBookingPortal.Repositories;

import com.example.RoomBookingPortal.DatabaseTables.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BookingsRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.room.roomID = :roomID " +
            "AND b.dateOfBooking >= CURRENT_DATE " +  // Check for upcoming bookings
            "AND CONCAT(b.dateOfBooking, ' ', b.timeFrom) >= CONCAT(CURRENT_DATE, ' ', CURRENT_TIME)")
    List<Booking> findUpcomingBookings(@Param("roomID") Long roomID);

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

    @Query("SELECT b FROM Booking b WHERE b.room.roomID = :roomID " +
            "AND b.dateOfBooking < CURRENT_DATE " +  // Check for past bookings
            "ORDER BY b.dateOfBooking DESC")         // Sort by booking date in descending order
    List<Booking> findBookingHistory(@Param("roomID") Long roomID);
}
