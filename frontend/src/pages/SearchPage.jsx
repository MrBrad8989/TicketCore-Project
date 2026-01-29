import React, { useState, useEffect } from 'react';
import { eventService } from '../services/api';
import { useAuth } from '../context/AuthContext';
import EventCard from '../components/EventCard'; // (Crear este componente visual)
import Swal from 'sweetalert2';

const SearchPage = ({ onSelectEvent }) => {
    const { isAdmin, user } = useAuth();
    const [eventos, setEventos] = useState([]);
    const [loading, setLoading] = useState(false);

    // Filtros
    const [filters, setFilters] = useState({
        ciudad: '', keyword: '', fecha: '', genero: '', page: 0
    });
    const [totalPages, setTotalPages] = useState(0);
    const [generos, setGeneros] = useState([]);

    useEffect(() => {
        cargarGeneros();
        buscar();
    }, [filters.page]); // Recargar si cambia la página

    const updateFilter = (patch) => {
        setFilters(prev => ({ ...prev, ...patch, page: 0 }));
    }

    const cargarGeneros = async () => {
        try { const { data } = await eventService.getGeneros(); setGeneros(data); } catch(e){}
    };

    const buscar = async () => {
        setLoading(true);
        try {
            // Mapeamos los filtros al formato que espera el Backend
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
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if ((await Swal.fire({ title: '¿Borrar?', icon: 'warning', showCancelButton: true })).isConfirmed) {
            await eventService.delete(id, user);
            buscar(); // Recargar lista
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 pb-10">
            {/* Barra de Filtros (Hero) */}
            <div className="bg-gradient-to-r from-blue-900 to-indigo-800 p-8 shadow-lg text-white">
                <div className="container mx-auto">
                    <h2 className="text-2xl font-bold mb-4">Encuentra tu evento ideal</h2>
                    <div className="bg-white p-4 rounded-lg shadow-lg text-gray-800 grid grid-cols-1 md:grid-cols-5 gap-4">

                        <select
                            className="form-select border p-2 rounded w-full"
                            onChange={e => updateFilter({ ciudad: e.target.value })}
                        >
                            <option value="">Todas las ciudades</option>
                            <option value="Madrid">Madrid</option>
                            <option value="Barcelona">Barcelona</option>
                            <option value="Valencia">Valencia</option>
                            <option value="Bilbao">Bilbao</option>
                        </select>

                        <input placeholder="Artista..." className="border p-2 rounded" onChange={e => updateFilter({ keyword: e.target.value })} />

                        <input type="date" className="border p-2 rounded" onChange={e => updateFilter({ fecha: e.target.value })} />

                        <select className="border p-2 rounded" onChange={e => updateFilter({ genero: e.target.value })}>
                            <option value="">Todos los géneros</option>
                            {generos.map(g => <option key={g} value={g}>{g}</option>)}
                        </select>

                        <button onClick={() => buscar()} className="bg-yellow-500 hover:bg-yellow-600 text-white font-bold rounded shadow transition">
                            BUSCAR
                        </button>
                    </div>
                </div>
            </div>

            {/* Grid de Resultados */}
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
                                onDelete={() => handleDelete(ev.id)}
                            />
                        ))}
                    </div>
                )}

                {/* Paginación */}
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
