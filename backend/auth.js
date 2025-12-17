const express = require('express');
const router = express.Router();

// Temporary in-memory user store
const users = [];

router.post('/signup', (req, res) => {
    const { username, password } = req.body;

    if (!username || !password) {
        return res.status(400).json({ message: 'Username and password are required' });
    }

    // Check if user already exists
    if (users.find(user => user.username === username)) {
        return res.status(409).json({ message: 'User already exists' });
    }

    // Save the new user (in-memory)
    const newUser = { id: users.length + 1, username, password };
    users.push(newUser);

    console.log('New user signed up:', newUser);
    console.log('All users:', users);

    // Respond with a success message and a dummy token
    res.status(201).json({ 
        message: 'User created successfully',
        token: `dummy-token-for-${username}` 
    });
});

router.post('/login', (req, res) => {
    const { username, password } = req.body;

    if (!username || !password) {
        return res.status(400).json({ message: 'Username and password are required' });
    }

    const user = users.find(u => u.username === username && u.password === password);

    if (!user) {
        return res.status(401).json({ message: 'Invalid credentials' });
    }

    console.log('User logged in:', user);

    res.status(200).json({
        message: 'Login successful',
        token: `dummy-token-for-${username}`
    });
});

module.exports = router;