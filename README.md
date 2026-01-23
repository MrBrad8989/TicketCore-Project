# 🎫 TicketCore Project

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**TicketCore** es una plataforma completa de gestión y venta de entradas para eventos musicales. El sistema sincroniza eventos reales desde la API de Ticketmaster, permite búsquedas avanzadas y simula un proceso de compra de entradas con control de aforo en tiempo real.

---

## 🚀 Características Principales

* **Sincronización Externa:** Integración con la API de Ticketmaster para importar eventos, recintos y artistas de una ciudad específica (por defecto Madrid).
* **Gestión de Eventos:** Visualización de conciertos con imágenes, precios y ubicaciones.
* **Búsqueda Avanzada:** Filtrado de eventos por ciudad, palabra clave y fecha.
* **Sistema de Venta:** Compra de tickets transaccional con control de concurrencia y validación de *Sold Out*.
* **Arquitectura Limpia:** Uso de DTOs, Mappers (MapStruct) y separación de capas (Controller, Service, Repository).
* **Frontend Integrado:** Interfaz web responsive construida con HTML5, Bootstrap 5 y consumo de API via Fetch.

---

## 🏛️ Modelo de Dominio y Datos

A continuación se muestra el esquema visual de la base de datos:

![Diagrama Entidad-Relación](https://i.imgur.com/oGYLKit.png)

### Relaciones Clave
* **Recinto (1) ↔ (N) Evento:** Un recinto alberga múltiples eventos, pero un evento pertenece a un único recinto.
* **Evento (M) ↔ (N) Artista:** Relación "Many-to-Many" gestionada mediante tabla intermedia. Un evento puede tener varios artistas (teloneros, festivales) y un artista actúa en múltiples eventos.
* **Usuario (1) ↔ (N) Ticket:** Un usuario registrado puede adquirir múltiples entradas.
* **Evento (1) ↔ (N) Ticket:** Control de inventario y aforo mediante la relación de tickets vendidos por evento.

### Entidades Principales
| Entidad | Descripción |
| :--- | :--- |
| **Evento** | Núcleo del sistema. Contiene título, fecha, precio, imagen y *ticketmasterId* para evitar duplicados. |
| **Recinto** | Lugar físico del evento. Incluye ciudad y **aforo máximo** (crítico para la lógica de venta). |
| **Ticket** | Representa la compra. Incluye un **UUID único** y fecha de transacción exacta. |
| **Usuario** | Cliente de la plataforma identificado por email único. |

---

## 📋 Reglas de Negocio Implementadas

El sistema aplica las siguientes reglas lógicas en sus servicios y repositorios:

### 1. Gestión de Ventas y Aforo
* **Control de Sold Out:** Antes de generar un ticket, el sistema consulta el conteo de entradas vendidas (`countByEventoId`). Si `vendidas >= aforoMaximo` del recinto, se bloquea la transacción lanzando una `SoldOutException` (HTTP 400).
* **Identificador Único:** Cada ticket generado recibe un código UUID v4 para garantizar su unicidad universal.
* **Validación:** No se permite la compra si el usuario o el evento no existen en la base de datos.

### 2. Sincronización con Ticketmaster
* **Unicidad:** Se evita la duplicidad de eventos verificando el `ticketmasterId` antes de insertar.
* **Persistencia Inteligente:**
    * Si un **Recinto** importado ya existe (por nombre), se reutiliza; si no, se crea con un aforo por defecto (5,000 pax).
    * Si un **Artista** ya existe, se reutiliza; si no, se crea (asignando género "General" si la API no lo provee).
* **Datos por Defecto:** Si la API externa no provee fecha, se asigna "Hoy + 15 días" para asegurar visibilidad. Precios nulos se convierten a 0.0.

### 3. Búsqueda y Filtrado
* **Histórico:** Por defecto, las búsquedas excluyen eventos pasados (`fechaEvento >= LocalDateTime.now()`).
* **Filtros Dinámicos:** Permite combinaciones opcionales de ciudad, palabra clave (título) y género musical.

---

## 🛠️ Stack Tecnológico

* **Backend:** Java 17, Spring Boot (Web, Data JPA, Validation).
* **Base de Datos:** MySQL 8.0 (Contenerizada en Docker).
* **Mapeo:** MapStruct (para conversión eficiente Entity <-> DTO).
* **Cliente