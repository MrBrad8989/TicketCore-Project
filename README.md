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

## 🛠️ Stack Tecnológico

* **Backend:** Java 17, Spring Boot (Web, Data JPA, Validation).
* **Base de Datos:** MySQL 8.0 (Contenerizada en Docker).
* **Mapeo:** MapStruct (para conversión eficiente Entity <-> DTO).
* **Cliente HTTP:** RestTemplate (para consumo de API Ticketmaster).
* **Frontend:** HTML5, CSS3, Bootstrap 5, JavaScript Vanilla.
* **Herramientas:** Maven, Lombok, Docker Compose.

---

## ⚙️ Instalación y Despliegue

### Prerrequisitos
* Java 17 JDK
* Maven
* Docker y Docker Compose

### 1. Clonar el repositorio
```bash
git clone [https://github.com/tu-usuario/TicketCore-Project.git](https://github.com/tu-usuario/TicketCore-Project.git)
cd TicketCore-Project