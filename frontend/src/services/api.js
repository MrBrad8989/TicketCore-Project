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

    // Mantener antigua firma para compatibilidad (llama a checkoutFromCart internamente)
    checkout: (userId, compradorInfo = null) => checkoutFromCart(userId, compradorInfo),

    disminuir: (userId, eventoId, cantidad) =>
        api.post(`/carrito/disminuir?usuarioId=${userId}&eventoId=${eventoId}&cantidad=${cantidad}`),

    eliminarLinea: (userId, lineaId) =>
        api.delete(`/carrito/linea/${userId}/${lineaId}`)
};

export const authService = {
    login: (creds) => api.post('/auth/login', creds),
    register: (data) => api.post('/auth/register', data)
};

// Funciones nuevas/actualizadas para integrar con el backend de compras
export async function checkoutFromCart(usuarioId, compradorInfo = null) {
  // Enviar siempre JSON para evitar Content-Type x-www-form-urlencoded
  const headers = { 'Content-Type': 'application/json' };
  // Si compradorInfo es null, enviar un objeto vacío {} para forzar JSON
  const body = compradorInfo ? compradorInfo : {};
  const res = await api.post(`/compras/carrito/${usuarioId}`, body, { headers });
  return res.data;
}

export async function compraDirecta(payload) {
  // Forzar JSON
  const res = await api.post('/compras/directo', payload, { headers: { 'Content-Type': 'application/json' } });
  return res.data;
}

export async function descargarPdf(compraId) {
  const res = await api.get(`/compras/${compraId}/pdf`, { responseType: 'blob' });
  const blob = new Blob([res.data], { type: 'application/pdf' });
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `compra-${compraId}.pdf`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}

export async function confirmPayment(compraId) {
  const res = await api.post(`/pagos/${compraId}/confirm`, {}, { headers: { 'Content-Type': 'application/json' } });
  return res.data;
}

export async function descargarZip(compraId) {
  const res = await api.get(`/compras/${compraId}/zip`, { responseType: 'blob' });
  const blob = new Blob([res.data], { type: 'application/zip' });
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `compra-${compraId}-tickets.zip`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}
