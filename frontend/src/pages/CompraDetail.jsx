import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import DownloadTicketsButton from '../components/DownloadTicketsButton';

export default function CompraDetail() {
  const { id } = useParams();
  const [compra, setCompra] = useState(null);

  useEffect(() => {
    async function load() {
      try {
        const res = await fetch(`/api/compras/${id}`);
        if (res.ok) {
          const data = await res.json();
          setCompra(data);
        }
      } catch (e) {
        console.error(e);
      }
    }
    load();
  }, [id]);

  if (!compra) return <div className="p-6">Cargando compra...</div>;

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Compra #{compra.id}</h2>
      <p>Estado: {compra.estado}</p>
      <p>Total: {compra.total}</p>

      <div className="mt-4">
        {compra.estado === 'PAID' && compra.tickets && compra.tickets.length > 0 ? (
          <div>
            <DownloadTicketsButton compraId={compra.id}>Descargar todos los tickets</DownloadTicketsButton>

            <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
              {compra.tickets.map((t) => (
                <div key={t.id} className="p-4 border rounded-lg bg-white shadow-sm text-gray-800">
                  <h3 className="font-bold">Ticket: {t.codigo}</h3>
                  <p className="text-sm text-gray-600">Evento: {t.tituloEvento}</p>
                  <p className="text-sm text-gray-600">Fecha compra: {t.fechaCompra}</p>

                  <div className="mt-2">
                    <h4 className="font-semibold">Datos del titular:</h4>
                    <p>Nombre: {t.compradorNombre || '—'}</p>
                    <p>Email: {t.compradorEmail || '—'}</p>
                    <p>Documento: {t.compradorDocumento || '—'}</p>
                    <p>Fecha Nac.: {t.compradorFechaNacimiento || '—'}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className="text-sm text-gray-500">Los tickets se generarán y estarán disponibles tras confirmar el pago.</div>
        )}
      </div>
    </div>
  );
}
