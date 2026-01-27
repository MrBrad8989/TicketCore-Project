import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/api';
import Swal from 'sweetalert2';
import { FaUser, FaEnvelope, FaLock, FaBuilding } from 'react-icons/fa';

const LoginModal = ({ onClose }) => {
    const { login } = useAuth();
    const [isRegister, setIsRegister] = useState(false); // Switch entre Login/Registro

    // Estado del formulario
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        nombre: '', // Solo para registro
        isAdmin: false // Solo para registro
    });

    const handleChange = (e) => {
        const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
        setFormData({ ...formData, [e.target.name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            if (isRegister) {
                // --- LOGICA DE REGISTRO ---
                const registerData = {
                    nombre: formData.nombre,
                    email: formData.email,
                    password: formData.password,
                    rol: formData.isAdmin ? 'ADMIN' : 'USER'
                };

                await authService.register(registerData);

                Swal.fire({
                    icon: 'success',
                    title: '¡Cuenta creada!',
                    text: 'Ahora puedes iniciar sesión',
                    timer: 2000,
                    showConfirmButton: false
                });

                // Cambiar automáticamente a vista de Login
                setIsRegister(false);

            } else {
                // --- LOGICA DE LOGIN ---
                const { data } = await authService.login({
                    email: formData.email,
                    password: formData.password
                });

                // Guardar usuario en el contexto
                login(data);

                Swal.fire({
                    icon: 'success',
                    title: `¡Hola, ${data.nombre}!`,
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });

                onClose(); // Cerrar modal al entrar
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: isRegister ? 'No se pudo crear la cuenta (quizás el email ya existe)' : 'Credenciales incorrectas'
            });
        }
    };

    return (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4 backdrop-blur-sm">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden animate-scaleIn">

                {/* Cabecera */}
                <div className="bg-indigo-600 p-6 text-center">
                    <h2 className="text-2xl font-bold text-white">
                        {isRegister ? 'Crear Cuenta' : 'Bienvenido'}
                    </h2>
                    <p className="text-indigo-200 text-sm mt-1">
                        {isRegister ? 'Únete a TicketCore' : 'Accede a tus entradas'}
                    </p>
                </div>

                {/* Formulario */}
                <form onSubmit={handleSubmit} className="p-6 flex flex-col gap-4">

                    {/* Campo Nombre (Solo Registro) */}
                    {isRegister && (
                        <div className="relative">
                            <FaUser className="absolute left-3 top-3 text-gray-400" />
                            <input
                                name="nombre"
                                type="text"
                                placeholder="Nombre completo"
                                className="w-full pl-10 p-2.5 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                                required
                                onChange={handleChange}
                            />
                        </div>
                    )}

                    <div className="relative">
                        <FaEnvelope className="absolute left-3 top-3 text-gray-400" />
                        <input
                            name="email"
                            type="email"
                            placeholder="Correo electrónico"
                            className="w-full pl-10 p-2.5 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                            required
                            onChange={handleChange}
                        />
                    </div>

                    <div className="relative">
                        <FaLock className="absolute left-3 top-3 text-gray-400" />
                        <input
                            name="password"
                            type="password"
                            placeholder="Contraseña"
                            className="w-full pl-10 p-2.5 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                            required
                            onChange={handleChange}
                        />
                    </div>

                    {/* Checkbox Admin (Solo Registro) */}
                    {isRegister && (
                        <label className="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
                            <input
                                name="isAdmin"
                                type="checkbox"
                                className="rounded text-indigo-600 focus:ring-indigo-500"
                                onChange={handleChange}
                            />
                            <span className="flex items-center gap-1"><FaBuilding /> Soy Empresa (Admin)</span>
                        </label>
                    )}

                    <button className="bg-indigo-600 text-white font-bold py-2.5 rounded-lg hover:bg-indigo-700 transition shadow-md mt-2">
                        {isRegister ? 'Registrarse' : 'Entrar'}
                    </button>
                </form>

                {/* Footer (Toggle Login/Register) */}
                <div className="bg-gray-50 p-4 text-center border-t text-sm">
                    <p className="text-gray-600">
                        {isRegister ? '¿Ya tienes cuenta?' : '¿No tienes cuenta?'}
                        <button
                            onClick={() => setIsRegister(!isRegister)}
                            className="text-indigo-600 font-bold ml-1 hover:underline"
                        >
                            {isRegister ? 'Inicia Sesión' : 'Regístrate'}
                        </button>
                    </p>
                    <button onClick={onClose} className="mt-4 text-gray-400 text-xs hover:text-gray-600">
                        Cerrar ventana
                    </button>
                </div>
            </div>
        </div>
    );
};

export default LoginModal;