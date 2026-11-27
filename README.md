# API de Reservas para Restaurante

API REST para gestionar reservas de mesas en un restaurante. Permite registrar
clientes, consultar las mesas y crear, modificar o cancelar reservas, validando
que no haya choques de horario ni grupos más grandes de lo que la mesa admite.

## Enunciado

> Un cliente puede hacer una reserva para una mesa en un turno específico.
> La API debe validar que la mesa esté disponible en la fecha y turno
> solicitados. Implementar GET, POST, PUT, DELETE.

## Stack técnico

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 4.1 |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | PostgreSQL |
| Build | Maven (wrapper `./mvnw`) |
| Documentación de la API | springdoc-openapi 3.2 (Swagger UI) |
| Pruebas | JUnit 5, Mockito y MockMvc |

El proyecto vive en la carpeta `ReservationService/`.

## Modelo de dominio

Tres entidades relacionadas entre sí:

```
Customer (1) ──< Reservation >── (1) RestaurantTable
```

- **Customer**: cliente que hace la reserva. Nombre, email (único) y teléfono opcional.
- **RestaurantTable**: mesa del restaurante. Número (único) y capacidad.
- **Reservation**: une a un cliente con una mesa en una fecha y un turno
  (`LUNCH` o `DINNER`). Guarda cuántas personas asistirán.

### Datos iniciales

Al arrancar, si no hay clientes, mesas ni reservas, `DataInitializer` carga datos
de ejemplo: 5 clientes, 5 mesas (capacidades 2, 4, 4, 6 y 8) y 5 reservas en los
tres días siguientes. Si ya existen datos, no inserta nada.

## Arquitectura

El proyecto sigue una organización por capas inspirada en Domain-Driven Design,
dentro del paquete `com.reservation`:

```
com.reservation
├── ReservationServiceApplication
├── domain
│   ├── model            Entidades (Customer, RestaurantTable, Reservation) y el enum Shift.
│   └── repository       Interfaces de repositorio. No dependen de Spring Data.
├── application          Casos de uso (CustomerService, ReservationService)
│   └── dto              Comandos de entrada a los servicios.
├── exception            Excepciones de negocio.
├── infrastructure
│   ├── persistence      Interfaces Spring Data JPA y adaptadores que implementan
│   │                    las interfaces del dominio.
│   └── config           DataInitializer (datos iniciales) y configuración de OpenAPI.
└── api                  Controllers REST, DTOs de entrada/salida y el manejador
    └── dto              global de excepciones (GlobalExceptionHandler).
```

La regla de dependencias es `api → application → domain`, e `infrastructure`
implementa las interfaces que declara `domain`. El dominio no sabe nada de HTTP
ni de JPA.

## Reglas de negocio

- **Disponibilidad**: no puede haber dos reservas para la misma mesa, en la
  misma fecha y el mismo turno. Se valida en el servicio (409) y además está
  protegida por una restricción UNIQUE en la base de datos, por si dos
  peticiones llegan al mismo tiempo.
- **Capacidad**: el tamaño del grupo no puede superar la capacidad de la mesa
  elegida (400 si se excede).
- **Email único de cliente**: no se permiten dos clientes con el mismo email,
  normalizado a minúsculas y sin espacios antes de guardar.
- **Borrado protegido**: no se puede eliminar un cliente que tiene reservas;
  primero hay que cancelarlas.
- **Fecha de la reserva**: debe ser hoy o una fecha futura.
- **Validación de campos**: nombre obligatorio, email con formato válido y
  grupo de al menos 1 persona.

## Endpoints

### Clientes

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/api/customers` | Crear cliente | 201 |
| GET | `/api/customers` | Listar todos los clientes | 200 |
| GET | `/api/customers/{id}` | Ver un cliente | 200 |
| PUT | `/api/customers/{id}` | Modificar un cliente | 200 |
| DELETE | `/api/customers/{id}` | Eliminar un cliente (falla si tiene reservas) | 204 |

### Mesas

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/tables` | Listar todas las mesas | 200 |
| GET | `/api/tables/{id}` | Ver una mesa | 200 |

### Reservas

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/api/reservations` | Crear una reserva | 201 |
| GET | `/api/reservations` | Listar todas las reservas | 200 |
| GET | `/api/reservations/{id}` | Ver una reserva | 200 |
| PUT | `/api/reservations/{id}` | Modificar una reserva | 200 |
| DELETE | `/api/reservations/{id}` | Cancelar una reserva | 204 |

### Salud

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/actuator/health` | Estado de la aplicación y su conexión a la base |

## Documentación interactiva (Swagger)

Con la aplicación en marcha:

- Interfaz Swagger UI: <http://localhost:8080/swagger-ui.html>
- Especificación OpenAPI (JSON): <http://localhost:8080/v3/api-docs>

Desde Swagger UI se pueden probar todos los endpoints sin usar `curl`.

## Ejemplos

**Crear un cliente**

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana Perez","email":"ana@mail.com","phone":"7777-0000"}'
```

**Crear una reserva**

```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"tableId":1,"partySize":2,"date":"2026-12-15","shift":"DINNER"}'
```

**Listar las mesas**

```bash
curl http://localhost:8080/api/tables
```

### Errores

Los errores se devuelven como `ProblemDetail` (RFC 7807):

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "La mesa 1 ya esta reservada el 2026-12-15 en el turno DINNER",
  "instance": "/api/reservations"
}
```

| Código | Cuándo ocurre |
|---|---|
| 400 | Body inválido (validaciones de campo), fecha con formato distinto de `yyyy-MM-dd`, turno distinto de `LUNCH` o `DINNER`, o capacidad de la mesa excedida |
| 404 | El cliente, la mesa o la reserva referenciados no existen |
| 409 | Conflicto: mesa ya reservada, email duplicado, o cliente con reservas al intentar borrarlo |

## Configuración

La aplicación se configura por variables de entorno (ver `application.properties`):

| Variable | Descripción |
|---|---|
| `SERVER_PORT` | Puerto HTTP (8080 por defecto) |
| `DB_HOST` | Host de PostgreSQL |
| `DB_PORT` | Puerto de PostgreSQL (5432 por defecto) |
| `DB_NAME` | Nombre de la base de datos |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `DDL_AUTO` | Estrategia de Hibernate para el esquema (`update` por defecto) |

Las credenciales se pasan siempre por variables de entorno; no deben escribirse
en el repositorio.

La base de datos y el esquema `sproject` deben existir de antemano; Hibernate
crea las tablas pero no la base ni el esquema.

## Ejecutar en local

**Requisitos**: JDK 17 y un PostgreSQL accesible.

```bash
cd ReservationService

export DB_HOST=localhost
export DB_NAME=postgres
export DB_USER=postgres
export DB_PASSWORD=tu-contraseña

./mvnw spring-boot:run
```

## Pruebas

Las pruebas unitarias y de capa web no necesitan base de datos: los servicios se
prueban con repositorios simulados (Mockito) y los controladores con `@WebMvcTest`
y `MockMvc`.

```bash
cd ReservationService
./mvnw test -Dtest='*ServiceTest,*ControllerTest'
```

| Archivo | Pruebas |
|---|---|
| `CustomerServiceTest` | 7 |
| `ReservationServiceTest` | 7 |
| `CustomerControllerTest` | 7 |
| `ReservationControllerTest` | 7 |
| `TableControllerTest` | 3 |

`./mvnw test` sin filtro también ejecuta `ReservationServiceApplicationTests`, que
arranca la aplicación completa y necesita una base de datos configurada.

## Flujo de trabajo del equipo

- `main`: proyecto completo y estable. `develop`: integración de los aportes.
- Cada tarea se trabaja en una rama `feature/nombre-del-cambio` creada desde `develop`.
- El commit lleva el número de la tarea, por ejemplo `feat: agregar TableController (#15)`.
- Los cambios entran a `develop` con un Pull Request que debe revisar al menos
  otro integrante, y la descripción lleva `Closes #N`.

## Pendiente de la Entrega 3

- Docker y Kubernetes.
- Pipeline CI/CD con `act`.
