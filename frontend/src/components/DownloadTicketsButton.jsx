import React from 'react';
import { descargarZip } from '../services/api';

export default function DownloadTicketsButton({ compraId, children }) {
  const handleDownload = async () => {
    if (!compraId) {
      alert('No hay compra seleccionada para descargar tickets');
      return;
    }
    try {
      await descargarZip(compraId);
    } catch (e) {
      console.error('Error descargando ZIP de tickets', e);
      alert('No se pudo descargar el ZIP de tickets');
    }
  };

  return (
    <button onClick={handleDownload} disabled={!compraId} className="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 disabled:opacity-50">
      {children || 'Descargar todos los tickets (ZIP)'}
    </button>
  );
}
