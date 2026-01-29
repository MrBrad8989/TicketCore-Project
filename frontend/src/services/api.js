import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

export const eventService = {
    // Búsqueda con filtros
    search: (params) => api.get('/eventos/buscar', { params }),
    getById: (id) => api.get(`/eventos/${id}`),
    // Admin endpoints
    create: (data, user) => api.post('/eventos', data, { headers: user ? { 'X-User-Id': user.id, 'X-User-Rol': user.rol } : {} }),
    update: (id, data, user) => api.put(`/eventos/${id}`, data, { headers: user ? { 'X-User-Id': user.id, 'X-User-Rol': user.rol } : {} }),
    delete: (id, user) => api.delete(`/eventos/${id}`, { headers: user ? { 'X-User-Id': user.id, 'X-User-Rol': user.rol } : {} }),
    getGeneros: () => api.get('/eventos/generos')
};

export const cartService = {
    getCart: (userId) => api.get(`/carrito/${userId}`),

    add: (userId, eventoId, cantidad) =>
        api.post(`/carrito/agregar?usuarioId=${userId}&eventoId=${eventoId}&cantidad=${cantidad}`),

    checkout: (userId) => api.post(`/carrito/checkout/${userId}`),

    disminuir: (userId, eventoId, cantidad) =>
        api.post(`/carrito/disminuir?usuarioId=${userId}&eventoId=${eventoId}&cantidad=${cantidad}`),

    eliminarLinea: (userId, lineaId) =>
        api.delete(`/carrito/linea/${userId}/${lineaId}`)
};

export const authService = {
    login: (creds) => api.post('/auth/login', creds),
    register: (data) => api.post('/auth/register', data)
};