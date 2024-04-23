package com.example.RoomBookingPortal.DatabaseTables;

import jakarta.persistence.*;

@Entity
@Table(name = "RoomsTable")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "roomID")
    private Long roomID;

    private String roomName;
    private int roomCapacity;

    // Default constructor (required by JPA)
    public Room() {}

    // Constructor with parameters
    public Room(Long roomID, String roomName, int roomCapacity) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomCapacity = roomCapacity;
    }

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
}
