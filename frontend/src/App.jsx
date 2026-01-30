import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { cartService } from './services/api';
import Swal from 'sweetalert2';

import Navbar from './components/Navbar';
import SearchPage from './pages/SearchPage';
import EventModal from './components/EventModal';
import CartModal from './components/CartModal';
import LoginModal from './components/LoginModal';
import Home from './pages/Home';
import CreateEvent from './pages/CreateEvent';
import CompraDetail from './pages/CompraDetail'; // Asegúrate de que la ruta de importación sea correcta

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
                <Route path="/" element={<Home />} />
                <Route path="/buscar" element={<SearchPage onSelectEvent={setSelectedEvent} />} />
                <Route path="/crear-evento" element={<CreateEvent />} />
                <Route path="/compras/:id" element={<CompraDetail />} />
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