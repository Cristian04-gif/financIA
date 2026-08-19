# Kash API

Backend de FinancIA para gestion financiera personal. El proyecto usa Spring Boot, PostgreSQL, JPA, seguridad con JWT y una estructura por modulos con enfoque hexagonal.

## Avance de semana 2

Esta rama deja evidencia de la base tecnica inicial:

- Modelo de dominio para usuarios, cuentas, categorias, transacciones, presupuestos y metas financieras.
- Entidades JPA y relaciones principales contra usuarios.
- Puertos, servicios de aplicacion, adaptadores de persistencia y repositorios por modulo financiero.
- Configuracion de PostgreSQL para desarrollo y H2 en memoria para pruebas.
- Swagger/OpenAPI disponible para documentar la API.
- Pruebas de contexto y reglas basicas de dominio.

## Requisitos

- Java 21
- Maven Wrapper incluido en el repositorio
- Docker opcional para levantar PostgreSQL local

## Ejecutar pruebas

```bash
.\mvnw.cmd test
```

Las pruebas usan H2 en memoria desde `src/test/resources/application.yml`, por eso no necesitan una base de datos local.

## Ejecutar la aplicacion

Con PostgreSQL local o Docker levantado:

```bash
.\mvnw.cmd spring-boot:run
```

La configuracion por defecto apunta a:

```text
jdbc:postgresql://localhost:5432/financia
usuario: postgres
password: password
```

Tambien se puede configurar con variables de entorno:

```bash
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/financia"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="password"
.\mvnw.cmd spring-boot:run
```

## PostgreSQL con Docker

```bash
docker compose up -d postgres
```

El script `docker/database/init.sql` crea las bases:

- `financia`
- `financia_it`

## Swagger

Con la aplicacion corriendo:

```text
http://localhost:8080/swagger-ui.html
```

La especificacion OpenAPI queda disponible en:

```text
http://localhost:8080/v3/api-docs
```

## Estructura

Los modulos siguen esta organizacion:

```text
domain/model
application/port/input
application/port/output
application/service
infrastructure/adapter/database
infrastructure/adapter/database/mapping
infrastructure/adapter/database/repository
infrastructure/config
```

Esta estructura separa reglas de dominio, casos de uso y detalles de infraestructura.
