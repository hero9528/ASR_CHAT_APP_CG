const express = require('express');
const router = express.Router();

// In-memory store for rooms and users
const rooms = {};
const users = [];
const MAX_USERS_PER_ROOM = 30;

router.post('/signup', (req, res) => {
    const { username, email, password } = req.body;

    if (!username || !email || !password) {
        return res.status(400).json({ message: 'Username, email, and password are required' });
    }

    // Check if user already exists
    if (users.find(user => user.email === email)) {
        return res.status(400).json({ message: 'User with this email already exists' });
    }

    const newUser = { id: users.length + 1, username, email, password }; // In a real app, hash the password
    users.push(newUser);

    // In a real app, you would generate a JWT token here
    res.json({ accessToken: 'dummy-token' });
});

router.post('/login', (req, res) => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ message: 'Email and password are required' });
    }

    const user = users.find(u => u.email === email && u.password === password);

    if (!user) {
        return res.status(401).json({ message: 'Invalid credentials' });
    }

    // In a real app, you would generate a JWT token here
    res.json({ accessToken: 'dummy-token' });
});

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

const app = express();
const PORT = process.env.PORT || 10000;

app.use(express.json());
app.use('/', router);

app.listen(PORT, () => {
    console.log(`Server is listening on port ${PORT}`);
});

module.exports = app;