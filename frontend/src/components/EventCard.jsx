import React from 'react';
import { FaCalendarAlt, FaMapMarkerAlt, FaTrash, FaEdit } from 'react-icons/fa';

const EventCard = ({ evento, onClick, isAdmin, onDelete, onEdit, user }) => {
    // Formateo seguro de fecha y precio
    const fecha = evento.fechaEvento ? new Date(evento.fechaEvento).toLocaleDateString() : 'Fecha por definir';
    const precio = evento.precio ? `${evento.precio} €` : 'Gratis';

    // Manejador para borrar sin activar el click de la carta
    const handleDeleteClick = (e) => {
        e.stopPropagation(); // Evita abrir el modal al hacer click en borrar
        onDelete();
    };

    // Manejador para editar sin activar el click de la carta
    const handleEditClick = (e) => {
        e.stopPropagation(); // Evita abrir el modal al hacer click en editar
        if (onEdit) onEdit();
    };

    // Verificar si el usuario actual es creador del evento
    const isCreator = user && evento.creadorId && user.id === evento.creadorId;

    return (
        <div
            onClick={onClick}
            className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl hover:-translate-y-1 transition-all duration-300 cursor-pointer group relative flex flex-col h-full"
        >
            {/* Imagen */}
            <div className="relative h-48 overflow-hidden">
                <img
                    src={evento.imageUrl || 'https://via.placeholder.com/300?text=Sin+Imagen'}
                    alt={evento.titulo}
                    className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                />
                <span className="absolute top-2 right-2 bg-black/70 text-white text-xs font-bold px-3 py-1 rounded-full backdrop-blur-sm">
          {precio}
        </span>
            </div>

            {/* Contenido */}
            <div className="p-4 flex flex-col gap-2 flex-grow">
                <h3 className="text-lg font-bold text-gray-900 truncate" title={evento.titulo}>
                    {evento.titulo}
                </h3>

                <div className="flex items-center text-gray-500 text-sm">
                    <FaCalendarAlt className="mr-2 text-indigo-500" />
                    <span>{fecha}</span>
                </div>

                <div className="flex items-center text-gray-500 text-sm">
                    <FaMapMarkerAlt className="mr-2 text-red-500" />
                    <span>{evento.recinto?.ciudad || 'Ubicación pendiente'}</span>
                </div>

                {/* Botón de acción principal */}
                <div className="mt-auto pt-3">
                    <button className="w-full bg-transparent border-2 border-gray-800 text-gray-800 font-semibold py-1.5 rounded-lg hover:bg-gray-800 hover:text-white transition-colors">
                        Ver Entradas
                    </button>
                </div>
            </div>

            {/* Controles para Admin o Empresa Creadora */}
            {(isAdmin || isCreator) && (
                <div className="absolute top-2 left-2 flex gap-2">
                    {isCreator && onEdit && (
                        <button
                            onClick={handleEditClick}
                            className="bg-blue-600 text-white p-2 rounded-full shadow-lg hover:bg-blue-700 transition"
                            title="Editar evento"
                        >
                            <FaEdit size={14} />
                        </button>
                    )}
                    {(isAdmin || isCreator) && (
                        <button
                            onClick={handleDeleteClick}
                            className="bg-red-600 text-white p-2 rounded-full shadow-lg hover:bg-red-700 transition"
                            title="Borrar evento"
                        >
                            <FaTrash size={14} />
                        </button>
                    )}
                </div>
            )}
        </div>
    );
};

export default EventCard;

