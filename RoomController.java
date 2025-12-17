package com.funo.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/auto-join")
    public ResponseEntity<AutoJoinResponse> autoJoin() {
        Room room = roomService.autoJoinRoom();
        AutoJoinResponse response = new AutoJoinResponse(room.getId(), room.getName());
        return ResponseEntity.ok(response);
    }

    // DTO for the response
    static class AutoJoinResponse {
        private Long roomId;
        private String roomName;

        public AutoJoinResponse(Long roomId, String roomName) {
            this.roomId = roomId;
            this.roomName = roomName;
        }

        public Long getRoomId() {
            return roomId;
        }

        public void setRoomId(Long roomId) {
            this.roomId = roomId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }
    }
}