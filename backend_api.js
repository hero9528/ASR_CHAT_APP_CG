const express = require('express');
const http = require('http');
const WebSocket = require('ws');
const { v4: uuidv4 } = require('uuid');

const app = express();
app.use(express.json());

const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

let rooms = {};
const MAX_USERS_PER_ROOM = 30;

// Find or create a room for the user
function findOrCreateRoom() {
    let roomId = Object.keys(rooms).find(id => rooms[id].users.size < MAX_USERS_PER_ROOM);
    if (!roomId) {
        roomId = uuidv4();
        rooms[roomId] = { users: new Set(), messages: [] };
    }
    return roomId;
}

// API to join a random room
app.post('/api/join-random-room', (req, res) => {
    const userId = req.body.userId; // Assume userId is sent from the client
    const roomId = findOrCreateRoom();
    res.json({ roomId });
});

wss.on('connection', (ws, req) => {
    const urlParams = new URLSearchParams(req.url.split('?')[1]);
    const roomId = urlParams.get('roomId');
    const userId = urlParams.get('userId');

    if (!rooms[roomId]) {
        ws.close();
        return;
    }

    rooms[roomId].users.add(ws);
    console.log(`User ${userId} joined room ${roomId}`);

    // Send existing messages to the new user
    if (rooms[roomId].messages.length > 0) {
        ws.send(JSON.stringify({ type: 'history', messages: rooms[roomId].messages }));
    }


    ws.on('message', (message) => {
        const parsedMessage = JSON.parse(message);

        // Add message to room history
        rooms[roomId].messages.push(parsedMessage);
        if (rooms[roomId].messages.length > 100) { // Keep last 100 messages
            rooms[roomId].messages.shift();
        }

        // Broadcast message to all users in the room
        rooms[roomId].users.forEach(client => {
            if (client !== ws && client.readyState === WebSocket.OPEN) {
                client.send(JSON.stringify({ type: 'message', message: parsedMessage }));
            }
        });
    });

    ws.on('close', () => {
        rooms[roomId].users.delete(ws);
        console.log(`User ${userId} left room ${roomId}`);
        if (rooms[roomId].users.size === 0) {
            delete rooms[roomId];
            console.log(`Room ${roomId} is empty and has been closed.`);
        }
    });
});

server.listen(8080, () => {
    console.log('Server started on port 8080');
});