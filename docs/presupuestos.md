# Presupuestos

El modulo `com.financia.kash.presupuesto` implementa las tablas `presupuestos` y
`presupuesto_categorias` del diagrama v3. El codigo conserva la separacion entre
dominio, casos de uso y adaptadores WebFlux/R2DBC.

## Base de datos

Los campos SQL siguen el diagrama, incluyendo `periodo_inicial` y `fecha_actualizado`.
Las bases nuevas reciben las tablas desde `docker/database/init.sql`.
En una base existente, aplicar:

```powershell
psql -h localhost -U postgres -d financia -v ON_ERROR_STOP=1 -f docker/database/migrations/001_presupuestos.sql
```

El script incremental tiene su propia transaccion y puede ejecutarse nuevamente.
El script de inicializacion requiere pgvector, como el resto del proyecto.
Modificar `init.sql` no actualiza por si solo un volumen existente de Docker.

## API

Todas las rutas requieren un token Bearer JWT. El propietario se obtiene de la
identidad autenticada; no se recibe un `userId` en la peticion.
Un usuario suspendido o inactivo recibe 403. Consultar o modificar un presupuesto
ajeno devuelve 404, igual que un presupuesto inexistente.

| Metodo | Ruta | Operacion |
|---|---|---|
| POST | /api/v1/budgets | Crear presupuesto y asignaciones |
| GET | /api/v1/budgets | Listar presupuestos del usuario |
| GET | /api/v1/budgets/{id} | Consultar detalle |
| PUT | /api/v1/budgets/{id} | Reemplazar configuracion y asignaciones |
| PATCH | /api/v1/budgets/{id}/status | Establecer estado activo/inactivo |
| GET | /api/v1/budgets/{id}/consumption | Consultar consumo |

Ejemplo de POST y PUT:

```json
{
  "name": "Gastos de octubre",
  "periodStart": "2026-10-01",
  "periodEnd": "2026-10-31",
  "amountLimitTotal": 1500.00,
  "categories": [
    {
      "categoryId": "00000000-0000-0000-0000-000000000001",
      "amountLimit": 500.00
    }
  ]
}
```

La categoria del ejemplo debe reemplazarse por una categoria real, activa, de tipo
`GASTOS` y global o del usuario. Se permite `categories: []`.
PUT reemplaza la lista completa de asignaciones; los identificadores de las
asignaciones pueden cambiar. El estado se modifica exclusivamente con PATCH:

```json
{ "active": false }
```

Los periodos incluyen las fechas inicial y final. Se permiten periodos
superpuestos: cada presupuesto calcula su consumo independientemente.
Los limites deben ser positivos y admitir como maximo dos decimales.
Una categoria no puede repetirse y la suma asignada no puede superar el limite total.

El consumo usa movimientos del usuario de tipo `EGRESO`, por `fecha_emision`.
El total incluye categorias sin asignacion. Cada asignacion incluye solamente la
categoria exacta, sin sumar sus categorias hijas. Crear o modificar un presupuesto
no altera cuentas ni movimientos.

La respuesta de consumo incluye `spent`, `available` y `exceeded`, tanto en el
total como por categoria. `available` puede ser negativo; `exceeded` es siempre
cero o positivo. Desactivar un presupuesto conserva su historial y permite
consultar su consumo.

## Arquitectura y consistencia

- Dominio: records inmutables y reglas sin Spring, HTTP ni base de datos.
- Aplicacion: puertos reactivos, validacion de categorias/propietario y calculo de consumo.
- Infraestructura: DTO, MapStruct, consultas parametrizadas y entidades R2DBC.
- Escrituras: transaccion reactiva y bloqueo de la cabecera al reemplazar asignaciones.
  Un fallo revierte cabecera y categorias. Cambiar estado conserva las asignaciones.
- Lecturas de un presupuesto: transaccion de solo lectura con `REPEATABLE READ`,
  para que la cabecera y sus categorias pertenezcan a la misma version.
- Las fechas de auditoria se ajustan a la precision de PostgreSQL en el mapper.

## Pruebas

Las pruebas de dominio, aplicacion, API y carga del contexto no requieren una
base disponible ni credenciales de Gemini/Cloudinary:

```powershell
.\mvnw.cmd test
```

Para ejecutar ademas la integracion con PostgreSQL, usar una base destinada a pruebas:

```powershell
$env:BUDGET_TEST_DATABASE_URL = "r2dbc:postgresql://usuario:password@localhost:5432/financia_test"
.\mvnw.cmd test
```

`BudgetPostgresTest` crea un esquema aleatorio, inicializa las tablas y elimina
exclusivamente ese esquema al finalizar. El usuario de pruebas necesita permisos
para crear esquemas. La inicializacion de pruebas omite la extension vector,
que no participa en presupuestos.

Se verifican ownership, usuarios/categorias inactivos, validaciones, consumo cero,
excesos, fechas inclusivas, filtrado por usuario/tipo, rollback de creacion y
actualizacion, precision de fechas y conservacion del estado ante cambios concurrentes.

