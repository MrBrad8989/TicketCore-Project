import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        // Al cargar la app, miramos si hay usuario guardado
        const storedUser = localStorage.getItem('ticketUser');
        if (storedUser) setUser(JSON.parse(storedUser));
    }, []);

    const login = (userData) => {
        setUser(userData);
        localStorage.setItem('ticketUser', JSON.stringify(userData));
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('ticketUser');
        window.location.href = '/';
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, isAdmin: user?.rol === 'ADMIN' }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);