export function formatPrice(value) {
  if (value === null || value === undefined) return 'Gratis';
  // Asegurar que sea número
  const num = Number(value);
  if (Number.isNaN(num)) return 'Gratis';
  // Formatear a 2 decimales y usar coma de miles si se desea (Intl)
  return new Intl.NumberFormat('es-ES', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(num) + ' €';
}

