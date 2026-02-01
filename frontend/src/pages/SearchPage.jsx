import React, { useState, useEffect, useRef } from 'react';
import { eventService } from '../services/api';
import { useAuth } from '../context/AuthContext';
import EventCard from '../components/EventCard';
import Swal from 'sweetalert2';
import { useLocation, useNavigate } from 'react-router-dom';

const SearchPage = ({ onSelectEvent }) => {
    const { isAdmin, user } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();

    const [createdIdToOpen, setCreatedIdToOpen] = useState(null);
    const [eventos, setEventos] = useState([]);
    const [loading, setLoading] = useState(false);

    const [filters, setFilters] = useState({
        ciudad: '', keyword: '', fecha: '', genero: '', page: 0
    });
    const [totalPages, setTotalPages] = useState(0);
    const [generos, setGeneros] = useState([]);

    const createdOpenedRef = useRef(false);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const kw = params.get('keyword');
        const fechaParam = params.get('fecha');
        const created = params.get('createdId');

        if (created) {
            setFilters(prev => ({ ...prev, keyword: '', fecha: fechaParam || prev.fecha, page: 0 }));
        } else {
            setFilters(prev => ({ ...prev, keyword: kw || prev.keyword, fecha: fechaParam || prev.fecha, page: 0 }));
        }

        if (created) {
            setCreatedIdToOpen(created);
            try {
                const newParams = new URLSearchParams(location.search);
                newParams.delete('createdId');
                const newSearch = newParams.toString();
                if (newSearch) {
                    navigate(`${location.pathname}?${newSearch}`, { replace: true });
                } else {
                    navigate(location.pathname, { replace: true });
                }
            } catch (e) {
                // ignore
            }
        }

        cargarGeneros();
    }, [location.search, location.pathname, navigate]);

    useEffect(() => { buscar(); }, [filters]);
    useEffect(() => { document.title = 'TicketCore - Buscar'; }, []);

    const updateFilter = (patch) => setFilters(prev => ({ ...prev, ...patch, page: 0 }));

    const cargarGeneros = async () => {
        try { const { data } = await eventService.getGeneros(); setGeneros(data); } catch(e){}
    };

    const buscar = async () => {
        setLoading(true);
        try {
            const params = {
                page: filters.page,
                size: 9,
                ciudad: filters.ciudad || null,
                keyword: filters.keyword || null,
                genero: filters.genero || null,
                fechaInicio: filters.fecha ? `${filters.fecha}T00:00:00` : null
            };

            const { data } = await eventService.search(params);
            setEventos(data.content);
            setTotalPages(data.page.totalPages);

            const createdId = createdIdToOpen;
            if (createdId && !createdOpenedRef.current) {
                const found = data.content.find(e => e.id && String(e.id) === String(createdId));
                if (found) {
                    onSelectEvent(found);
                    setCreatedIdToOpen(null);
                } else {
                    try {
                        const { data: single } = await eventService.getById(createdId);
                        onSelectEvent(single);
                        setCreatedIdToOpen(null);
                    } catch (e) {
                        console.warn('Created event not found by id', createdId);
                    }
                }
                createdOpenedRef.current = true;
            }

        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if ((await Swal.fire({ title: '¿Borrar?', icon: 'warning', showCancelButton: true })).isConfirmed) {
            await eventService.delete(id, user);
            buscar();
        }
    };

    const handleEdit = async (evento) => {
        const { value: formValues } = await Swal.fire({
            title: 'Editar Evento',
            html: `
                <input id="titulo" class="swal2-input" placeholder="Título" value="${evento.titulo || ''}">
                <input id="precio" type="number" class="swal2-input" placeholder="Precio" value="${evento.precio || ''}">
                <input id="fecha" type="date" class="swal2-input" value="${evento.fechaEvento ? evento.fechaEvento.split('T')[0] : ''}">
            `,
            focusConfirm: false,
            showCancelButton: true,
            confirmButtonText: 'Guardar',
            cancelButtonText: 'Cancelar',
            preConfirm: () => {
                return {
                    titulo: document.getElementById('titulo').value,
                    precio: document.getElementById('precio').value,
                    fechaEvento: document.getElementById('fecha').value ? `${document.getElementById('fecha').value}T00:00:00` : null
                };
            }
        });

        if (formValues) {
            try {
                await eventService.update(evento.id, formValues, user);
                Swal.fire('Actualizado', 'Evento actualizado con éxito', 'success');
                buscar();
            } catch (err) {
                Swal.fire('Error', err?.response?.data || 'No se pudo actualizar', 'error');
            }
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 pb-10">
            <div className="bg-gradient-to-r from-blue-900 to-indigo-800 p-8 shadow-lg text-white">
                <div className="container mx-auto">
                    <h2 className="text-2xl font-bold mb-4">Encuentra tu evento ideal</h2>
                    <div className="bg-white p-4 rounded-lg shadow-lg text-gray-800 grid grid-cols-1 md:grid-cols-5 gap-4">

                        <select
                            className="form-select border p-2 rounded w-full"
                            value={filters.ciudad}
                            onChange={e => updateFilter({ ciudad: e.target.value })}
                        >
                            <option value="">Todas las ciudades</option>
                            <option value="Madrid">Madrid</option>
                            <option value="Barcelona">Barcelona</option>
                            <option value="Valencia">Valencia</option>
                            <option value="Bilbao">Bilbao</option>
                        </select>

                        <input
                            placeholder="Artista..."
                            className="border p-2 rounded"
                            value={filters.keyword}
                            onChange={e => updateFilter({ keyword: e.target.value })}
                        />

                        <input
                            type="date"
                            className="border p-2 rounded"
                            value={filters.fecha}
                            onChange={e => updateFilter({ fecha: e.target.value })}
                        />

                        <select
                            className="border p-2 rounded"
                            value={filters.genero}
                            onChange={e => updateFilter({ genero: e.target.value })}
                        >
                            <option value="">Todos los géneros</option>
                            {generos.map(g => <option key={g} value={g}>{g}</option>)}
                        </select>

                        <div className="flex items-center gap-2">
                            <button onClick={() => buscar()} className="bg-yellow-500 hover:bg-yellow-600 text-white font-bold rounded shadow transition px-4 py-2">BUSCAR</button>
                            <button onClick={() => { setFilters({ ciudad: '', keyword: '', fecha: '', genero: '', page: 0 }); }} className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold rounded shadow transition px-4 py-2">LIMPIAR</button>
                        </div>
                    </div>
                </div>
            </div>

            <div className="container mx-auto mt-8 px-4">
                {loading ? (
                    <div className="text-center py-10 text-xl font-bold text-gray-500">Cargando eventos...</div>
                ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                        {eventos.map(ev => (
                            <EventCard
                                key={ev.id}
                                evento={ev}
                                onClick={() => onSelectEvent(ev)}
                                isAdmin={isAdmin}
                                user={user}
                                onDelete={() => handleDelete(ev.id)}
                                onEdit={() => handleEdit(ev)}
                            />
                        ))}
                    </div>
                )}

                <div className="flex justify-center mt-8 gap-4">
                    <button
                        disabled={filters.page === 0}
                        onClick={() => setFilters({...filters, page: filters.page - 1})}
                        className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
                    >
                        Anterior
                    </button>
                    <span className="py-2 font-bold">Página {filters.page + 1} de {totalPages}</span>
                    <button
                        disabled={filters.page >= totalPages - 1}
                        onClick={() => setFilters({...filters, page: filters.page + 1})}
                        className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
                    >
                        Siguiente
                    </button>
                </div>
            </div>
        </div>
    );
};

export default SearchPage;
