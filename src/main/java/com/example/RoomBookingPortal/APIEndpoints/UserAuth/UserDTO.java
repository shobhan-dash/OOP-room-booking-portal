package com.example.RoomBookingPortal.APIEndpoints.UserAuth;

public class UserDTO {
    private Long userID;
    private String email;
    private String name;

    public UserDTO() {
        this.userID = null;
        this.email = null;
        this.name = null;
    }

    public UserDTO(String message) {
        this.userID = null;
        this.email = null;
        this.name = null;
    }

    // Getters and setters
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
