import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

export const eventService = {
    // Búsqueda con filtros
    search: (params) => api.get('/eventos/buscar', { params }),
    getById: (id) => api.get(`/eventos/${id}`),
    // Admin endpoints
    create: (data) => api.post('/eventos', data),
    update: (id, data) => api.put(`/eventos/${id}`, data),
    delete: (id) => api.delete(`/eventos/${id}`),
    getGeneros: () => api.get('/eventos/generos')
};

export const cartService = {
    getCart: (userId) => api.get(`/carrito/${userId}`),

    add: (userId, eventoId, cantidad) =>
        api.post(`/carrito/agregar?usuarioId=${userId}&eventoId=${eventoId}&cantidad=${cantidad}`),

    checkout: (userId) => api.post(`/carrito/checkout/${userId}`)
};

export const authService = {
    login: (creds) => api.post('/auth/login', creds),
    register: (data) => api.post('/auth/register', data)
};