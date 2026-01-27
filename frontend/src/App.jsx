import React, { useState, useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { cartService } from './services/api';
import Swal from 'sweetalert2';

import Navbar from './components/Navbar';
import SearchPage from './pages/SearchPage';
import EventModal from './components/EventModal';
import CartModal from './components/CartModal';
import LoginModal from './components/LoginModal';

// Wrapper interno para poder usar useAuth
const MainLayout = () => {
    const { user } = useAuth();
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [isCartOpen, setIsCartOpen] = useState(false);
    const [isLoginOpen, setIsLoginOpen] = useState(false);
    const [cartCount, setCartCount] = useState(0);

    // Actualizar contador del carrito
    useEffect(() => {
        if (user) fetchCartCount();
    }, [user, isCartOpen]);

    const fetchCartCount = async () => {
        try {
            const { data } = await cartService.getCart(user.id);
            const count = data.lineas?.reduce((acc, l) => acc + l.cantidad, 0) || 0;
            setCartCount(count);
        } catch (e) {}
    };

    const handleAddToCart = async (evento, cantidad) => {
        if (!user) {
            setSelectedEvent(null);
            setIsLoginOpen(true);
            Swal.fire('¡Ups!', 'Inicia sesión para comprar entradas', 'info');
            return;
        }
        try {
            await cartService.add(user.id, evento.id, cantidad);
            Swal.fire({ icon: 'success', title: '¡Añadido!', timer: 1500, showConfirmButton: false });
            setSelectedEvent(null);
            fetchCartCount();
        } catch (e) {
            Swal.fire('Error', 'No se pudo añadir (¿Sold Out?)', 'error');
        }
    };

    return (
        <div className="font-sans text-gray-800">
            <Navbar
                cartCount={cartCount}
                onOpenCart={() => setIsCartOpen(true)}
                onOpenLogin={() => setIsLoginOpen(true)}
            />

            <Routes>
                <Route path="/" element={<div className="text-center py-20 bg-gray-900 text-white"><h1 className="text-5xl font-bold mb-4">Bienvenido a TicketCore</h1><p className="text-xl">Usa el buscador para empezar.</p></div>} />
                <Route path="/buscar" element={<SearchPage onSelectEvent={setSelectedEvent} />} />
            </Routes>

            {/* MODALES GLOBALES */}
            {selectedEvent && (
                <EventModal
                    evento={selectedEvent}
                    onClose={() => setSelectedEvent(null)}
                    onAddToCart={handleAddToCart}
                />
            )}

            {isCartOpen && <CartModal onClose={() => setIsCartOpen(false)} user={user} refreshCart={fetchCartCount}/>}
            {isLoginOpen && <LoginModal onClose={() => setIsLoginOpen(false)} />}
        </div>
    );
};

export default function App() {
    return (
        <AuthProvider>
            <MainLayout />
        </AuthProvider>
    );
}