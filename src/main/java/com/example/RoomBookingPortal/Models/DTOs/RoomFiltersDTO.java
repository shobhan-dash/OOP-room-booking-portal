package com.example.RoomBookingPortal.Models.DTOs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomFiltersDTO {
    private Long roomID;
    private String roomName;
    private Integer roomCapacity;
    private List<Booked> booked = new ArrayList<>();

    // Getters and setters for RoomFiltersDTO
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

    public Integer getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(Integer roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public List<Booked> getBooked() {
        return booked;
    }

    public void setBooked(List<Booked> booked) {
        this.booked = booked;
    }

    public void addBooked(Booked booked) {
        this.booked.add(booked);
    }

    // Inner class for booked details
    public static class Booked {
        private Long bookingID;
        private Date dateOfBooking;
        private String timeFrom;
        private String timeTo;
        private String purpose;
        private User user;

        // Getters and setters for Booked
        public Long getBookingID() {
            return bookingID;
        }

        public void setBookingID(Long bookingID) {
            this.bookingID = bookingID;
        }

        public Date getDateOfBooking() {
            return dateOfBooking;
        }

        public void setDateOfBooking(Date dateOfBooking) {
            this.dateOfBooking = dateOfBooking;
        }

        public String getTimeFrom() {
            return timeFrom;
        }

        public void setTimeFrom(String timeFrom) {
            this.timeFrom = timeFrom;
        }

        public String getTimeTo() {
            return timeTo;
        }

        public void setTimeTo(String timeTo) {
            this.timeTo = timeTo;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        // Inner class for user details
        public static class User {
            private Long userID;

            // Getters and setters for User
            public Long getUserID() {
                return userID;
            }

            public void setUserID(Long userID) {
                this.userID = userID;
            }
        }
    }
}
