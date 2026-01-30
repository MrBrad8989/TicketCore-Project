import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Home = () => {
    useEffect(() => {
        document.title = 'TicketCore - Inicio';
    }, []);
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-gradient-to-b from-indigo-900 to-purple-800 text-white">
            <div className="container mx-auto py-28 px-4 text-center">
                <h1 className="text-5xl font-extrabold mb-4">Bienvenido a TicketCore</h1>
                <p className="text-xl mb-8">Compra entradas para conciertos, festivales y eventos en tu ciudad de forma rápida y segura.</p>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-4xl mx-auto">
                    <div className="bg-white/10 p-6 rounded-lg">
                        <h3 className="text-2xl font-bold mb-2">Encuentra eventos</h3>
                        <p className="text-sm">Filtra por ciudad, género, fecha o artista y descubre eventos cerca de ti.</p>
                    </div>
                    <div className="bg-white/10 p-6 rounded-lg">
                        <h3 className="text-2xl font-bold mb-2">Compra segura</h3>
                        <p className="text-sm">Pago seguro y gestión de tus entradas desde tu cuenta.</p>
                    </div>
                    <div className="bg-white/10 p-6 rounded-lg">
                        <h3 className="text-2xl font-bold mb-2">Organiza tus eventos</h3>
                        <p className="text-sm">¿Eres empresa? Crea y gestiona tus eventos de forma sencilla.</p>
                    </div>
                </div>

                <div className="mt-12">
                    <button onClick={() => navigate('/buscar')} className="bg-yellow-400 text-black font-bold px-6 py-3 rounded-lg hover:bg-yellow-500">Buscar eventos</button>
                </div>

            </div>
        </div>
    );
};

export default Home;
