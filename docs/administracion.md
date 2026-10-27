# Administracion

Primera entrega en `feature/administracion`, dividida en commits revisables:

1. Seguridad: registro siempre USER, rutas administrativas restringidas a ADMIN,
   bloqueo de usuarios inactivos y eliminacion del acceso administrativo a finanzas ajenas.
2. Catalogo: crear, consultar, renombrar y activar/desactivar categorias globales.
3. Usuarios: busqueda paginada y suspension/reactivacion con motivo.
4. Auditoria: registro persistente y consulta paginada de acciones administrativas.

El administrador no puede cambiar roles por API, modificar finanzas ajenas ni
consultar contrasenas o secretos de 2FA. Dashboard y supervision operativa quedan
para una entrega posterior.

## Primera cuenta administrativa

El bootstrap esta deshabilitado por defecto. Para una primera creacion, configurar
`ADMIN_BOOTSTRAP_ENABLED=true`, `ADMIN_BOOTSTRAP_EMAIL` y
`ADMIN_BOOTSTRAP_PASSWORD` (al menos 12 caracteres). Cargar las variables en el
entorno del proceso: Spring no carga automaticamente `.env` al ejecutar Maven.
Deshabilitar el bootstrap despues. No modifica una cuenta existente ni la promueve.
Las credenciales antiguas ya persistidas no se cambian automaticamente.

El registro publico acepta el campo historico `role` por compatibilidad, pero lo
ignora: toda cuenta nueva recibe USER. El campo puede omitirse.
