# 🎫 TicketCore Project

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D.svg)](http://localhost:8080/swagger-ui/index.html)

**TicketCore** es una plataforma integral de gestión y venta de entradas para eventos musicales. El sistema no solo sincroniza eventos reales desde la API de Ticketmaster, sino que implementa un flujo completo de comercio electrónico con **carrito de compra**, autenticación de usuarios y un panel de administración para la gestión de contenido.

---

## 🚀 Características Principales

### 🛍️ Experiencia de Usuario (E-Commerce)
* **Carrito de Compra Persistente:** Los usuarios pueden añadir múltiples eventos al carrito, modificar cantidades y procesar un pedido conjunto (`Carrito` -> `LineaCarrito`).
* **Checkout Transaccional:** Conversión atómica de los ítems del carrito en `Tickets` reales, validando stock en el último momento.
* **Búsqueda Inteligente:**
  * Filtrado por ciudad, palabra clave, género y fecha.
  * **Modo Descubrimiento:** Si no se selecciona ciudad, el sistema sugiere eventos de forma aleatoria (`ORDER BY RAND()`).
* **Autenticación:** Sistema de Login y Registro de usuarios.

### ⚙️ Gestión y Administración
* **Panel de Administración:** Los usuarios con rol `ADMIN` tienen acceso a controles exclusivos en el frontend para:
  * Crear eventos manualmente.
  * Editar detalles de eventos (precios, fechas, imágenes).
  * Borrar eventos del sistema.
* **Sincronización Externa:** Integración con la API de Ticketmaster para importar masivamente eventos, recintos y artistas, evitando duplicados.

### 💻 Frontend Integrado
* Interfaz SPA (Single Page Application) construida con **HTML5, Bootstrap 5 y JavaScript Vanilla**.
* Consumo de API mediante `Fetch API`.
* Modales dinámicos para detalles de eventos, carrito de compra y formularios de login.

---

## 🏛️ Modelo de Dominio y Datos

A continuación se muestra el esquema visual de la base de datos:

![Diagrama Entidad-Relación](https://i.imgur.com/oGYLKit.png)

### Entidades Principales
| Entidad | Descripción |
| :--- | :--- |
| **Usuario** | Clientes de la plataforma. Incluye gestión de roles (`USER`, `ADMIN`) y credenciales. |
| **Carrito** | Vinculado 1:1 al usuario. Contiene múltiples líneas de pedido (`LineaCarrito`) antes de la compra. |
| **Evento** | Núcleo del sistema. Contiene título, precio, imagen y *ticketmasterId* para control de duplicados. |
| **Recinto** | Lugar físico. Controla la ciudad y el **aforo máximo** crítico para evitar *overbooking*. |
| **Ticket** | Representa la entrada final generada tras el checkout. Incluye un **UUID único**. |
| **Artista** | Relación N:M con Eventos. Permite categorizar por género musical. |

---

## 📋 Reglas de Negocio Implementadas

### 1. Flujo de Compra (Carrito -> Ticket)
El proceso de compra no es directo, sino que pasa por un estado intermedio:
1.  **Agregar:** Se crea o actualiza una `LineaCarrito`.
2.  **Checkout:** El servicio `finalizarCompra`:
  * Recorre las líneas del carrito.
  * Verifica el aforo disponible por evento (`countByEventoId`).
  * Genera `N` entidades `Ticket` según la cantidad solicitada.
  * Vacia el carrito tras el éxito.
  * Todo bajo una transacción (`@Transactional`) para asegurar la integridad.

### 2. Control de Aforo (Sold Out)
Antes de generar cualquier ticket, se consulta el stock. Si `entradas_vendidas >= aforo_recinto`, se lanza una excepción personalizada `SoldOutException` (HTTP 400), bloqueando la compra.

### 3. Sincronización Inteligente
* **Persistencia de Relaciones:** Al importar de Ticketmaster, el sistema detecta si el **Recinto** o el **Artista** ya existen en base de datos (por nombre) para reutilizarlos y no duplicar registros.
* **Datos por Defecto:** Si la API externa no provee precios o fechas, el sistema asigna valores lógicos por defecto (ej. fecha actual + 15 días) para mantener la consistencia.

---

## 🛠️ Stack Tecnológico

* **Backend:** Java 17, Spring Boot 3.4.1.
* **Datos:** Spring Data JPA, MySQL 8.0 (Docker), Hibernate.
* **API Doc:** SpringDoc OpenAPI (Swagger UI).
* **Mapeo:** MapStruct (Entity <-> DTO).
* **Seguridad:** Gestión de usuarios y roles propia.
* **Frontend:** HTML5, CSS3, Bootstrap 5, JS.

---

## ⚙️ Instalación y Despliegue

### Prerrequisitos
* Java 17 JDK
* Maven
* Docker Desktop (para la BBDD)

### 1. Base de Datos
El proyecto incluye un `docker-compose.yml`. Inicia la base de datos:
```bash
docker-compose up -d