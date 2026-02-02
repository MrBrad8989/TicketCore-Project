import React, { useState, useEffect, useCallback } from 'react';
import { Routes, Route, useLocation } from 'react-router-dom';
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
import MyEvents from './pages/MyEvents';
import CompraDetail from './pages/CompraDetail';

// Wrapper interno para poder usar useAuth
const MainLayout = () => {
    const { user } = useAuth();
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [isCartOpen, setIsCartOpen] = useState(false);
    const [isLoginOpen, setIsLoginOpen] = useState(false);
    const [cartCount, setCartCount] = useState(0);

    // Actualizar contador del carrito: fetchCartCount definido antes como useCallback
    const fetchCartCount = useCallback(async () => {
        try {
            if (!user) return;
            const { data } = await cartService.getCart(user.id);
            const count = data.lineas?.reduce((acc, l) => acc + l.cantidad, 0) || 0;
            setCartCount(count);
        } catch (e) {
            console.warn('fetchCartCount error', e);
        }
    }, [user]);

    useEffect(() => {
        if (user) fetchCartCount();
    }, [user, isCartOpen, fetchCartCount]);

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
            console.warn('addToCart error', e);
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
                <Route path="/mis-eventos" element={<MyEvents />} />
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

            {isCartOpen && <CartModal onClose={() => setIsCartOpen(false)} user={user} refreshCart={fetchCartCount}/>}            {isLoginOpen && <LoginModal onClose={() => setIsLoginOpen(false)} />}
        </div>
    );
};

// Componente que envuelve MainLayout para controlar título y favicon según ruta
function AppWrapper() {
    const location = useLocation();

    useEffect(() => {
        // Determinar título según pathname y parámetros
        const { pathname, search } = location;
        const params = new URLSearchParams(search);
        let title = 'TicketCore';

        if (pathname === '/' || pathname === '') {
            title = 'TicketCore - Inicio';
        } else if (pathname.startsWith('/buscar')) {
            const kw = params.get('keyword');
            const fecha = params.get('fecha');
            if (kw) title = `TicketCore - Buscar: ${kw}`;
            else if (fecha) title = `TicketCore - Buscar (${fecha})`;
            else title = 'TicketCore - Buscar';
        } else if (pathname.startsWith('/crear-evento')) {
            title = 'TicketCore - Crear evento';
        } else if (pathname.startsWith('/mis-eventos')) {
            title = 'TicketCore - Mis eventos';
        } else if (pathname.startsWith('/compras')) {
            title = 'TicketCore - Compra';
        }

        document.title = title;

        // Actualizar favicon (usa favicon.svg del build/public)
        try {
            const link = document.querySelector("link[rel*='icon']");
            if (link) {
                // Si quieres cambiar el icono según ruta, puedes hacer lógica aquí.
                link.href = '/favicon.svg';
            }
        } catch (e) {
            console.warn('favicon update error', e);
        }
    }, [location]);

    return <MainLayout />;
}

export default function App() {
    return (
        <AuthProvider>
            <AppWrapper />
        </AuthProvider>
    );
}