import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        // Al cargar la app, miramos si hay usuario guardado
        const storedUser = localStorage.getItem('ticketUser');
        if (storedUser) {
            try {
                const parsed = JSON.parse(storedUser);
                setUser(parsed);
            } catch (e) {
                // Si el JSON está corrupto, limpiamos el almacenamiento y seguimos sin usuario
                console.warn('Invalid ticketUser in localStorage, clearing it', e);
                localStorage.removeItem('ticketUser');
            }
        }
    }, []);

    const login = (userData) => {
        setUser(userData);
        try {
            localStorage.setItem('ticketUser', JSON.stringify(userData));
        } catch (e) {
            console.warn('Could not persist user to localStorage', e);
        }
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