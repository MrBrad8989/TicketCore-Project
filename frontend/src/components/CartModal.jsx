import React, { useEffect, useState } from 'react';
import { FaShoppingCart, FaTrash, FaCreditCard, FaTimes } from 'react-icons/fa';
import { cartService } from '../services/api';
import Swal from 'sweetalert2';

const CartModal = ({ user, onClose, refreshCart }) => {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [total, setTotal] = useState(0);
    const [processing, setProcessing] = useState(false);

    // Cargar carrito al abrir el modal
    useEffect(() => {
        fetchCart();
    }, []);

    const fetchCart = async () => {
        try {
            const { data } = await cartService.getCart(user.id);
            const lineas = data.lineas || [];
            setItems(lineas);

            // Calcular total
            const t = lineas.reduce((acc, item) => acc + (item.cantidad * item.evento.precio), 0);
            setTotal(t);
        } catch (error) {
            console.error("Error cargando carrito", error);
        } finally {
            setLoading(false);
        }
    };

    const handleCheckout = async () => {
        setProcessing(true);
        try {
            await cartService.checkout(user.id);

            Swal.fire({
                icon: 'success',
                title: '¡Compra realizada!',
                text: 'Te hemos enviado las entradas a tu correo.',
                confirmButtonColor: '#10B981' // Verde Tailwind
            });

            refreshCart(); // Actualizar contador en Navbar
            onClose(); // Cerrar modal
        } catch (error) {
            Swal.fire('Error', 'No se pudo procesar la compra. Revisa el stock.', 'error');
        } finally {
            setProcessing(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-end bg-black/50 backdrop-blur-sm animate-fadeIn">
            {/* Panel lateral deslizante */}
            <div className="bg-white w-full max-w-md h-full shadow-2xl flex flex-col animate-slideInRight">

                {/* Header */}
                <div className="bg-yellow-500 p-5 flex justify-between items-center text-white shadow-md">
                    <h2 className="text-xl font-bold flex items-center gap-2">
                        <FaShoppingCart /> Tu Carrito
                    </h2>
                    <button onClick={onClose} className="hover:text-gray-200 text-xl">
                        <FaTimes />
                    </button>
                </div>

                {/* Body (Lista de items) */}
                <div className="flex-1 overflow-y-auto p-5 space-y-4">
                    {loading ? (
                        <div className="text-center py-10 text-gray-500">Cargando...</div>
                    ) : items.length === 0 ? (
                        <div className="text-center py-20 flex flex-col items-center text-gray-400">
                            <FaShoppingCart size={50} className="mb-4 opacity-20" />
                            <p>Tu carrito está vacío 😢</p>
                            <button onClick={onClose} className="mt-4 text-indigo-600 font-bold hover:underline">
                                ¡Busca eventos!
                            </button>
                        </div>
                    ) : (
                        items.map((item) => (
                            <div key={item.id} className="flex gap-4 p-3 bg-gray-50 rounded-lg border border-gray-100 shadow-sm">
                                <img
                                    src={item.evento.imageUrl || 'https://via.placeholder.com/80'}
                                    className="w-16 h-16 object-cover rounded-md"
                                    alt="Miniatura"
                                />
                                <div className="flex-grow">
                                    <h4 className="font-bold text-gray-800 text-sm line-clamp-1">{item.evento.titulo}</h4>
                                    <div className="text-xs text-gray-500 mt-1">
                                        {item.cantidad} x {item.evento.precio} €
                                    </div>
                                </div>
                                <div className="flex flex-col items-end justify-between">
                  <span className="font-bold text-indigo-600">
                    {(item.cantidad * item.evento.precio).toFixed(2)} €
                  </span>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {/* Footer (Total y Botón) */}
                {items.length > 0 && (
                    <div className="p-5 bg-gray-50 border-t border-gray-200">
                        <div className="flex justify-between items-center mb-4 text-lg">
                            <span className="font-semibold text-gray-600">Total:</span>
                            <span className="font-bold text-2xl text-indigo-900">{total.toFixed(2)} €</span>
                        </div>

                        <button
                            onClick={handleCheckout}
                            disabled={processing}
                            className="w-full bg-green-600 text-white font-bold py-3 rounded-xl hover:bg-green-700 transition flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed shadow-lg"
                        >
                            {processing ? (
                                <span>Procesando...</span>
                            ) : (
                                <>
                                    <FaCreditCard /> Pagar Ahora
                                </>
                            )}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default CartModal;