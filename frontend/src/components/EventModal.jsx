import React, { useState } from 'react';
import { FaCalendar, FaMapMarkerAlt, FaEuroSign } from 'react-icons/fa';
import { formatPrice } from '../utils/format';

const EventModal = ({ evento, onClose, onAddToCart }) => {
    const [qty, setQty] = useState(1);

    if (!evento) return null;

    return (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4 backdrop-blur-sm">
            <div className="bg-white w-full max-w-4xl rounded-2xl overflow-hidden shadow-2xl flex flex-col md:flex-row animate-fadeIn">

                {/* Imagen */}
                <div className="md:w-1/2 h-64 md:h-auto bg-gray-900 relative">
                    <img
                        src={evento.imageUrl || 'https://via.placeholder.com/500'}
                        className="w-full h-full object-cover opacity-90"
                        alt={evento.titulo}
                    />
                    <div className="absolute top-4 right-4 bg-yellow-500 text-black font-bold px-3 py-1 rounded-full shadow">
                        {formatPrice(evento.precio)}
                    </div>
                </div>

                {/* Info */}
                <div className="md:w-1/2 p-8 flex flex-col">
                    <button onClick={onClose} className="self-end text-gray-400 hover:text-gray-600 text-2xl">&times;</button>

                    <h2 className="text-3xl font-bold text-gray-800 mb-2">{evento.titulo}</h2>

                    <div className="flex gap-4 text-gray-600 mb-6 text-sm">
                        <span className="flex items-center gap-1"><FaCalendar/> {new Date(evento.fechaEvento).toLocaleDateString()}</span>
                        <span className="flex items-center gap-1"><FaMapMarkerAlt/> {evento.recinto?.ciudad}</span>
                    </div>

                    <p className="text-gray-600 flex-grow mb-6 leading-relaxed">
                        {evento.descripcion || "Disfruta de uno de los mejores eventos de la temporada."}
                    </p>

                    <div className="border-t pt-6 mt-auto">
                        <div className="flex gap-3">
                            <input
                                type="number" min="1" max="10" value={qty}
                                onChange={e => setQty(e.target.value)}
                                className="w-20 border-2 border-gray-300 rounded-lg text-center text-lg focus:border-yellow-500 outline-none"
                            />
                            <button
                                onClick={() => onAddToCart(evento, qty)}
                                className="flex-grow bg-gray-900 text-white font-bold py-3 rounded-lg hover:bg-gray-800 transition shadow-lg transform active:scale-95"
                            >
                                Añadir al Carrito
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default EventModal;