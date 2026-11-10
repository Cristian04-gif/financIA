# Administracion

Primera entrega en `feature/administracion`, dividida en commits revisables:

1. Seguridad: registro siempre USER, rutas administrativas restringidas a ADMIN,
   bloqueo de usuarios inactivos y eliminacion del acceso administrativo a finanzas ajenas.
2. Catalogo: crear, consultar, renombrar y activar/desactivar categorias globales.
3. Usuarios: busqueda paginada y suspension/reactivacion con motivo.
4. Auditoria: registro persistente y consulta paginada de acciones administrativas.

El administrador no puede cambiar roles por API, modificar finanzas ajenas ni
consultar contrasenas o secretos de 2FA. El dashboard agregado se incluye en esta
rama. Supervision de servicios y avisos generales quedan para otra entrega.

## Primera cuenta administrativa

El bootstrap esta deshabilitado por defecto. Para una primera creacion, configurar
`ADMIN_BOOTSTRAP_ENABLED=true`, `ADMIN_BOOTSTRAP_EMAIL` y
`ADMIN_BOOTSTRAP_PASSWORD` (al menos 12 caracteres). Cargar las variables en el
entorno del proceso: Spring no carga automaticamente `.env` al ejecutar Maven.
Deshabilitar el bootstrap despues. No modifica una cuenta existente ni la promueve.
Las credenciales antiguas ya persistidas no se cambian automaticamente.

El registro publico acepta el campo historico `role` por compatibilidad, pero lo
ignora: toda cuenta nueva recibe USER. El campo puede omitirse.

## Categorias globales

Todas las rutas `/api/v1/admin/**` requieren un JWT de un ADMIN activo.

| Metodo | Ruta | Cuerpo / resultado |
|---|---|---|
| GET | /api/v1/admin/categories | Lista global, incluyendo inactivas |
| POST | /api/v1/admin/categories | `{"name":"Alimentos","type":"GASTOS"}` |
| PUT | /api/v1/admin/categories/{id} | `{"name":"Alimentacion"}` |
| PATCH | /api/v1/admin/categories/{id}/status | `{"active":false}` |

Tipos de creacion: INGRESOS, GASTOS, AHORRO. No se permite cambiar el tipo de una
categoria existente para conservar la clasificacion historica. Tampoco se ofrece
eliminacion fisica. Un identificador personal o inexistente devuelve 404.
Las actualizaciones SQL modifican solo nombre o estado, evitando sobrescribir
otros campos ante operaciones concurrentes. Las operaciones esperan la
invalidacion de cache antes de responder.

POST `/api/v1/categories/global` se conserva como alias administrativo de creacion.
Las rutas personales no permiten modificar ni eliminar categorias globales.

## Usuarios

| Metodo | Ruta | Uso |
|---|---|---|
| GET | /api/v1/admin/users | `search`, `status`, `page` (0), `size` (20, maximo 100) |
| GET | /api/v1/admin/users/{id} | Proyeccion de perfil y estado sin credenciales |
| PATCH | /api/v1/admin/users/{id}/status | `{"status":"SUSPENDIDO","reason":"Solicitud de soporte"}` |

La busqueda contiene coincidencias literales en nombre, apellido o correo, sin
distinguir mayusculas; admite hasta 255 caracteres. Orden: fecha de creacion
descendente e ID ascendente. La respuesta incluye content, page, size,
totalElements y totalPages.

Solo se permite alternar ACTIVO/SUSPENDIDO en cuentas USER. No se puede modificar
el propio estado, gestionar otro ADMIN ni reactivar INACTIVO/ELIMINADO: 409.
Un usuario inexistente devuelve 404; motivo vacio, mayor de 500 caracteres o
estado destino no permitido devuelve 400. El cambio SQL no sobrescribe password,
2FA ni rol. Se invalidan las entradas de cache del usuario y su estado.
Los JWT existentes de usuarios suspendidos se rechazan en la siguiente peticion;
una conexion WebSocket previamente abierta no se revoca automaticamente.

## Auditoria y base de datos

Aplicar en bases existentes antes de desplegar esta rama:

```powershell
psql -h localhost -U postgres -d financia -v ON_ERROR_STOP=1 -f docker/database/migrations/002_administracion.sql
```

Las bases nuevas reciben la tabla desde `docker/database/init.sql`. La migracion
es transaccional e idempotente. No se aplica automaticamente a volumenes existentes.

GET `/api/v1/admin/audit` admite `actorId`, `action`, `page` y `size` (maximo 100).
Devuelve actor, accion, recurso afectado, detalle y fecha UTC, sin secretos.
Acciones: CATEGORY_CREATED, CATEGORY_RENAMED, CATEGORY_STATUS_CHANGED,
USER_STATUS_CHANGED. En cambios de estado del usuario, detalle conserva el estado
destino y el motivo. No incluir credenciales ni datos financieros en los motivos.

El cambio y su registro de auditoria usan una misma transaccion R2DBC: si falla
la escritura de auditoria o la invalidacion de cache, se revierte el cambio SQL.
La cache no forma parte de la transaccion de PostgreSQL; entradas invalidadas
pueden volver a cargarse. Las lecturas paginadas usan REPEATABLE READ y consultas
secuenciales para que contenido y conteo correspondan a la misma vista.
La API no ofrece editar ni eliminar auditoria. No se trata de una auditoria de
lecturas, intentos fallidos ni operaciones historicas previas a esta entrega.

## Validacion

```powershell
.\mvnw.cmd clean test
```

Las pruebas normales no requieren servicios externos. La integracion de administracion
se activa con `ADMIN_TEST_DATABASE_URL` apuntando a una base PostgreSQL de pruebas;
crea un esquema aleatorio y elimina solo ese esquema al terminar.
No ejecutar contra una base de produccion.

## Metricas de OCR

Aplicar `docker/database/migrations/003_dashboard_administrativo.sql` en bases
existentes antes de desplegar las metricas. Las bases nuevas incluyen la tabla
`metricas_ocr_diarias` en init.sql. El script es transaccional e idempotente.

Cada solicitud OCR completada registra un exito o fallo, despues de resolver la
cascada de modelos y mapear el resultado. Un reintento de modelo no cuenta como
otra solicitud. No se cuentan conexiones canceladas ni errores de validacion del
archivo anteriores al caso de uso. No se almacenan imagenes, respuestas de IA,
identificadores de usuario ni importes en esta tabla.

Los contadores se incrementan mediante UPSERT atomico. La fecha se obtiene al
completar la solicitud en `REPORTING_TIME_ZONE` (America/Lima por defecto).
Un fallo de telemetria se registra en el log sin cambiar el resultado del OCR;
por ello estos contadores son operativos y pueden subestimar el uso durante fallos.
No hay datos historicos anteriores a esta instrumentacion.

## Dashboard administrativo

GET `/api/v1/admin/dashboard?from=2026-10-01&to=2026-10-27` requiere un ADMIN activo.
Fechas ISO YYYY-MM-DD inclusivas, maximo 366 dias, sin fechas futuras. Un rango
invalido devuelve 400. Sin `to` se usa hoy en REPORTING_TIME_ZONE; sin `from`, el
primer dia del mes del fin resuelto. La misma ruta esta documentada en Swagger.

La respuesta contiene `period`, `timeZone`, `generatedAt`, `overview` y
`registrations` (una fila por dia, incluyendo ceros). Overview incluye:

| Grupo | Significado |
|---|---|
| users | Total de cuentas USER y cantidad por estado actual, ADMIN por separado y registros del periodo |
| categories | Categorias globales raiz: total, activas e inactivas |
| budgets | Presupuestos de cuentas USER: total, activos, inactivos y creados durante el periodo |
| goals | Metas de cuentas USER: total, completadas y con fecha_inicio dentro del periodo |
| ocr | Solicitudes exitosas, fallidas y total durante el periodo; incluye solicitudes de ADMIN |

Los totales y estados son una fotografia actual, no una reconstruccion historica
al cierre de `to`. "Activo" describe el estado de la cuenta, no actividad de login.
Registros y serie diaria usan `usuarios.fecha_creacion` de cuentas USER existentes,
incluyendo suspendidas/eliminadas logicamente. Una eliminacion fisica retira ese
registro de la serie. Metas usa la fecha de inicio de negocio porque el esquema
no contiene una fecha de creacion independiente.

`ocr.firstRecordedDate` es la primera fecha que tiene datos en los contadores,
puede quedar fuera del periodo y es null cuando no hay registros; no representa
la fecha del despliegue. El resultado no contiene identidades, importes ni
comprobantes. Las consultas de resumen y serie se ejecutan secuencialmente dentro
de una transaccion de solo lectura REPEATABLE READ, sin cache.

Antes de desplegar, aplicar la migracion 003 indicada arriba. No es necesario
configurar Gemini para consultar el dashboard; las metricas OCR empiezan a
acumularse cuando se utiliza el analizador. Esta entrega agrega la API backend;
el repositorio no contiene una interfaz frontend administrativa.
