package com.example.RoomBookingPortal.Models.DTOs;

import java.util.List;

public class RoomFiltersDTO {
    private Long roomID;
    private String roomName;
    private int roomCapacity;
    private List<BookingDTO> booked;

    // Getters and setters

    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(int roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public List<BookingDTO> getBooked() {
        return booked;
    }

    public void setBooked(List<BookingDTO> booked) {
        this.booked = booked;
    }

    // Additional method to return user object
    public UserDTO getUserFromBooking(BookingDTO booking) {
        UserDTO user = new UserDTO();
        user.setUserID(booking.getUserID());
        // Set other user properties as needed
        return user;
    }
}
