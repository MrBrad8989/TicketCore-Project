import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { eventService } from '../services/api';
import Swal from 'sweetalert2';
import { FaEdit, FaTrash, FaCalendarAlt, FaMapMarkerAlt, FaEuroSign } from 'react-icons/fa';
import { formatPrice } from '../utils/format';

const MyEvents = () => {
    const { user } = useAuth();
    const [eventos, setEventos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [generos, setGeneros] = useState([]);

    useEffect(() => {
        document.title = 'TicketCore - Mis Eventos';
        cargarGeneros();
        cargarEventos();
    }, []);

    const cargarGeneros = async () => {
        try {
            const { data } = await eventService.getGeneros();
            setGeneros(data || []);
        } catch (e) {
            console.error('Error cargando géneros', e);
        }
    };

    const cargarEventos = async () => {
        if (!user || !user.id) return;
        setLoading(true);
        try {
            const { data } = await eventService.getMyEvents(user);
            setEventos(data || []);
        } catch (error) {
            console.error('Error cargando eventos', error);
            Swal.fire('Error', 'No se pudieron cargar tus eventos', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = async (evento) => {
        const { value: formValues } = await Swal.fire({
            title: 'Editar Evento',
            html: `
                <div style="text-align: left; max-width: 500px; margin: auto;">
                    <label style="display: block; margin-top: 10px; font-weight: bold;">Título</label>
                    <input id="titulo" class="swal2-input" style="width: 100%;" value="${evento.titulo || ''}">
                    
                    <label style="display: block; margin-top: 10px; font-weight: bold;">Ciudad</label>
                    <select id="ciudad" class="swal2-input" style="width: 100%;">
                        <option value="">-- Seleccionar --</option>
                        <option value="Madrid" ${evento.recinto?.ciudad === 'Madrid' ? 'selected' : ''}>Madrid</option>
                        <option value="Barcelona" ${evento.recinto?.ciudad === 'Barcelona' ? 'selected' : ''}>Barcelona</option>
                        <option value="Valencia" ${evento.recinto?.ciudad === 'Valencia' ? 'selected' : ''}>Valencia</option>
                        <option value="Bilbao" ${evento.recinto?.ciudad === 'Bilbao' ? 'selected' : ''}>Bilbao</option>
                    </select>
                    
                    <label style="display: block; margin-top: 10px; font-weight: bold;">Fecha</label>
                    <input id="fecha" type="datetime-local" class="swal2-input" style="width: 100%;" value="${evento.fechaEvento ? evento.fechaEvento.slice(0, 16) : ''}">
                    
                    <label style="display: block; margin-top: 10px; font-weight: bold;">Precio (€)</label>
                    <input id="precio" type="number" step="0.01" class="swal2-input" style="width: 100%;" value="${evento.precio || ''}">
                    
                    <label style="display: block; margin-top: 10px; font-weight: bold;">Imagen URL</label>
                    <input id="imageUrl" class="swal2-input" style="width: 100%;" value="${evento.imageUrl || ''}">
                    
                    <label style="display: block; margin-top: 10px; font-weight: bold;">Máximo de entradas</label>
                    <input id="maxEntradas" type="number" class="swal2-input" style="width: 100%;" value="${evento.maxEntradas || evento.recinto?.aforoMaximo || ''}">
                    
                    <label style="display: block; margin-top: 10px; font-weight: bold;">Género</label>
                    <select id="genero" class="swal2-input" style="width: 100%;">
                        <option value="">-- Seleccionar --</option>
                        ${generos.map(g => `<option value="${g}" ${evento.artistas?.[0]?.genero === g ? 'selected' : ''}>${g}</option>`).join('')}
                    </select>
                </div>
            `,
            width: 600,
            focusConfirm: false,
            showCancelButton: true,
            confirmButtonText: 'Guardar',
            cancelButtonText: 'Cancelar',
            preConfirm: () => {
                const titulo = document.getElementById('titulo').value;
                const ciudad = document.getElementById('ciudad').value;
                const fecha = document.getElementById('fecha').value;
                const precio = document.getElementById('precio').value;
                const imageUrl = document.getElementById('imageUrl').value;
                const maxEntradas = document.getElementById('maxEntradas').value;

                if (!titulo || !ciudad || !fecha || !precio) {
                    Swal.showValidationMessage('Por favor completa título, ciudad, fecha y precio');
                    return null;
                }

                return {
                    titulo,
                    precio: parseFloat(precio),
                    fechaEvento: fecha ? `${fecha}:00` : null,
                    imageUrl,
                    maxEntradas: parseInt(maxEntradas, 10) || 0,
                    recinto: ciudad ? { ciudad, nombre: evento.recinto?.nombre || 'Por definir', aforoMaximo: parseInt(maxEntradas, 10) || 0 } : null
                };
            }
        });

        if (formValues) {
            try {
                await eventService.update(evento.id, formValues, user);
                Swal.fire('Actualizado', 'Evento actualizado con éxito', 'success');
                cargarEventos();
            } catch (err) {
                console.error('Error actualizando', err);
                Swal.fire('Error', err?.response?.data || 'No se pudo actualizar', 'error');
            }
        }
    };

    const handleDelete = async (id, titulo) => {
        const result = await Swal.fire({
            title: '¿Estás seguro?',
            text: `Se eliminará "${titulo}" permanentemente`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        });

        if (result.isConfirmed) {
            try {
                await eventService.delete(id, user);
                Swal.fire('Eliminado', 'Evento eliminado con éxito', 'success');
                cargarEventos();
            } catch (err) {
                console.error('Error eliminando', err);
                Swal.fire('Error', err?.response?.data || 'No se pudo eliminar', 'error');
            }
        }
    };

    if (!user || user.rol !== 'EMPRESA') {
        return (
            <div className="container mx-auto py-10 px-4">
                <div className="bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4">
                    <p className="font-bold">Acceso denegado</p>
                    <p>Solo las empresas pueden acceder a esta sección.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto py-10 px-4">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold text-gray-800">Mis Eventos</h2>
                <button
                    onClick={cargarEventos}
                    className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
                >
                    Actualizar
                </button>
            </div>

            {loading ? (
                <div className="text-center py-20 text-xl font-bold text-gray-500">Cargando tus eventos...</div>
            ) : eventos.length === 0 ? (
                <div className="bg-gray-50 rounded-lg p-10 text-center">
                    <p className="text-gray-500 text-lg mb-4">No has creado ningún evento todavía</p>
                    <a href="/crear-evento" className="text-blue-600 font-bold hover:underline">Crear mi primer evento</a>
                </div>
            ) : (
                <div className="grid grid-cols-1 gap-4">
                    {eventos.map(evento => (
                        <div key={evento.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-xl transition">
                            <div className="flex justify-between items-start">
                                <div className="flex-grow">
                                    <h3 className="text-2xl font-bold text-gray-900 mb-2">{evento.titulo}</h3>

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mt-3">
                                        <div className="flex items-center text-gray-600">
                                            <FaCalendarAlt className="mr-2 text-indigo-500" />
                                            <span>{evento.fechaEvento ? new Date(evento.fechaEvento).toLocaleString() : 'Fecha por definir'}</span>
                                        </div>

                                        <div className="flex items-center text-gray-600">
                                            <FaMapMarkerAlt className="mr-2 text-red-500" />
                                            <span>{evento.recinto?.ciudad || 'Sin ciudad'}</span>
                                        </div>

                                        <div className="flex items-center text-gray-600">
                                            <FaEuroSign className="mr-2 text-green-500" />
                                            <span className="font-bold">{evento.precio ? formatPrice(evento.precio) : 'Gratis'}</span>
                                        </div>

                                        <div className="flex items-center text-gray-600">
                                            <span className="text-sm">Aforo: {evento.recinto?.aforoMaximo || evento.maxEntradas || 'N/A'}</span>
                                        </div>
                                    </div>

                                    {evento.imageUrl && (
                                        <div className="mt-3">
                                            <img src={evento.imageUrl} alt={evento.titulo} className="h-32 w-auto rounded object-cover" />
                                        </div>
                                    )}
                                </div>

                                <div className="flex flex-col gap-2 ml-4">
                                    <button
                                        onClick={() => handleEdit(evento)}
                                        className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
                                        title="Editar"
                                    >
                                        <FaEdit /> Editar
                                    </button>
                                    <button
                                        onClick={() => handleDelete(evento.id, evento.titulo)}
                                        className="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
                                        title="Eliminar"
                                    >
                                        <FaTrash /> Eliminar
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default MyEvents;
