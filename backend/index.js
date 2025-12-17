const express = require('express');
const http = require('http');
const WebSocket = require('ws');
const randomRoomRouter = require('./random_room');
const authRouter = require('./auth');

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

app.use(express.json());
app.use('/api/random-room', randomRoomRouter);
app.use('/api/auth', authRouter);

wss.on('connection', (ws, req) => {
    const roomId = req.url.substring(1); // Extract room ID from URL
    ws.roomId = roomId;

    console.log(`Client connected to room: ${roomId}`);

    ws.on('message', (message) => {
        console.log(`Received message in room ${roomId}: ${message}`);
        // Broadcast the message to all clients in the same room
        wss.clients.forEach((client) => {
            if (client !== ws && client.readyState === WebSocket.OPEN && client.roomId === roomId) {
                client.send(message);
            }
        });
    });

    ws.on('close', () => {
        console.log(`Client disconnected from room: ${roomId}`);
    });
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`Server is listening on port ${PORT}`);
});