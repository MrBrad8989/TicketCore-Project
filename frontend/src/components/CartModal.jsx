import React, { useEffect, useState } from 'react';
import { FaShoppingCart, FaTrash, FaCreditCard, FaTimes, FaMinus } from 'react-icons/fa';
import { cartService, descargarPdf, compraDirecta, confirmPayment, descargarZip } from '../services/api';
import Swal from 'sweetalert2';

const CartModal = ({ user, onClose, refreshCart }) => {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [total, setTotal] = useState(0);
    const [processing, setProcessing] = useState(false);

    // Cargar carrito al abrir el modal
    useEffect(() => {
        fetchCart();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const fetchCart = async () => {
        try {
            const { data } = await cartService.getCart(user.id);
            const lineas = data.lineas || [];
            setItems(lineas);

            // Calcular total
            const t = lineas.reduce((acc, item) => acc + (item.cantidad * item.evento.precio), 0);
            setTotal(t);
        } catch (error) {
            console.error("Error cargando carrito", error);
        } finally {
            setLoading(false);
        }
    };

    // Recolecta datos de compradores para una línea concreta
    const collectCompradoresForLinea = async (linea) => {
        const compradores = [];

        // Si estamos logueados, preguntar si queremos autocompletar con los datos del usuario
        if (user) {
            const { isConfirmed } = await Swal.fire({
                title: 'Rellenar con tus datos?',
                text: '¿Deseas rellenar automáticamente los datos de los compradores con tu información de usuario?',
                showCancelButton: true,
                confirmButtonText: 'Sí, rellenar',
                cancelButtonText: 'No, introducir manualmente'
            });
            if (isConfirmed) {
                for (let i = 0; i < linea.cantidad; i++) {
                    compradores.push({
                        nombre: user.nombre || '',
                        apellido: user.apellido || '',
                        email: user.email || ''
                    });
                }
                return compradores;
            }
        }

        // Construir un único formulario con N filas para pedir los datos de una vez
        const rowsHtml = [];
        for (let i = 0; i < linea.cantidad; i++) {
            rowsHtml.push(`
                <div style="margin-bottom:8px;border-radius:6px;padding:8px;background:#f8fafc;border:1px solid #e6eef5">
                  <div style="font-weight:600;margin-bottom:6px">Entrada ${i + 1}</div>
                  <input id="nombre-${i}" class="swal2-input" placeholder="Nombre">
                  <input id="apellido-${i}" class="swal2-input" placeholder="Apellido (opcional)">
                  <input id="email-${i}" class="swal2-input" placeholder="Email">
                  <input id="documento-${i}" class="swal2-input" placeholder="Documento identificacion (opcional)">
                  <input id="fecha-${i}" type="date" class="swal2-input" placeholder="Fecha nacimiento (opcional)">
                </div>
            `);
        }

        const html = `
            <div style="max-height: 60vh; overflow:auto; text-align:left">
              <div style="margin-bottom:8px"><label><input type=checkbox id="copyAll" /> Copiar datos de la primera fila a todas</label></div>
              ${rowsHtml.join('')}
            </div>
        `;

        const result = await Swal.fire({
            title: `Introduce datos de ${linea.cantidad} comprador(es)` ,
            html,
            focusConfirm: false,
            showCancelButton: true,
            customClass: { popup: 'swal2-large' },
            preConfirm: () => {
                const copyAll = document.getElementById('copyAll')?.checked;
                const collected = [];

                // leer primera fila
                const first = {
                    nombre: document.getElementById('nombre-0')?.value || '',
                    apellido: document.getElementById('apellido-0')?.value || '',
                    email: document.getElementById('email-0')?.value || '',
                    documento: document.getElementById('documento-0')?.value || '',
                    fecha: document.getElementById('fecha-0')?.value || ''
                };

                if (copyAll) {
                    // validar la primera fila
                    if (!first.nombre || !first.email) {
                        Swal.showValidationMessage('Nombre y email son obligatorios en la primera fila para copiar a todas');
                        return null;
                    }
                    for (let i = 0; i < linea.cantidad; i++) {
                        collected.push({
                            nombre: first.nombre,
                            apellido: first.apellido,
                            email: first.email,
                            documentoIdentificacion: first.documento || null,
                            fechaNacimiento: first.fecha || null
                        });
                    }
                    return collected;
                }

                // Si no copiamos, leer todas las filas
                for (let i = 0; i < linea.cantidad; i++) {
                    const nombre = document.getElementById(`nombre-${i}`)?.value || '';
                    const apellido = document.getElementById(`apellido-${i}`)?.value || '';
                    const email = document.getElementById(`email-${i}`)?.value || '';
                    const documento = document.getElementById(`documento-${i}`)?.value || '';
                    const fecha = document.getElementById(`fecha-${i}`)?.value || '';

                    if (!nombre || !email) {
                        Swal.showValidationMessage(`Fila ${i + 1}: nombre y email son obligatorios`);
                        return null;
                    }

                    collected.push({
                        nombre,
                        apellido,
                        email,
                        documentoIdentificacion: documento || null,
                        fechaNacimiento: fecha || null
                    });
                }

                return collected;
            }
        });

        if (!result || result.isDismissed) return null;
        return result.value; // array de compradores
    };

    const handleCheckout = async () => {
        setProcessing(true);
        try {
            let usuarioId = user ? user.id : null;

            // Construir payload CompraDirectaRequest a partir de las lineas del carrito
            const lineasPayload = [];

            for (const linea of items) {
                // Collect compradores por linea si cantidad > 0
                let compradores = null;
                if (linea.cantidad > 0) {
                    compradores = await collectCompradoresForLinea(linea);
                    if (compradores === null) {
                        // cancelado por el usuario
                        setProcessing(false);
                        return;
                    }
                }

                // Convertir compradores a CompradorInfoDTO (si existe)
                const compradoresDto = compradores && compradores.length > 0 ? compradores.map(c => ({
                    nombre: c.nombre,
                    apellido: c.apellido,
                    email: c.email,
                    documentoIdentificacion: c.documentoIdentificacion || null,
                    fechaNacimiento: c.fechaNacimiento || null
                })) : null;

                lineasPayload.push({
                    eventoId: linea.evento.id,
                    cantidad: linea.cantidad,
                    compradores: compradoresDto
                });
            }

            const payload = {
                usuarioId: usuarioId,
                compradorInfo: null, // datos por línea ya incluidos
                lineas: lineasPayload
            };

            // Crear la compra (directa) con la info detallada
            const compra = await compraDirecta(payload);

            // Confirmar pago
            const compraConfirmada = await confirmPayment(compra.id);

            // Intentar descargar ZIP con todos los tickets; fallback a PDF si falla
            try {
                await descargarZip(compraConfirmada.id);
            } catch (eZip) {
                console.warn('No se pudo descargar el ZIP, intentando PDF individual', eZip);
                try {
                    await descargarPdf(compraConfirmada.id);
                } catch (ePdf) {
                    console.warn('No se pudo descargar el PDF individual', ePdf);
                }
            }

            Swal.fire({
                icon: 'success',
                title: '¡Compra realizada!',
                text: 'Tu compra se ha procesado correctamente.',
                confirmButtonColor: '#10B981' // Verde Tailwind
            });

            refreshCart(); // Actualizar contador en Navbar
            onClose(); // Cerrar modal
        } catch (error) {
            console.error('Error en checkout', error);
            Swal.fire('Error', error?.response?.data || 'No se pudo procesar la compra. Revisa el stock.', 'error');
        } finally {
            setProcessing(false);
        }
    };

    const decreaseItem = async (linea) => {
        try {
            const { data } = await cartService.disminuir(user.id, linea.evento.id, 1);
            const lineas = data.lineas || [];
            setItems(lineas);
            const t = lineas.reduce((acc, item) => acc + (item.cantidad * item.evento.precio), 0);
            setTotal(t);
            refreshCart();
        } catch (error) {
            console.error('Error disminuyendo item', error);
            Swal.fire('Error', 'No se pudo disminuir la cantidad', 'error');
        }
    };

    const removeLinea = async (linea) => {
        try {
            const { data } = await cartService.eliminarLinea(user.id, linea.id);
            const lineas = data.lineas || [];
            setItems(lineas);
            const t = lineas.reduce((acc, item) => acc + (item.cantidad * item.evento.precio), 0);
            setTotal(t);
            refreshCart();
        } catch (error) {
            console.error('Error eliminando linea', error);
            Swal.fire('Error', 'No se pudo eliminar la línea', 'error');
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-end bg-black/50 backdrop-blur-sm animate-fadeIn">
            {/* Panel lateral deslizante */}
            <div className="bg-white w-full max-w-md h-full shadow-2xl flex flex-col animate-slideInRight">

                {/* Header */}
                <div className="bg-yellow-500 p-5 flex justify-between items-center text-white shadow-md">
                    <h2 className="text-xl font-bold flex items-center gap-2">
                        <FaShoppingCart /> Tu Carrito
                    </h2>
                    <button onClick={onClose} className="hover:text-gray-200 text-xl">
                        <FaTimes />
                    </button>
                </div>

                {/* Body (Lista de items) */}
                <div className="flex-1 overflow-y-auto p-5 space-y-4">
                    {loading ? (
                        <div className="text-center py-10 text-gray-500">Cargando...</div>
                    ) : items.length === 0 ? (
                        <div className="text-center py-20 flex flex-col items-center text-gray-400">
                            <FaShoppingCart size={50} className="mb-4 opacity-20" />
                            <p>Tu carrito está vacío 😢</p>
                            <button onClick={onClose} className="mt-4 text-indigo-600 font-bold hover:underline">
                                ¡Busca eventos!
                            </button>
                        </div>
                    ) : (
                        items.map((item) => (
                            <div key={item.id} className="flex gap-4 p-3 bg-gray-50 rounded-lg border border-gray-100 shadow-sm">
                                <img
                                    src={item.evento.imageUrl || 'https://via.placeholder.com/80'}
                                    className="w-16 h-16 object-cover rounded-md"
                                    alt="Miniatura"
                                />
                                <div className="flex-grow">
                                    <h4 className="font-bold text-gray-800 text-sm line-clamp-1">{item.evento.titulo}</h4>
                                    <div className="text-xs text-gray-500 mt-1">
                                        {item.cantidad} x {item.evento.precio} €
                                    </div>
                                </div>
                                <div className="flex flex-col items-end justify-between">
                  <span className="font-bold text-indigo-600">
                    {(item.cantidad * item.evento.precio).toFixed(2)} €
                  </span>
                                    <div className="flex gap-2 mt-2">
                                        <button onClick={() => decreaseItem(item)} className="p-2 rounded-full bg-gray-100 hover:bg-gray-200 text-sm">
                                            <FaMinus />
                                        </button>

                                        <button onClick={() => removeLinea(item)} className="p-2 rounded-full bg-red-100 hover:bg-red-200 text-sm text-red-600">
                                            <FaTrash />
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {/* Footer (Total y Botón) */}
                {items.length > 0 && (
                    <div className="p-5 bg-gray-50 border-t border-gray-200">
                        <div className="flex justify-between items-center mb-4 text-lg">
                            <span className="font-semibold text-gray-600">Total:</span>
                            <span className="font-bold text-2xl text-indigo-900">{total.toFixed(2)} €</span>
                        </div>

                        <button
                            onClick={handleCheckout}
                            disabled={processing}
                            className="w-full bg-green-600 text-white font-bold py-3 rounded-xl hover:bg-green-700 transition flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed shadow-lg"
                        >
                            {processing ? (
                                <span>Procesando...</span>
                            ) : (
                                <>
                                    <FaCreditCard /> Pagar Ahora
                                </>
                            )}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default CartModal;

