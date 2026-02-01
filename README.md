# 🎫 TicketCore Project

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-Rapid-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-Enabled-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-MySQL_8.0-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

---

## 📖 Descripción del Proyecto

**TicketCore** es una solución Full-Stack robusta para la gestión y venta de entradas de eventos musicales. Este proyecto simula un entorno de producción real combinando un backend potente en **Spring Boot** con un frontend moderno y reactivo en **React (Vite)**.

El sistema destaca por su capacidad de **sincronización con la API de Ticketmaster**, permitiendo poblar la base de datos con eventos reales, y por su flujo de compra transaccional que culmina en la generación dinámica de entradas en formato **PDF**.

---

## 🚀 Características Estrella

### 🛍️ Para el Usuario (Cliente)
* **Experiencia Reactiva:** Interfaz ultra-rápida (SPA) construida con React 19 y estilizada con Tailwind CSS.
* **Carrito de Compra Inteligente:** Persistencia de estado, gestión de cantidades y validación de stock en tiempo real.
* **Motor de Búsqueda:** Filtrado avanzado por ciudad, género musical y fechas.
* **Checkout & PDF:** Generación automática de entradas en PDF (usando Apache PDFBox) tras una compra exitosa.
* **Seguridad:** Login y registro de usuarios con validación de credenciales.

### ⚙️ Para el Administrador
* **Gestión Total (CRUD):** Panel para crear, editar o eliminar eventos, recintos y artistas.
* **Sincronización Ticketmaster:** Servicio avanzado que consume la API externa, evitando duplicados mediante validación de IDs (`ticketmasterId`) y lógica difusa para nombres de recintos y artistas.
* **Dashboard de Control:** Vista privilegiada del inventario.

---

## 🛠️ Stack Tecnológico

### Backend (API REST)
* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.4.1
* **Base de Datos:** MySQL 8.0 (Dockerizada)
* **ORM:** Spring Data JPA + Hibernate
* **Documentación:** OpenAPI 3 (Swagger UI)
* **Utilidades:**
    * `MapStruct`: Mapeo eficiente Entidad <-> DTO.
    * `Apache PDFBox`: Generación de tickets físicos en PDF.
    * `Lombok`: Reducción de código repetitivo.

### Frontend (SPA)
* **Framework:** React 19
* **Build Tool:** Vite (para un desarrollo y compilación instantáneos)
* **Estilos:** Tailwind CSS 4
* **Cliente HTTP:** Axios
* **UI/UX:** SweetAlert2 (alertas), React Icons.

### Infraestructura
* **Contenerización:** Docker Compose para la base de datos MySQL.

---

## 🏛️ Arquitectura de Datos

El modelo de dominio está diseñado para soportar alta concurrencia y consistencia de datos.

![Diagrama Entidad-Relación](https://i.imgur.com/YIEcW3l.png)

> **Nota sobre el Modelo:**
> * **Integridad:** Un `Ticket` solo se genera si el `Pago` es aprobado y hay aforo en el `Recinto`.
> * **Relaciones:** Los `Artistas` tienen una relación N:M con `Eventos`, permitiendo festivales con múltiples bandas.

---

## ⚡ Guía de Instalación y Ejecución

Sigue estos pasos para levantar el proyecto completo en tu máquina local.

### 1. Prerrequisitos
* Java 17 JDK instalado.
* Node.js (v18 o superior) y npm.
* Docker Desktop instalado y corriendo.

### 2. Base de Datos (Docker)
El proyecto incluye un `docker-compose.yml` configurado en el puerto **3307** para no chocar con instalaciones locales de MySQL.

```bash
# En la raíz del proyecto
docker-compose up -d