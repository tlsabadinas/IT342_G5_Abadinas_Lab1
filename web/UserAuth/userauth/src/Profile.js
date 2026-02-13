import React, { useEffect, useState } from 'react';
import { Container, Box, Typography, Card, CardContent, Avatar, Button, CircularProgress } from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

export default function Profile() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const { logout } = useAuth();

  useEffect(() => {
    const fetchProfile = async () => {
      const token = localStorage.getItem('token');

      if (!token) {
        navigate('/login');
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/api/users/me', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (response.ok) {
          const data = await response.json();
          setUser(data);
        } else {
          
          handleLogout();
        }
      } catch (error) {
        console.error("Error fetching profile:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    if (logout) logout();
    navigate('/login');
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container component="main" maxWidth="sm">
      <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Card sx={{ minWidth: 275, textAlign: 'center', p: 3, boxShadow: 3 }}>
          <Box display="flex" justifyContent="center" mb={2}>
            <Avatar sx={{ width: 80, height: 80, bgcolor: 'primary.main' }}>
              <PersonIcon sx={{ fontSize: 50 }} />
            </Avatar>
          </Box>
          <CardContent>
            <Typography variant="h5" component="div" gutterBottom>
              User Profile
            </Typography>
            
            <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
              <strong>Username:</strong> {user?.username}
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
              <strong>Email:</strong> {user?.email}
            </Typography>

            <Button 
              variant="contained" 
              color="error" 
              sx={{ mt: 4 }} 
              onClick={handleLogout}
            >
              Logout
            </Button>
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
}