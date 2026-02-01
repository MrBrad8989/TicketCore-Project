import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FaTicketAlt, FaShoppingCart, FaUser, FaSignOutAlt } from 'react-icons/fa';

const Navbar = ({ cartCount, onOpenCart, onOpenLogin }) => {
    const { user, logout } = useAuth();

    return (
        <nav className="bg-gray-900 text-white shadow-lg sticky top-0 z-50">
            <div className="container mx-auto px-4 py-3 flex justify-between items-center">
                {/* Logo */}
                <Link to="/" className="text-2xl font-bold flex items-center gap-2 hover:text-yellow-400 transition">
                    <FaTicketAlt /> TicketCore
                </Link>

                {/* Menú Central */}
                <div className="hidden md:flex gap-6">
                    <Link to="/" className="hover:text-yellow-400 font-medium">Inicio</Link>
                    <Link to="/buscar" className="hover:text-yellow-400 font-medium">Buscador</Link>
                    {user?.rol === 'EMPRESA' && (
                        <>
                            <Link to="/crear-evento" className="hover:text-yellow-400 font-medium">Crear evento</Link>
                            <Link to="/mis-eventos" className="hover:text-yellow-400 font-medium">Mis eventos</Link>
                        </>
                    )}
                </div>

                {/* Zona Usuario / Carrito */}
                <div className="flex items-center gap-4">
                    {user && (
                        <button onClick={onOpenCart} className="relative btn-icon hover:text-yellow-400">
                            <FaShoppingCart size={22} />
                            {cartCount > 0 && (
                                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                  {cartCount}
                </span>
                            )}
                        </button>
                    )}

                    {user ? (
                        <div className="flex items-center gap-3 border-l border-gray-700 pl-4">
                            <span className="text-sm font-semibold">{user.nombre}</span>
                            <button onClick={logout} className="text-red-400 hover:text-red-300" title="Salir">
                                <FaSignOutAlt />
                            </button>
                        </div>
                    ) : (
                        <button onClick={onOpenLogin} className="bg-yellow-500 text-gray-900 px-4 py-1.5 rounded-full font-bold hover:bg-yellow-400 transition">
                            Acceder
                        </button>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;