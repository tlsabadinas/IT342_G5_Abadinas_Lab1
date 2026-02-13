import React, { useState } from 'react';
import { Container, Box, Typography, TextField, Button, Avatar, Link } from '@mui/material';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import { useAuth } from './AuthContext';
import { Link as RouterLink, useNavigate } from 'react-router-dom';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    const user = {
      username: username,
      password: password
    };

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
      });

      if (response.ok) {
        const data = await response.json();
        
        if (data.token) {
            localStorage.setItem('token', data.token);
            console.log("Token stored:", data.token);
        }

        login(data); 

        alert("Login successful!");
        
        navigate('/profile'); 

      } else {
        const errorText = await response.text();
        alert("Login failed: " + errorText);
      }

    } catch (error) {
      console.error("Error:", error);
      alert("Network error");
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
          <LockOutlinedIcon />
        </Avatar>
        <Typography component="h1" variant="h5">Sign in</Typography>
        <Box component="form" onSubmit={handleLogin} sx={{ mt: 1 }}>
          <TextField
            margin="normal" required fullWidth label="Username"
            autoFocus value={username} onChange={(e) => setUsername(e.target.value)}
          />
          <TextField
            margin="normal" required fullWidth label="Password" type="password"
            value={password} onChange={(e) => setPassword(e.target.value)}
          />
          <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
            Sign In
          </Button>
          <Link component={RouterLink} to="/register" variant="body2">
            {"Don't have an account? Sign Up"}
          </Link>
        </Box>
      </Box>
    </Container>
  );
}