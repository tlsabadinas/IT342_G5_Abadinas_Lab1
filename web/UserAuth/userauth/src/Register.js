import React, { useState } from 'react';
import { Container, Box, Typography, TextField, Button, Avatar, Link, Alert } from '@mui/material';
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import { Link as RouterLink, useNavigate } from 'react-router-dom';

export default function Register() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [status, setStatus] = useState({ type: '', msg: '' });

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus({ type: '', msg: '' });

    
    const userData = {
      username: username, 
      email: email,
      password: password
    };
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });

      if (response.ok) {
        setStatus({ type: 'success', msg: 'Registration successful! Redirecting...' });
        setTimeout(() => navigate('/login'), 2000);
      } else {
        const errorText = await response.text();
        setStatus({ type: 'error', msg: errorText });
      }

    } catch (error) {
      setStatus({ type: 'error', msg: 'Failed to connect to the server.' });
      console.error(error);
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Avatar sx={{ m: 1, bgcolor: 'primary.main' }}>
          <PersonAddAltIcon />
        </Avatar>
        <Typography component="h1" variant="h5">Sign up</Typography>

        {status.msg && <Alert severity={status.type} sx={{ mt: 2, width: '100%' }}>{status.msg}</Alert>}

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
          
          
          <TextField 
            margin="normal" 
            required 
            fullWidth 
            label="Username" 
            value={username} 
            onChange={(e) => setUsername(e.target.value)}
          />

          <TextField
            margin="normal" 
            required 
            fullWidth 
            label="Email Address"
            value={email} 
            onChange={(e) => setEmail(e.target.value)}
          />

          <TextField 
            margin="normal" 
            required 
            fullWidth 
            label="Password" 
            type="password" 
            value={password} 
            onChange={(e) => setPassword(e.target.value)} 
          />

          <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
            Sign Up
          </Button>
          
          <Link component={RouterLink} to="/login" variant="body2">
            {"Already have an account? Sign In"}
          </Link>
        </Box>
      </Box>
    </Container>
  );
}