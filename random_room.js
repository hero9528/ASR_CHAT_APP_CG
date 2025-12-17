const express = require('express');
const router = express.Router();

// In-memory store for rooms
const rooms = {};
const MAX_USERS_PER_ROOM = 30;

router.post('/join-random-room', (req, res) => {
    const { userId } = req.body;

    if (!userId) {
        return res.status(400).json({ message: 'userId is required' });
    }

    let availableRoomId = null;

    // Find a room that is not full
    for (const roomId in rooms) {
        if (rooms[roomId].length < MAX_USERS_PER_ROOM) {
            availableRoomId = roomId;
            break;
        }
    }

    // If no available room, create a new one
    if (!availableRoomId) {
        const newRoomId = `room_${Date.now()}`;
        rooms[newRoomId] = [];
        availableRoomId = newRoomId;
    }

    // Add user to the room
    rooms[availableRoomId].push(userId);

    res.json({ roomId: availableRoomId });
});

module.exports = router;