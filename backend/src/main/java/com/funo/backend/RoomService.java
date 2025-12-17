package com.funo.backend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public Room autoJoinRoom() {
        Room room = roomRepository.findRandomPublicRoom();
        if (room == null) {
            // Create a default public room if none exists
            room = new Room();
            room.setName("General");
            room.setPublic(true);
            roomRepository.save(room);
        }
        return room;
    }

    public List<Room> getPublicRooms() {
        return roomRepository.findByIsPublic(true);
    }
}