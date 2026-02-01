# 🎫 TicketCore Project

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-7.3.1-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4.0-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**Sistema integral de venta de entradas para eventos musicales con sincronización en tiempo real con Ticketmaster API**

[Características](#-características-principales) • [Instalación](#-instalación-rápida) • [Arquitectura](#-arquitectura-del-sistema) • [API Docs](#-documentación-de-la-api) • [Screenshots](#-capturas-de-pantalla)

</div>

---

## 📖 Descripción del Proyecto

**TicketCore** es una plataforma full-stack de gestión y comercio de entradas para eventos musicales que simula un entorno de producción profesional. Combina un robusto backend construido con **Spring Boot 3** y un frontend moderno y reactivo desarrollado en **React 19** con **Vite**.

### 🎯 Objetivos del Sistema

- **Comercio Electrónico Completo**: Carrito de compras, checkout seguro y generación automática de tickets en PDF
- **Integración con API Externa**: Sincronización inteligente con Ticketmaster para poblar la base de datos con eventos reales
- **Gestión Multi-Rol**: Permisos diferenciados para usuarios, empresas organizadoras y administradores
- **Experiencia de Usuario Premium**: Interfaz reactiva con búsqueda avanzada, filtros dinámicos y diseño responsive
- **Arquitectura Escalable**: Diseño modular preparado para crecer y adaptarse a nuevas funcionalidades

---

## 🌟 Características Principales

### 👤 Para Usuarios (Clientes)

#### 🔍 Búsqueda y Exploración
- **Motor de búsqueda avanzado** con filtros por:
  - 🏙️ Ciudad (Madrid, Barcelona, Valencia, Bilbao)
  - 🎵 Género musical (Rock, Pop, Jazz, Hip-Hop, etc.)
  - 📅 Fecha de evento
  - 🎤 Nombre del artista o evento
- **Resultados aleatorios** cuando no se especifica ciudad (función de descubrimiento)
- **Paginación inteligente** con navegación fluida
- **Botón "Limpiar filtros"** para resetear búsqueda rápidamente

#### 🛒 Carrito de Compras
- **Persistencia de estado** en tiempo real
- **Gestión dinámica de cantidades** por evento
- **Validación automática de stock** disponible
- **Cálculo instantáneo** del total de compra
- **Previsualización** de tickets antes de pagar

#### 💳 Proceso de Compra
- **Checkout rápido** desde el carrito
- **Datos personalizados por ticket**: Permite comprar múltiples entradas para diferentes personas
- **Información requerida por entrada**:
  - Nombre completo
  - Email
  - Documento de identificación
  - Fecha de nacimiento
- **Autocompletado inteligente**: Si estás logueado, opción de usar tus datos automáticamente
- **Confirmación de pago** con generación inmediata de tickets

#### 🎟️ Gestión de Tickets
- **Generación automática en PDF** con Apache PDFBox
- **Descarga individual o masiva** (ZIP con todos los tickets)
- **Información completa en cada ticket**:
  - Código único QR/Barcode
  - Detalles del evento (título, fecha, lugar)
  - Datos del titular de la entrada
  - Referencia de compra
- **Historial de compras** accesible desde tu perfil

#### 🔐 Autenticación y Seguridad
- **Sistema de login/registro** con validación de credenciales
- **Sesiones persistentes** durante la navegación
- **Roles diferenciados**: Usuario, Empresa, Administrador
- **Protección de datos personales** con validación en backend

### 🏢 Para Empresas Organizadoras

#### 📝 Gestión de Eventos Propios
- **Panel "Mis Eventos"** centralizado con toda tu información
- **Creación de eventos** con formulario completo:
  - Título del evento
  - Ciudad y recinto
  - Fecha y hora
  - Precio de entrada
  - Imagen promocional (URL)
  - Género musical
  - Aforo máximo
- **Edición en tiempo real** con modal intuitivo
- **Eliminación segura** con confirmación
- **Visualización detallada** de todos tus eventos publicados

#### 🎨 Características de Gestión
- **Interfaz dedicada** accesible desde el menú principal
- **Vista tipo tarjetas** con información resumida
- **Botón de actualización manual** para refrescar datos
- **Filtrado y búsqueda** dentro de tus eventos
- **Estadísticas de ventas** (próximamente)

### ⚙️ Para Administradores

#### 🛠️ Control Total del Sistema
- **CRUD completo** de eventos, recintos y artistas
- **Gestión de usuarios** y permisos
- **Sincronización manual** con Ticketmaster API
- **Actualización masiva de precios** con endpoint dedicado
- **Panel de control** con métricas del sistema
- **Vista privilegiada** de todo el inventario
- **Permisos especiales**: Editar y eliminar cualquier evento

#### 🔄 Sincronización con Ticketmaster
- **Importación inteligente** que evita duplicados mediante:
  - Validación de `ticketmasterId` único
  - Detección de nombres similares (recintos y artistas)
  - Control de títulos duplicados por ciudad
- **Generación automática de precios realistas** cuando la API no los proporciona
- **Categorización por género** automática
- **Gestión de aforo** y capacidad de recintos
- **Actualización incremental** sin borrar datos existentes

---

## 🛠️ Stack Tecnológico Detallado

### Backend (API REST)

#### Core Framework
- **Java 17**: LTS con características modernas (Records, Pattern Matching, Text Blocks)
- **Spring Boot 3.4.1**: Framework empresarial con:
  - Spring Web (REST Controllers)
  - Spring Data JPA (Repositorios y consultas)
  - Spring Boot DevTools (Hot reload en desarrollo)
  - Spring Validation (Validación de DTOs)

#### Base de Datos
- **MySQL 8.0**: Sistema de gestión de bases de datos relacional
- **Hibernate 6.6.4**: ORM avanzado con soporte para:
  - Lazy Loading optimizado
  - Caché de segundo nivel
  - Consultas HQL personalizadas
- **Docker Compose**: Contenerización de MySQL en puerto 3307

#### Herramientas y Librerías
- **MapStruct 1.6.3**: Generación automática de mappers Entidad ↔ DTO
- **Lombok 1.18.36**: Reducción de boilerplate (getters, setters, constructores)
- **Apache PDFBox 2.0.27**: Generación dinámica de tickets en PDF
- **SpringDoc OpenAPI 3**: Documentación interactiva de la API (Swagger UI)
- **ModelMapper 3.1.1**: Mapeo flexible de objetos

#### Arquitectura Backend
```
src/main/java/
├── controller/      → REST Controllers (API endpoints)
├── service/         → Lógica de negocio
├── repository/      → Acceso a datos (Spring Data JPA)
├── model/           → Entidades JPA
├── dto/             → Data Transfer Objects
├── mapper/          → Conversores Entity ↔ DTO
├── config/          → Configuración (CORS, Swagger, DataLoader)
└── exception/       → Gestión de errores personalizados
```

### Frontend (SPA)

#### Framework y Build Tools
- **React 19**: Última versión con mejoras de rendimiento y Concurrent Features
- **Vite 7.3.1**: Build tool ultra-rápido con:
  - Hot Module Replacement (HMR) instantáneo
  - Optimización automática de bundles
  - Tree-shaking agresivo
  - Pre-bundling de dependencias
- **React Router 7**: Navegación client-side con:
  - Lazy loading de rutas
  - Parámetros dinámicos
  - Hooks de navegación

#### Estilos y UI
- **Tailwind CSS 4**: Framework utility-first con:
  - JIT (Just-In-Time) compilation
  - Custom theming
  - Responsive design integrado
  - Dark mode ready
- **React Icons**: Librería completa de iconos (Font Awesome, Material, etc.)
- **SweetAlert2**: Alertas y modales elegantes y customizables

#### Estado y Comunicación
- **Axios**: Cliente HTTP con:
  - Interceptores de peticiones/respuestas
  - Cancelación de requests
  - Transformación automática de datos
- **Context API**: Gestión de estado global (AuthContext)
- **React Hooks**: useState, useEffect, useRef, useContext

#### Arquitectura Frontend
```
frontend/src/
├── components/      → Componentes reutilizables
│   ├── Navbar.jsx
│   ├── EventCard.jsx
│   ├── EventModal.jsx
│   ├── CartModal.jsx
│   └── DownloadTicketsButton.jsx
├── pages/           → Páginas principales
│   ├── Home.jsx
│   ├── SearchPage.jsx
│   ├── CreateEvent.jsx
│   ├── MyEvents.jsx
│   └── CompraDetail.jsx
├── context/         → Providers de estado global
│   └── AuthContext.jsx
├── services/        → Comunicación con API
│   └── api.js
└── App.jsx          → Componente raíz y router
```

### Infraestructura

#### Contenedorización
- **Docker Desktop**: Plataforma de contenedores
- **Docker Compose**: Orquestación multi-contenedor
- **MySQL Container**: Base de datos aislada y portable

#### APIs Externas
- **Ticketmaster Discovery API v2**: Integración para eventos reales
  - Endpoint: `https://app.ticketmaster.com/discovery/v2/events.json`
  - Filtros: Ciudad, género musical, fechas
  - Rate limiting: Gestionado con control de peticiones

---

## 🏗️ Arquitectura del Sistema

### Modelo de Capas

```
┌─────────────────────────────────────────┐
│         Frontend (React SPA)            │
│  ┌──────────┐  ┌──────────┐            │
│  │  Pages   │  │Components│            │
│  └────┬─────┘  └────┬─────┘            │
│       │             │                   │
│       └─────┬───────┘                   │
│             │                           │
│      ┌──────▼──────┐                    │
│      │   Services  │ (Axios)            │
│      └──────┬──────┘                    │
└─────────────┼────────────────────────────┘
              │ HTTP/REST
┌─────────────▼────────────────────────────┐
│         Backend (Spring Boot)            │
│  ┌──────────────────────────────┐        │
│  │     Controllers (REST)       │        │
│  └────────────┬─────────────────┘        │
│               │                          │
│  ┌────────────▼─────────────────┐        │
│  │     Services (Business)      │        │
│  └────────────┬─────────────────┘        │
│               │                          │
│  ┌────────────▼─────────────────┐        │
│  │    Repositories (Data)       │        │
│  └────────────┬─────────────────┘        │
└───────────────┼──────────────────────────┘
                │ JPA/Hibernate
┌───────────────▼──────────────────────────┐
│      MySQL Database (Docker)             │
│  ┌────────────────────────────────┐      │
│  │  Tables: evento, usuario,      │      │
│  │  compra, ticket, artista, etc. │      │
│  └────────────────────────────────┘      │
└──────────────────────────────────────────┘
```

### Flujo de Datos: Compra de Tickets

```
1. Usuario añade eventos al carrito
   └→ POST /api/carrito/agregar
      └→ CarritoService crea LineaCarrito
         └→ Valida stock disponible

2. Usuario procede al checkout
   └→ POST /api/compras/directo
      └→ CompraService.crearCompraDirecta()
         ├→ Crea Compra (estado: PENDING)
         ├→ Crea LineaCompra por cada evento
         └→ Mapea compradores a cada línea

3. Usuario confirma el pago
   └→ POST /api/pagos/{id}/confirm
      └→ PagoService.confirmarPago()
         └→ CompraService.confirmarCompra()
            ├→ Cambia estado a PAID
            ├→ Genera Tickets individuales
            ├→ Asocia datos de comprador a cada ticket
            └→ Reduce stock disponible

4. Usuario descarga sus tickets
   └→ GET /api/compras/{id}/zip
      └→ PdfService.generarPdfCompra()
         ├→ Crea PDF por cada ticket
         ├→ Incluye código, evento, titular
         └→ Empaqueta en ZIP
```

### Flujo de Sincronización con Ticketmaster

```
1. Aplicación inicia (DataLoader)
   └→ TicketmasterSyncService.sincronizarEventos()
      ├→ Para cada ciudad: Madrid, Barcelona, Valencia, Bilbao
      │  ├→ GET Ticketmaster API (máx 100 eventos)
      │  ├→ Filtra duplicados por nombre en memoria
      │  └→ Para cada evento:
      │     ├→ Verifica ticketmasterId en BD
      │     ├→ Crea/busca Recinto (por nombre)
      │     ├→ Crea/busca Artistas (por nombre + género)
      │     ├→ Asigna precio (API o genera aleatorio)
      │     └→ Guarda Evento con relaciones
      │
      └→ actualizarPreciosExistentes()
         └→ Actualiza eventos con precio ≤ 5€
            └→ Genera precios realistas por género
```

---

## 🗄️ Modelo de Base de Datos

### Diagrama Entidad-Relación

```
┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│   Usuario   │──1:N──│    Compra    │──1:N──│   Ticket    │
└─────────────┘       └──────────────┘       └─────────────┘
      │                      │                       │
      │                      │                       │
      │                      │                  ┌────▼────┐
      │                      │                  │ Evento  │
      │                      │                  └────┬────┘
      │                      │                       │
      │                 ┌────▼────────┐              │
      │                 │ LineaCompra │              │
      │                 └─────────────┘              │
      │                                              │
      │                                         ┌────▼────┐
      └────────────────────────────────────────│ Recinto │
                                                └─────────┘
                                                     │
                                               ┌─────▼──────┐
                                               │  Artista   │
                                               └────────────┘
```

### Entidades Principales

#### Usuario
```sql
CREATE TABLE usuario (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  rol ENUM('USUARIO', 'EMPRESA', 'ADMIN') NOT NULL,
  empresa_nombre VARCHAR(200),
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Evento
```sql
CREATE TABLE evento (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  titulo VARCHAR(200) NOT NULL,
  fecha_evento DATETIME NOT NULL,
  precio DOUBLE NOT NULL,
  descripcion TEXT,
  image_url VARCHAR(500),
  ticketmaster_id VARCHAR(100) UNIQUE,
  max_entradas INT DEFAULT 0,
  recinto_id BIGINT,
  creador_id BIGINT,
  FOREIGN KEY (recinto_id) REFERENCES recinto(id),
  FOREIGN KEY (creador_id) REFERENCES usuario(id)
);
```

#### Compra
```sql
CREATE TABLE compra (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT,
  fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  total DOUBLE NOT NULL,
  estado ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
  referencia_pago VARCHAR(100) UNIQUE,
  comprador_nombre VARCHAR(200),
  comprador_email VARCHAR(100),
  FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
```

#### Ticket
```sql
CREATE TABLE ticket (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  codigo VARCHAR(50) UNIQUE NOT NULL,
  compra_id BIGINT NOT NULL,
  evento_id BIGINT NOT NULL,
  usuario_id BIGINT,
  comprador_nombre VARCHAR(200),
  comprador_email VARCHAR(100),
  comprador_documento VARCHAR(50),
  comprador_fecha_nacimiento DATE,
  fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (compra_id) REFERENCES compra(id),
  FOREIGN KEY (evento_id) REFERENCES evento(id),
  FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
```

### Relaciones Clave

- **Usuario ↔ Compra**: Un usuario puede tener múltiples compras (1:N)
- **Compra ↔ Ticket**: Una compra genera múltiples tickets (1:N)
- **Evento ↔ Ticket**: Un evento puede tener múltiples tickets vendidos (1:N)
- **Evento ↔ Artista**: Relación N:M (un evento puede tener varios artistas)
- **Evento ↔ Recinto**: Relación N:1 (varios eventos en el mismo recinto)
- **Usuario ↔ Evento** (creador): Una empresa puede crear múltiples eventos (1:N)

---

## 📡 Documentación de la API

### Endpoints Principales

#### 🔍 Eventos

```http
GET /api/eventos/buscar?ciudad={ciudad}&keyword={keyword}&genero={genero}&fechaInicio={fecha}&page={page}&size={size}
```
Busca eventos con filtros opcionales. Soporta paginación.

**Respuesta:**
```json
{
  "content": [
    {
      "id": 1,
      "titulo": "Coldplay Live in Madrid",
      "fechaEvento": "2026-06-15T20:00:00",
      "precio": 42.99,
      "imageUrl": "https://...",
      "recinto": {
        "id": 1,
        "nombre": "WiZink Center",
        "ciudad": "Madrid"
      },
      "artistas": [
        {
          "id": 1,
          "nombre": "Coldplay",
          "genero": "Pop"
        }
      ],
      "creadorId": 2
    }
  ],
  "page": {
    "size": 9,
    "totalElements": 127,
    "totalPages": 15,
    "number": 0
  }
}
```

```http
GET /api/eventos/mis-eventos
```
Obtiene eventos creados por el usuario actual (solo empresas).

**Headers:** `X-User-Id`, `X-User-Rol`

```http
POST /api/eventos
```
Crea un nuevo evento.

**Body:**
```json
{
  "titulo": "Festival Rock 2026",
  "fechaEvento": "2026-07-20T18:00:00",
  "precio": 35.50,
  "imageUrl": "https://...",
  "maxEntradas": 5000,
  "recinto": {
    "ciudad": "Barcelona",
    "nombre": "Palau Sant Jordi",
    "aforoMaximo": 5000
  },
  "artistas": [
    {
      "nombre": "The Killers",
      "genero": "Rock"
    }
  ]
}
```

```http
PUT /api/eventos/{id}
```
Actualiza un evento existente (solo creador o admin).

```http
DELETE /api/eventos/{id}
```
Elimina un evento (solo creador o admin).

#### 🛒 Carrito

```http
GET /api/carrito/{usuarioId}
```
Obtiene el carrito del usuario.

```http
POST /api/carrito/agregar?usuarioId={id}&eventoId={id}&cantidad={n}
```
Añade eventos al carrito.

```http
DELETE /api/carrito/linea/{usuarioId}/{lineaId}
```
Elimina una línea del carrito.

#### 💳 Compras

```http
POST /api/compras/directo
```
Crea una compra directa (sin carrito previo).

**Body:**
```json
{
  "usuarioId": 1,
  "compradorInfo": {
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@example.com",
    "documentoIdentificacion": "12345678A",
    "fechaNacimiento": "1990-05-15"
  },
  "lineas": [
    {
      "eventoId": 1,
      "cantidad": 2,
      "compradores": [
        {
          "nombre": "Juan Pérez",
          "email": "juan@example.com",
          "documentoIdentificacion": "12345678A",
          "fechaNacimiento": "1990-05-15"
        },
        {
          "nombre": "María García",
          "email": "maria@example.com",
          "documentoIdentificacion": "87654321B",
          "fechaNacimiento": "1992-08-20"
        }
      ]
    }
  ]
}
```

```http
POST /api/compras/carrito/{usuarioId}
```
Finaliza compra desde el carrito.

```http
GET /api/compras/{id}
```
Obtiene detalles de una compra específica.

```http
GET /api/compras/{id}/pdf
```
Descarga todos los tickets de una compra en PDF (un solo archivo).

```http
GET /api/compras/{id}/zip
```
Descarga todos los tickets de una compra en ZIP (un PDF por ticket).

#### 💰 Pagos

```http
POST /api/pagos/{compraId}/confirm
```
Confirma el pago y genera los tickets.

**Respuesta:**
```json
{
  "id": 15,
  "compraId": 42,
  "metodoPago": "TARJETA",
  "estado": "COMPLETADO",
  "fechaPago": "2026-02-01T14:30:00"
}
```

#### 👤 Autenticación

```http
POST /api/auth/login
```
Inicia sesión.

**Body:**
```json
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "usuario@example.com",
  "rol": "USUARIO"
}
```

```http
POST /api/auth/register
```
Registra un nuevo usuario.

**Body:**
```json
{
  "nombre": "Juan Pérez",
  "email": "nuevo@example.com",
  "password": "password123",
  "rol": "USUARIO"
}
```

### Swagger UI

Accede a la documentación interactiva completa en:
```
http://localhost:8080/swagger-ui/index.html
```

---

## ⚡ Instalación Rápida

### Prerrequisitos

Asegúrate de tener instalado:
- ☕ **Java 17 JDK** ([Descargar](https://adoptium.net/))
- 📦 **Node.js 18+** y **npm** ([Descargar](https://nodejs.org/))
- 🐳 **Docker Desktop** ([Descargar](https://www.docker.com/products/docker-desktop/))
- 🔧 **Git** ([Descargar](https://git-scm.com/))

### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/TicketCore-Project.git
cd TicketCore-Project
```

### Paso 2: Configurar Base de Datos

El proyecto incluye un `docker-compose.yml` configurado en el puerto **3307** para evitar conflictos con instalaciones locales de MySQL.

```bash
# Iniciar contenedor MySQL
docker-compose up -d

# Verificar que está corriendo
docker ps
```

**Credenciales por defecto:**
- Host: `localhost:3307`
- Usuario: `root`
- Contraseña: `root`
- Base de datos: `ticketcore`

### Paso 3: Configurar API Key de Ticketmaster

1. Obtén tu API Key gratuita en [Ticketmaster Developer Portal](https://developer.ticketmaster.com/)
2. Edita `src/main/resources/application.properties`:

```properties
ticketmaster.api.key=TU_API_KEY_AQUI
```

### Paso 4: Iniciar Backend

```bash
# En la raíz del proyecto

# Con Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Con Maven Wrapper (Linux/Mac)
./mvnw spring-boot:run
```

El backend estará disponible en: `http://localhost:8080`

**Durante el primer inicio:**
- Se crearán automáticamente las tablas en MySQL
- Se sincronizarán eventos desde Ticketmaster (puede tardar 1-2 minutos)
- Se actualizarán precios de eventos existentes

### Paso 5: Instalar Dependencias del Frontend

```bash
cd frontend
npm install
```

### Paso 6: Iniciar Frontend

```bash
# Modo desarrollo (con hot reload)
npm run dev

# O construir para producción
npm run build
```

El frontend estará disponible en: `http://localhost:5173` (o el puerto que indique Vite)

### Paso 7: Acceder a la Aplicación

1. Abre tu navegador en `http://localhost:5173`
2. Registra un usuario o usa las credenciales de prueba
3. ¡Comienza a explorar eventos!

---

## 🔧 Scripts Disponibles

### Backend

```bash
# Compilar sin ejecutar tests
.\mvnw.cmd -DskipTests package

# Ejecutar tests
.\mvnw.cmd test

# Limpiar y compilar
.\mvnw.cmd clean install

# Ejecutar en modo producción
java -jar target/TicketCore-Project-0.0.1-SNAPSHOT.jar
```

### Frontend

```bash
# Desarrollo con hot reload
npm run dev

# Build para producción
npm run build

# Preview del build de producción
npm run preview

# Linting
npm run lint
```

### Docker

```bash
# Iniciar base de datos
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar base de datos
docker-compose down

# Parar y eliminar volúmenes
docker-compose down -v
```

---

## 🎨 Capturas de Pantalla

### 🏠 Página Principal
*Interfaz de bienvenida con eventos destacados y llamada a la acción*

### 🔍 Buscador de Eventos
*Motor de búsqueda con filtros dinámicos por ciudad, género, fecha y artista*

### 🛒 Carrito de Compras
*Vista detallada del carrito con cantidades ajustables y total actualizado*

### 💳 Checkout y Datos de Comprador
*Formulario para ingresar datos de cada titular de entrada*

### 🎟️ Tickets Generados
*PDF profesional con código QR, detalles del evento y datos del titular*

### 🏢 Panel de Empresa
*Vista "Mis Eventos" con opciones de crear, editar y eliminar*

---

## 🧪 Testing

### Backend Tests

```bash
# Ejecutar todos los tests
.\mvnw.cmd test

# Test de integración de compras
.\mvnw.cmd test -Dtest=CompraZipIntegrationTest

# Test de servicios
.\mvnw.cmd test -Dtest=CompraServiceTest
```

### Tests Implementados

- ✅ `CompraServiceTest`: Validación de lógica de compras
- ✅ `CompraZipIntegrationTest`: Test de generación de ZIP con múltiples PDFs
- ✅ `TicketCoreProjectApplicationTests`: Test de contexto de Spring

---

## 📚 Patrones y Buenas Prácticas

### Arquitectura

- ✅ **Arquitectura en Capas**: Controller → Service → Repository
- ✅ **DTO Pattern**: Separación entre entidades de BD y objetos de transferencia
- ✅ **Repository Pattern**: Abstracción del acceso a datos
- ✅ **Dependency Injection**: Gestión de dependencias con Spring IoC

### Backend

- ✅ **RESTful API**: Siguiendo convenciones REST
- ✅ **SOLID Principles**: Código mantenible y escalable
- ✅ **Exception Handling**: Gestión centralizada de errores
- ✅ **Validation**: Validación de datos con Bean Validation
- ✅ **Transaction Management**: Control de transacciones con `@Transactional`

### Frontend

- ✅ **Component-Based**: Componentes reutilizables y modulares
- ✅ **Hooks Pattern**: Uso de hooks personalizados para lógica compartida
- ✅ **Context API**: Gestión de estado global sin Redux
- ✅ **Async/Await**: Manejo moderno de asincronía
- ✅ **Responsive Design**: Mobile-first con Tailwind

---

## 🚀 Próximas Mejoras

### Funcionalidades Planificadas

- [ ] **Pasarela de Pago Real**: Integración con Stripe o PayPal
- [ ] **QR Codes**: Generación de códigos QR únicos por ticket
- [ ] **Email Notifications**: Envío automático de tickets por email
- [ ] **Dashboard de Estadísticas**: Métricas de ventas para empresas
- [ ] **Sistema de Reseñas**: Valoraciones y comentarios de eventos
- [ ] **Chat en Vivo**: Soporte al cliente integrado
- [ ] **Notificaciones Push**: Alertas de eventos próximos
- [ ] **API de Terceros**: Webhook para integraciones externas
- [ ] **Multi-idioma**: Soporte para inglés y español
- [ ] **Dark Mode**: Tema oscuro en toda la aplicación

### Mejoras Técnicas

- [ ] **Redis Cache**: Caché distribuida para mejorar rendimiento
- [ ] **JWT Authentication**: Tokens seguros en lugar de sesiones
- [ ] **Rate Limiting**: Control de peticiones por usuario
- [ ] **CI/CD Pipeline**: Deploy automático con GitHub Actions
- [ ] **Monitoring**: Integración con Prometheus y Grafana
- [ ] **Logging Avanzado**: ELK Stack (Elasticsearch, Logstash, Kibana)
- [ ] **Tests E2E**: Cobertura completa con Selenium/Cypress
- [ ] **Kubernetes**: Orquestación de contenedores para escalabilidad

---

## 🤝 Contribución

¿Quieres contribuir a TicketCore? ¡Genial! Sigue estos pasos:

1. **Fork** el repositorio
2. **Crea una rama** para tu feature (`git checkout -b feature/NuevaCaracteristica`)
3. **Commit** tus cambios (`git commit -m 'Añadir nueva característica'`)
4. **Push** a la rama (`git push origin feature/NuevaCaracteristica`)
5. **Abre un Pull Request**

### Directrices

- Sigue las convenciones de código del proyecto
- Añade tests para nuevas funcionalidades
- Actualiza la documentación si es necesario
- Asegúrate de que todos los tests pasan antes de hacer PR

---

## 🙏 Agradecimientos

- **Ticketmaster API** por proporcionar datos reales de eventos
- **Spring Boot Community** por la excelente documentación
- **React Team** por el increíble framework
- **Tailwind Labs** por revolucionar el CSS

---

<div align="center">

**⭐ Si te ha gustado este proyecto, dale una estrella en GitHub ⭐**

Made with ☕ and 💻 | © 2026 TicketCore Project

</div>
