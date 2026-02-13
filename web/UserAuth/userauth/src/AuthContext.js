import React, { createContext, useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const login = (email) => {
    const fakeUser = { email, name: 'Test User' };
    setUser(fakeUser);
    localStorage.setItem('user', JSON.stringify(fakeUser));
    navigate('/profile');
  };

  const register = (email) => {
    const fakeUser = { email, name: 'New User' };
    setUser(fakeUser);
    localStorage.setItem('user', JSON.stringify(fakeUser));
    navigate('/profile');
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
    navigate('/login');
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);