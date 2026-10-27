# 🪙 Kash API - FinancIA

> **Backend Reactivo de Gestión Financiera Personal Inteligente**  
> Desarrollado con **Spring Boot**, **Project Reactor (WebFlux)**, **R2DBC (PostgreSQL + pgvector)**, **Redis**, **Spring AI (Google Gemini)** y **Arquitectura Hexagonal**.

[![Java](https://img.shields.io/badge/Java-21-orange.svg?logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring WebFlux](https://img.shields.io/badge/Spring%20WebFlux-Reactive-blue.svg)](https://docs.spring.io/spring-framework/reference/web/webflux.html)
[![R2DBC](https://img.shields.io/badge/R2DBC-PostgreSQL-336791.svg?logo=postgresql)](https://r2dbc.io/)
[![Redis](https://img.shields.io/badge/Redis-7.x%20Reactive-red.svg?logo=redis)](https://redis.io/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-Google%20Gemini-4285F4.svg?logo=google)](https://spring.io/projects/spring-ai)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## 📌 Tabla de Contenidos

- [Descripción General](#-descripción-general)
- [Características Principales](#-características-principales)
- [Arquitectura del Sistema](#-arquitectura-del-sistema)
- [Stack Tecnológico](#-stack-tecnológico)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración y Variables de Entorno](#-configuración-y-variables-de-entorno)
- [Instalación y Ejecución](#-instalación-y-ejecución)
  - [1. Infraestructura con Docker Compose](#1-infraestructura-con-docker-compose)
  - [2. Ejecución Local del Backend](#2-ejecución-local-del-backend)
  - [3. Ejecución Completa en Contenedores](#3-ejecución-completa-en-contenedores)
- [Documentación de la API](#-documentación-de-la-api)
  - [Swagger / OpenAPI UI](#swagger--openapi-ui)
  - [Resumen de Endpoints](#resumen-de-endpoints)
- [Notificaciones en Tiempo Real (WebSocket)](#-notificaciones-en-tiempo-real-websocket)
- [OCR Inteligente con Cascada de Modelos IA](#-ocr-inteligente-con-cascada-de-modelos-ia)
- [Base de Datos y Migraciones](#-base-de-datos-y-migraciones)
- [Ejecución de Pruebas](#-ejecución-de-pruebas)

---

## 📖 Descripción General

**Kash API** es el motor backend de **FinancIA**, diseñado desde sus cimientos para operar de forma totalmente no bloqueante y reactiva. Permite a los usuarios administrar sus finanzas personales de manera integral: desde el control de cuentas bancarias y billeteras digitales, hasta el escaneo inteligente de recibos/comprobantes físicos mediante modelos de Inteligencia Artificial (Google Gemini), pasando por la presupuestación con límites asignados por categorías, metas de ahorro con control de progreso, suscripciones recurrentes y notificaciones push en tiempo real.

---

## 🚀 Características Principales

- ⚡ **Stack 100% Reactivo No Bloqueante**: Pipeline reactivo de extremo a extremo impulsado por Spring WebFlux, Project Reactor (`Mono`/`Flux`) y Spring Data R2DBC con pool reactivo.
- 🔐 **Seguridad Avanzada & 2FA (MFA)**:
  - Autenticación sin estado con tokens Bearer JWT.
  - Autenticación de Doble Factor (2FA) basada en TOTP (Google Authenticator) con generación de códigos QR en Base64 (ZXing).
  - Validadores de autorización propietarios para garantizar el aislamiento estricto de recursos por usuario.
- 🤖 **OCR Inteligente de Comprobantes con IA**:
  - Procesamiento y extracción automática de información clave de tickets/recibos (fecha, monto, categoría sugerida, cuenta y detalle).
  - **Estrategia en Cascada de Modelos Gemini**: Mecanismo de contingencia automático (`gemini-3.8-flash` ➡️ `gemini-3.6-flash` ➡️ `gemini-3-flash` ➡️ `gemini-2.5-flash` ➡️ `gemini-3.5-flash-lite`, etc.) para asegurar máxima disponibilidad y sortear límites de cuota/tasa.
  - Almacenamiento multimedia de comprobantes en **Cloudinary**.
- 💳 **Gestión Integral de Cuentas y Transferencias**:
  - Múltiples naturalezas: Efectivo, Bancaria, Billetera Digital, entre otras.
  - Conciliación de saldos en tiempo real y transferencias entre cuentas propias con rollback transaccional ante fallos.
- 📊 **Presupuestos y Control de Consumo**:
  - Períodos de presupuestación configurables con límite total global y límites específicos por categoría.
  - Métricas de consumo acumulado, saldo disponible y detección de sobregiros.
- 🎯 **Metas Financieras y Aportes**:
  - Creación de objetivos financieros con fecha meta y monto objetivo.
  - Cálculo de porcentaje de progreso dinámico vinculado directamente a movimientos financieros registrados.
- 🔁 **Suscripciones y Pagos Recurrentes**:
  - Programación de cobros periódicos (semanales, mensuales, anuales).
  - Monitoreo del día de pago y fecha del próximo corte.
- 📁 **Categorización Flexible y Jerárquica**:
  - Categorías globales del sistema y subcategorías personalizables creadas por cada usuario (`INGRESOS`, `GASTOS`, `AHORRO`).
- 📈 **Dashboard Personalizado**:
  - Reportes de balance global, distribución de gastos, historial filtrado y agregación temporal de ingresos/egresos.
- 🔔 **Notificaciones Push en Tiempo Real**:
  - Canal WebSocket (`/ws/notifications`) para alertas de eventos críticos, confirmaciones de transacciones y estados.

---

## 🏛 Arquitectura del Sistema

El rol administrador y sus rutas, permisos, bootstrap y migracion SQL se describen
en [docs/administracion.md](docs/administracion.md).

El proyecto implementa los principios de **Arquitectura Hexagonal (Puertos y Adaptadores)** y **Diseño Guiado por el Dominio (DDD)**, garantizando un bajo acoplamiento entre la lógica de negocio y las dependencias de infraestructura:

```text
com.financia.kash/
├── auth/                               # Autenticación, JWT, 2FA y seguridad reactiva
│   ├── application/                    # Casos de uso y puertos
│   ├── domain/                         # Modelos y contratos de autenticación
│   └── infrastructure/                 # Controladores REST, filtros y proveedores de seguridad
├── cuenta/                             # Cuentas financieras y transferencias monetarias
│   ├── cuenta/                         # Gestión y saldos de cuentas
│   └── transferencia/                  # Lógica de transferencias entre cuentas
├── movimiento/                         # Núcleo transaccional
│   ├── categoria/                      # Árbol de categorías y subcategorías
│   ├── comprobante/                    # OCR con Spring AI y Cloudinary
│   ├── movimiento/                     # Registro de ingresos, gastos y ahorros
│   └── suscripcion/                    # Pagos recurrentes y suscripciones periódicas
├── meta/                               # Metas financieras y aportes vinculados
├── presupuesto/                        # Presupuestos y límites por categoría
├── dashboard/                          # Agregadores analíticos para el usuario
├── notificacion/                       # WebSocket Handler y despacho de alertas
├── usuario/                            # Ciclo de vida y perfil del usuario
└── shared/                             # Utilidades, configuración global y manejo de excepciones
```

Cada módulo mantiene la estructura interna:
- `domain`: Modelos puros, Value Objects y excepciones del dominio.
- `application`: Casos de uso (`input ports`), puertos hacia el exterior (`output ports`), comandos y servicios.
- `infrastructure`: Adaptadores web (controladores `@RestController`), adaptadores de persistencia (repositorios R2DBC y entidades de base de datos) y mappers (MapStruct).

---

## 🛠 Stack Tecnológico

| Capa / Componente | Tecnología | Propósito |
|---|---|---|
| **Lenguaje** | Java 21 LTS | Tipado fuerte, Virtual Threads, Records, Pattern Matching |
| **Framework Base** | Spring Boot 4.1.0 | Núcleo de la aplicación |
| **Motor Web** | Spring WebFlux & Netty | Servidor HTTP no bloqueante y reactivo |
| **Persistencia Reactiva** | Spring Data R2DBC + r2dbc-pool | Conexión reactiva no bloqueante a la base de datos |
| **Base de Datos** | PostgreSQL 17 + `pgvector` | Base de datos relacional y soporte de vectores |
| **Cache y Estado Temporal** | Redis 7 (Alpine) + Reactive Redis | Caché en memoria, tokens de sesión y WebSocket tickets |
| **Inteligencia Artificial** | Spring AI 2.0 (Google GenAI) | Análisis OCR de recibos con modelos Gemini Flash/Lite |
| **Almacenamiento Multimedia** | Cloudinary HTTP5 | Hosting seguro de imágenes de comprobantes |
| **Seguridad** | Spring Security 6+ & JJWT 0.11.5 | Autenticación Bearer JWT y protección de endpoints |
| **2FA / MFA** | Taimos TOTP & Google ZXing | Generación de secretos TOTP y renderizado de QR |
| **Documentación API** | SpringDoc OpenAPI 3 (WebFlux UI) | Swagger interactivo autogenerado |
| **Mapeo de Datos** | MapStruct 1.6.3 & Lombok | Mapeo eficiente de DTOs y Entities en tiempo de compilación |
| **Contenedores** | Docker & Docker Compose | Aprovisionamiento de base de datos, Redis y despliegue |

---

## 📋 Requisitos Previos

Asegúrate de contar con las siguientes herramientas instaladas en tu entorno de desarrollo:

- **JDK 21** o superior instalado y configurado en el `PATH` (`JAVA_HOME`).
- **Docker** y **Docker Compose** (recomendado para PostgreSQL y Redis).
- **Git** para clonar el repositorio.
- *(Opcional)* Maven instalado, aunque el proyecto incluye el ejecutable de conveniencia **Maven Wrapper** (`mvnw` / `mvnw.cmd`).

---

## ⚙️ Configuración y Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto basándote en la plantilla `.env.example`:

```bash
cp .env.example .env
```

### Tabla de Variables de Entorno

| Variable | Descripción | Valor por Defecto / Ejemplo |
|---|---|---|
| `PROFILE` | Perfil activo de Spring Boot | `dev` / `prod` |
| `SECRET_KEY_SECURITY` | Clave secreta Base64 para firma y validación de tokens JWT | Cadena segura de 256 bits |
| `HOST_DB` | Host de la base de datos PostgreSQL | `localhost` (local) o `postgres` (docker) |
| `PORT_BD` | Puerto de PostgreSQL | `5432` |
| `NAME_DB` | Nombre de la base de datos | `financia` |
| `DB_USERNAME` | Usuario de PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `password` |
| `DB_HEALTH` | Endpoint para validación de salud en docker compose | `http://localhost:8080/actuator/health` |
| `API_KEY_GEMINI` | API Key de Google AI Studio (Gemini) para OCR | `AIzaSy...` |
| `REDIS_HOST` | Host del servidor Redis | `localhost` o `redis` |
| `REDIS_PORT` | Puerto de conexión a Redis | `6379` |
| `REDIS_PASSWORD` | Contraseña de autenticación en Redis | `redispass` |
| `CLOUDINATY_NAME` | Cloud Name de tu cuenta Cloudinary | `tu_cloud_name` |
| `CLOUDINARY_APY_KEY` | API Key de Cloudinary | `123456789012345` |
| `CLOUDINARY_API_SECRET`| API Secret de Cloudinary | `abcde_tu_secreto` |
| `HOST_FRONTEND` | URL del cliente frontend para políticas de CORS | `http://localhost:5173` |

---

## 🏃 Instalación y Ejecución

### 1. Infraestructura con Docker Compose

Levanta PostgreSQL (con extensión `pgvector`) y Redis con un solo comando:

```bash
docker compose up -d postgres redis
```

> **Nota:** El contenedor de base de datos ejecuta automáticamente el script [docker/database/init.sql](file:///d:/utp/9no/full-stack/kash/docker/database/init.sql) durante el primer inicio, creando los tipos ENUM, tablas e índices requeridos.

### 2. Ejecución Local del Backend

Asegúrate de que tus variables de entorno estén cargadas o definidas en tu archivo `.env`.

**En Windows (PowerShell):**
```powershell
# Cargar variables o pasarlas en línea y ejecutar
.\mvnw.cmd spring-boot:run
```

**En Linux / macOS:**
```bash
./mvnw spring-boot:run
```

La aplicación iniciará en el puerto predeterminado: **`http://localhost:8080`**.

### 3. Ejecución Completa en Contenedores

Si deseas empaquetar y ejecutar todo en Docker:

```bash
# Construir la imagen Docker usando el Dockerfile multi-stage
docker build -t financia-kash-api .

# O descomentar el servicio 'spring-boot' en compose.yml y ejecutar:
docker compose up -d --build
```

---

## 📑 Documentación de la API

### Swagger / OpenAPI UI

Una vez iniciada la aplicación, puedes explorar e interactuar con todos los endpoints a través de la interfaz visual de Swagger:

- 🌐 **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- 📄 **OpenAPI Spec (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> Para probar endpoints protegidos en Swagger UI, autentícate en `/api/v1/auth/login`, copia el `accessToken` y presiona el botón **Authorize** en la parte superior derecha ingresando el token en formato `Bearer <tu_token>`.

### Resumen de Endpoints

#### 🔑 Autenticación (`/api/v1/auth`)
| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `POST` | `/api/v1/auth/register` | Registro de nuevos usuarios | ❌ |
| `POST` | `/api/v1/auth/login` | Inicio de sesión (devuelve token o `preToken` si 2FA está activo) | ❌ |
| `POST` | `/api/v1/auth/verify-2fa` | Valida código TOTP usando el `preToken` temporal | ❌ |
| `POST` | `/api/v1/auth/2fa/setup` | Inicia configuración de 2FA y entrega QR en Base64 | ✅ |
| `POST` | `/api/v1/auth/2fa/confirm` | Confirma la activación de 2FA con el código de 6 dígitos | ✅ |

#### 👤 Perfil de Usuario (`/api/v1/users/me`)
| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `GET` | `/api/v1/users/me` | Obtiene el perfil del usuario autenticado | ✅ |
| `PUT` | `/api/v1/users/me/password` | Cambio de contraseña segura | ✅ |
| `DELETE` | `/api/v1/users/me` | Suspende la cuenta (ventana de 30 días antes de eliminación) | ✅ |

#### 💳 Cuentas y Transferencias (`/api/v1/accounts` & `/api/v1/transfers`)
| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `GET` | `/api/v1/accounts/my-accounts` | Lista todas las cuentas activas del usuario | ✅ |
| `GET` | `/api/v1/accounts/my-account/{id}` | Detalle de una cuenta por su identificador | ✅ |
| `POST` | `/api/v1/accounts` | Creación de cuenta (efectivo, banco, billetera, etc.) | ✅ |
| `PUT` | `/api/v1/accounts/my-account/{id}/changeStatus` | Alterna el estado activo/inactivo de una cuenta | ✅ |
| `POST` | `/api/v1/accounts/transfer` | Realiza una transferencia monetaria entre cuentas propias | ✅ |
| `GET` | `/api/v1/transfers/my-transfers` | Historial paginado de transferencias | ✅ |
| `GET` | `/api/v1/transfers/my-transfers/account/{id}` | Transferencias filtradas por cuenta específica | ✅ |
| `PUT` | `/api/v1/transfers/my-transfers/{id}` | Edita el monto o concepto de una transferencia | ✅ |
| `DELETE`| `/api/v1/transfers/my-transfers/{id}` | Anula una transferencia restaurando los saldos | ✅ |

#### 🏷️ Categorías (`/api/v1/categories`)
| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `GET` | `/api/v1/categories/global` | Obtiene las categorías globales del sistema | ❌ |
| `GET` | `/api/v1/categories/of-user` | Lista las categorías y subcategorías personalizadas | ✅ |
| `GET` | `/api/v1/categories/category/{id}` | Detalle de categoría | ✅ |
| `POST` | `/api/v1/categories/of-user` | Registra una nueva categoría o subcategoría | ✅ |
| `PUT` | `/api/v1/categories/of-user/{id}` | Actualiza información o estado de una categoría | ✅ |
| `DELETE`| `/api/v1/categories/{id}` | Elimina una categoría del usuario | ✅ |

#### 💸 Movimientos Financieros (`/api/v1/movements`)
| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `GET` | `/api/v1/movements/my-movements` | Historial paginado con ordenamiento y filtros | ✅ |
| `GET` | `/api/v1/movements/my-movements/{id}` | Detalle de un movimiento transaccional | ✅ |
| `POST` | `/api/v1/movements` | Registra un ingreso, gasto o ahorro | ✅ |
| `POST` | `/api/v1/movements/my-movements/{id}/submit-voucher`| Asocia una imagen o archivo de comprobante al movimiento | ✅ |
| `DELETE`| `/api/v1/movements/my-movements/{id}` | Elimina el movimiento y restaura el saldo en la cuenta | ✅ |

#### 🧾 OCR y Análisis de Comprobantes con IA (`/api/v1/voucher`)
| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `POST` | `/api/v1/voucher/analyzer` | Recibe un archivo (`multipart/form-data`) y extrae los datos del gasto con IA | ✅ |

#### 🔁 Suscripciones y Pagos Periódicos (`/api/v1/subscriptions`)
| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `GET` | `/api/v1/subscriptions/my-subs` | Lista suscripciones y servicios recurrentes activos | ✅ |
| `GET` | `/api/v1/subscriptions/my-subs/{id}` | Consulta de una suscripción específica | ✅ |
| `POST` | `/api/v1/subscriptions` | Registra un nuevo compromiso de pago frecuente | ✅ |
| `PUT` | `/api/v1/subscriptions/my-subs/{id}` | Activa o pausa una suscripción recurrente | ✅ |

#### 📊 Presupuestos (`/api/v1/budgets`)
> Consulta la documentación detallada del módulo en [docs/presupuestos.md](file:///d:/utp/9no/full-stack/kash/docs/presupuestos.md).

| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `POST` | `/api/v1/budgets` | Crea presupuesto con límite global y límites por categoría | ✅ |
| `GET` | `/api/v1/budgets` | Lista los presupuestos del usuario | ✅ |
| `GET` | `/api/v1/budgets/{id}` | Detalle del presupuesto y límites asociados | ✅ |
| `PUT` | `/api/v1/budgets/{id}` | Reemplaza configuración y asignaciones de categorías | ✅ |
| `PATCH`| `/api/v1/budgets/{id}/status` | Activa o desactiva un presupuesto | ✅ |
| `GET` | `/api/v1/budgets/{id}/consumption` | Consulta consumo actual, disponible y excesos totales/categoría | ✅ |

#### 🎯 Metas Financieras y Aportes (`/api/v1/objectives`)
| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `GET` | `/api/v1/objectives` | Lista paginada de metas financieras | ✅ |
| `GET` | `/api/v1/objectives/{id}` | Detalle y porcentaje de avance de una meta | ✅ |
| `POST` | `/api/v1/objectives` | Registra una nueva meta de ahorro | ✅ |
| `PUT` | `/api/v1/objectives/{id}` | Actualiza estado (`INICIADA`, `PAUSADA`, etc.) | ✅ |
| `POST` | `/api/v1/objectives/{id}/contribution` | Registra un aporte monetario a la meta | ✅ |
| `GET` | `/api/v1/objectives/contributions/objective/{id}` | Historial de contribuciones por meta | ✅ |

#### 📊 Dashboard (`/api/v1/user/dashboard`)
| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|:---:|
| `GET` | `/api/v1/user/dashboard?from=YYYY-MM-DD` | Resumen financiero consolidado desde la fecha indicada | ✅ |

---

## 🔔 Notificaciones en Tiempo Real (WebSocket)

El backend expone un canal reactivo de WebSockets autenticado en:
```text
ws://localhost:8080/ws/notifications
```

### Probar Notificaciones con el Cliente Web Incluido

En la raíz del proyecto se incluye una interfaz web lista para usar: [notification.html](file:///d:/utp/9no/full-stack/kash/notification.html).

1. Abre [notification.html](file:///d:/utp/9no/full-stack/kash/notification.html) en tu navegador preferido.
2. Ingresa tu token JWT generado al autenticarte.
3. Conéctate para escuchar en vivo las notificaciones y eventos reactivos del sistema.

---

## 🤖 OCR Inteligente con Cascada de Modelos IA

El servicio [VoucherService](file:///d:/utp/9no/full-stack/kash/src/main/java/com/financia/kash/movimiento/comprobante/application/service/VoucherService.java) integra **Spring AI** con Google Gemini. Para garantizar alta disponibilidad frente a posibles cuotas agotadas o variaciones de latencia, implementa un algoritmo de cascada configurable en `application-dev.yml` y `application-prod.yml`:

```yaml
app:
  ai:
    models-cascade:
      - gemini-3.8-flash
      - gemini-3.6-flash
      - gemini-3-flash
      - gemini-2.5-flash
      - gemini-3.5-flash-lite
      - gemini-3.1-flash-lite
      - gemini-2.5-flash-lite
```

Si el modelo principal experimenta fallos o límites de tasa, la petición automáticamente intenta con el siguiente modelo de respaldo hasta procesar exitosamente la imagen del comprobante.

---

## 🗄️ Base de Datos y Migraciones

- **Inicialización Limpia**:  
  El script [docker/database/init.sql](file:///d:/utp/9no/full-stack/kash/docker/database/init.sql) genera el esquema base completo con tipos `ENUM`, tablas con restricciones de integridad referencial, índices de alto rendimiento y activación de la extensión `vector`.

- **Migraciones Incrementales**:  
  Los cambios de versión de esquema se ubican en `docker/database/migrations/` (por ejemplo, [001_presupuestos.sql](file:///d:/utp/9no/full-stack/kash/docker/database/migrations/001_presupuestos.sql)), los cuales pueden aplicarse de manera idempotente sobre bases de datos existentes:
  ```powershell
  psql -h localhost -U postgres -d financia -v ON_ERROR_STOP=1 -f docker/database/migrations/001_presupuestos.sql
  ```

---

## 🧪 Ejecución de Pruebas

El proyecto cuenta con suites de pruebas unitarias y de integración para validar la lógica de negocio, mapeos y casos de uso con Spring Boot Test y WebTestClient.

Para ejecutar todas las pruebas:

**En Windows:**
```powershell
.\mvnw.cmd test
```

**En Linux / macOS:**
```bash
./mvnw test
```

Las pruebas de integración utilizan el perfil `test` configurado en `src/test/resources/application.yml` con simulación (`@MockitoBean`) de clientes externos como `ChatClient` (IA) y `CloudinaryService`.

---

## 👥 Equipo y Créditos

Proyecto desarrollado por el **Equipo FinancIA**.  
*Kash API - Transformando la gestión financiera personal con tecnología reactiva e inteligencia artificial.*
