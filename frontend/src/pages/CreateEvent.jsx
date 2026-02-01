import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { eventService } from '../services/api';
import { useNavigate } from 'react-router-dom';
import Swal from 'sweetalert2';

const CreateEvent = () => {
    const { user } = useAuth();
    const navigate = useNavigate();

    const [form, setForm] = useState({
        titulo: '',
        fechaEvento: '',
        precio: '',
        imageUrl: '',
        maxEntradas: 100,
        descripcion: '',
        genero: '',
        ciudad: ''
    });

    const [generos, setGeneros] = useState([]);

    useEffect(() => { document.title = 'TicketCore - Crear evento'; }, []);

    useEffect(() => {
        // cargar géneros disponibles para el select
        (async () => {
            try {
                const { data } = await eventService.getGeneros();
                setGeneros(data || []);
            } catch (e) {
                // ignore
            }
        })();
    }, []);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!user) { Swal.fire('Acceso', 'Debes iniciar sesión como empresa', 'warning'); return; }

        const dto = {
            titulo: form.titulo,
            fechaEvento: form.fechaEvento ? `${form.fechaEvento}T00:00:00` : null,
            precio: parseFloat(form.precio) || 0,
            imageUrl: form.imageUrl,
            maxEntradas: parseInt(form.maxEntradas, 10) || 0,
            descripcion: form.descripcion,
            recinto: form.ciudad ? { ciudad: form.ciudad, nombre: 'Por definir', aforoMaximo: form.maxEntradas } : null
        };

        // Si el usuario es una empresa, añadimos un artista con el nombre de la empresa/usuario para que aparezca en la sección artistas
        if (user) {
            const nombreArtista = user.empresaNombre || user.nombre || null;
            if (nombreArtista) {
                const artistaObj = { nombre: nombreArtista };
                if (form.genero) artistaObj.genero = form.genero;
                dto.artistas = [artistaObj];
            }
        }

        try {
            const { data } = await eventService.create(dto, user);
            Swal.fire('Creado', 'Evento creado con éxito', 'success');
            // Llevar a la búsqueda; incluimos solo createdId (y fecha si hay) para abrir el modal sin forzar keyword
            const params = new URLSearchParams();
            if (form.fechaEvento) params.set('fecha', form.fechaEvento);
            if (data && data.id) params.set('createdId', data.id);
            const qs = params.toString();
            navigate(qs ? `/buscar?${qs}` : `/buscar`);
        } catch (err) {
            console.error(err);
            Swal.fire('Error', err?.response?.data?.message || 'No se pudo crear', 'error');
        }
    };

    return (
        <div className="container mx-auto py-10 px-4">
            <h2 className="text-2xl font-bold mb-4">Crear Evento</h2>
            <form onSubmit={handleSubmit} className="max-w-xl bg-white p-6 rounded shadow">
                <label className="block mb-2 font-semibold">Título</label>
                <input name="titulo" value={form.titulo} onChange={handleChange} className="w-full border p-2 rounded mb-4" required />

                <label className="block mb-2 font-semibold">Ciudad</label>
                <select name="ciudad" value={form.ciudad} onChange={handleChange} className="w-full border p-2 rounded mb-4" required>
                    <option value="">-- Seleccionar ciudad --</option>
                    <option value="Madrid">Madrid</option>
                    <option value="Barcelona">Barcelona</option>
                    <option value="Valencia">Valencia</option>
                    <option value="Bilbao">Bilbao</option>
                </select>

                <label className="block mb-2 font-semibold">Fecha</label>
                <input type="date" name="fechaEvento" value={form.fechaEvento} onChange={handleChange} className="w-full border p-2 rounded mb-4" required />

                <label className="block mb-2 font-semibold">Precio</label>
                <input name="precio" value={form.precio} onChange={handleChange} className="w-full border p-2 rounded mb-4" required />

                <label className="block mb-2 font-semibold">Imagen URL</label>
                <input name="imageUrl" value={form.imageUrl} onChange={handleChange} className="w-full border p-2 rounded mb-4" />

                <label className="block mb-2 font-semibold">Género (asociado al artista)</label>
                <select name="genero" value={form.genero} onChange={handleChange} className="w-full border p-2 rounded mb-4">
                    <option value="">-- Seleccionar género --</option>
                    {generos.map(g => <option key={g} value={g}>{g}</option>)}
                    <option value="Otra">Otra</option>
                </select>

                <label className="block mb-2 font-semibold">Máximo de entradas</label>
                <input type="number" name="maxEntradas" value={form.maxEntradas} onChange={handleChange} className="w-full border p-2 rounded mb-4" />

                <label className="block mb-2 font-semibold">Descripción</label>
                <textarea name="descripcion" value={form.descripcion} onChange={handleChange} className="w-full border p-2 rounded mb-4" />

                <div className="flex gap-2 justify-end">
                    <button type="button" onClick={() => navigate('/')} className="px-4 py-2 bg-gray-200 rounded">Cancelar</button>
                    <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded">Crear evento</button>
                </div>
            </form>
        </div>
    );
};

export default CreateEvent;
