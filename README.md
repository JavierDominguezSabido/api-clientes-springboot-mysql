# API REST de Clientes con Spring Boot y MySQL

[![CI](https://github.com/JavierDominguezSabido/api-clientes-springboot-mysql/actions/workflows/ci.yml/badge.svg)](https://github.com/JavierDominguezSabido/api-clientes-springboot-mysql/actions/workflows/ci.yml)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.4-4479A1?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-compose-2496ED?logo=docker&logoColor=white)

API REST backend desarrollada en Java con Spring Boot para la gestión de clientes.  
El proyecto permite realizar operaciones CRUD, validar datos, gestionar errores personalizados y almacenar la información en una base de datos MySQL.

Este proyecto forma parte de mi portfolio como **Desarrollador Java Junior / Backend Junior**.

---

## Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- Maven
- Jakarta Validation
- JUnit 5, Mockito y MockMvc (tests)
- H2 en memoria (tests de repositorio)
- Docker y Docker Compose
- GitHub Actions (integración continua)
- Postman
- Git y GitHub

---

## Funcionalidades

- Crear clientes.
- Listar todos los clientes.
- Buscar cliente por ID.
- Actualizar cliente.
- Eliminar cliente.
- Buscar clientes por nombre.
- Validar campos obligatorios.
- Validar formato de email.
- Gestionar errores personalizados (404, 400 y 409).
- Impedir emails repetidos: devuelve `409 Conflict` en lugar de un error 500.
- Devolver `201 Created` al crear un cliente.
- Separar entidad y DTOs para una estructura más limpia.

---

## Estructura del proyecto

```text
src/main/java/com/javi/apiclientes
├── controller
│   └── ClienteController.java
├── dto
│   ├── ClienteRequestDTO.java
│   └── ClienteResponseDTO.java
├── entity
│   └── Cliente.java
├── exception
│   ├── ClienteNoEncontradoException.java
│   └── GlobalExceptionHandler.java
├── repository
│   └── ClienteRepository.java
├── service
│   └── ClienteService.java
└── ApiClientesSpringbootMysqlApplication.java
```

---

## Arquitectura del proyecto

El proyecto sigue una estructura por capas:

```text
Cliente HTTP / Postman
        ↓
Controller
        ↓
Service
        ↓
Repository
        ↓
MySQL
```

### Capas principales

- **Controller:** recibe las peticiones HTTP y devuelve las respuestas.
- **Service:** contiene la lógica principal de la aplicación.
- **Repository:** se encarga del acceso a la base de datos.
- **Entity:** representa la tabla `clientes` en MySQL.
- **DTO:** separa los datos que entran y salen de la API.
- **Exception:** gestiona errores personalizados.

---

## Entidad Cliente

La entidad principal del proyecto es `Cliente`.

Campos:

- `id`
- `nombre`
- `apellidos`
- `email`
- `telefono`
- `fechaRegistro`

Reglas básicas:

- El nombre es obligatorio.
- El email es obligatorio.
- El email debe tener un formato válido.
- El teléfono es opcional.
- La fecha de registro se genera automáticamente.
- El email no debe repetirse.

---

## Base de datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE api_clientes_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

---

## Configuración del proyecto

La conexión se configura con variables de entorno, así que no hay contraseñas escritas en el código:

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/api_clientes_db?...` | URL de conexión a MySQL |
| `DB_USERNAME` | `root` | Usuario de la base de datos |
| `DB_PASSWORD` | *(vacía)* | Contraseña de la base de datos |
| `SHOW_SQL` | `false` | Mostrar las consultas SQL en el log |
| `PORT` | `8080` | Puerto de la API |

---

## Endpoints de la API

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/clientes` | Listar todos los clientes |
| GET | `/api/clientes/{id}` | Buscar cliente por ID |
| POST | `/api/clientes` | Crear un nuevo cliente |
| PUT | `/api/clientes/{id}` | Actualizar un cliente |
| DELETE | `/api/clientes/{id}` | Eliminar un cliente |
| GET | `/api/clientes/buscar?nombre=` | Buscar clientes por nombre |

---

## Ejemplos de uso

### Crear cliente

**POST**

```http
http://localhost:8080/api/clientes
```

Body JSON:

```json
{
  "nombre": "Javier",
  "apellidos": "Dominguez",
  "email": "javier@email.com",
  "telefono": "600123456"
}
```

Respuesta esperada (`201 Created`):

```json
{
  "id": 1,
  "nombre": "Javier",
  "apellidos": "Dominguez",
  "email": "javier@email.com",
  "telefono": "600123456",
  "fechaRegistro": "2026-07-01T19:30:00"
}
```

Si ya existe un cliente con ese email, la API responde `409 Conflict`:

```json
{
  "fecha": "2026-07-01T19:30:00",
  "estado": 409,
  "error": "Email duplicado",
  "mensaje": "Ya existe un cliente con el email: javier@email.com"
}
```

---

### Listar clientes

**GET**

```http
http://localhost:8080/api/clientes
```

Respuesta esperada:

```json
[
  {
    "id": 1,
    "nombre": "Javier",
    "apellidos": "Dominguez",
    "email": "javier@email.com",
    "telefono": "600123456",
    "fechaRegistro": "2026-07-01T19:30:00"
  }
]
```

---

### Buscar cliente por ID

**GET**

```http
http://localhost:8080/api/clientes/1
```

Respuesta esperada:

```json
{
  "id": 1,
  "nombre": "Javier",
  "apellidos": "Dominguez",
  "email": "javier@email.com",
  "telefono": "600123456",
  "fechaRegistro": "2026-07-01T19:30:00"
}
```

---

### Actualizar cliente

**PUT**

```http
http://localhost:8080/api/clientes/1
```

Body JSON:

```json
{
  "nombre": "Javi",
  "apellidos": "Dominguez Sabido",
  "email": "javi.actualizado@email.com",
  "telefono": "611222333"
}
```

Respuesta esperada:

```json
{
  "id": 1,
  "nombre": "Javi",
  "apellidos": "Dominguez Sabido",
  "email": "javi.actualizado@email.com",
  "telefono": "611222333",
  "fechaRegistro": "2026-07-01T19:30:00"
}
```

---

### Eliminar cliente

**DELETE**

```http
http://localhost:8080/api/clientes/1
```

Respuesta esperada:

```text
204 No Content
```

---

### Buscar clientes por nombre

**GET**

```http
http://localhost:8080/api/clientes/buscar?nombre=jav
```

Respuesta esperada:

```json
[
  {
    "id": 1,
    "nombre": "Javier",
    "apellidos": "Dominguez",
    "email": "javier@email.com",
    "telefono": "600123456",
    "fechaRegistro": "2026-07-01T19:30:00"
  }
]
```

---

## Validaciones

El proyecto valida los datos recibidos en las peticiones mediante Jakarta Validation.

Ejemplo de petición incorrecta:

```json
{
  "nombre": "",
  "email": "correo-mal"
}
```

Respuesta esperada:

```json
{
  "fecha": "2026-07-01T19:30:00",
  "estado": 400,
  "error": "Error de validación",
  "campos": {
    "nombre": "El nombre es obligatorio",
    "email": "El email debe tener un formato válido"
  }
}
```

---

## Gestión de errores

Si se intenta buscar, actualizar o eliminar un cliente que no existe, la API devuelve un error personalizado.

Ejemplo:

**GET**

```http
http://localhost:8080/api/clientes/999
```

Respuesta esperada:

```json
{
  "fecha": "2026-07-01T19:30:00",
  "estado": 404,
  "error": "Cliente no encontrado",
  "mensaje": "Cliente no encontrado con id: 999"
}
```

---

## Ejecutar con Docker (recomendado)

Levanta MySQL y la API con un solo comando, sin instalar nada más que Docker:

```bash
git clone https://github.com/JavierDominguezSabido/api-clientes-springboot-mysql.git
cd api-clientes-springboot-mysql
docker compose up --build
```

La API queda disponible en `http://localhost:8080/api/clientes`. La contraseña de MySQL se puede cambiar con la variable `DB_PASSWORD`.

---

## Cómo ejecutar el proyecto sin Docker

1. Clonar el repositorio:

```bash
git clone https://github.com/JavierDominguezSabido/api-clientes-springboot-mysql.git
```

2. Entrar en la carpeta del proyecto:

```bash
cd api-clientes-springboot-mysql
```

3. Crear la base de datos en MySQL:

```sql
CREATE DATABASE api_clientes_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

4. Definir `DB_USERNAME` y `DB_PASSWORD` con los datos de tu MySQL (o usar los valores por defecto).

5. Ejecutar el proyecto:

En Linux/Mac:

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

La API estará disponible en:

```http
http://localhost:8080/api/clientes
```

---

## Tests automáticos

El proyecto tiene 18 tests que se ejecutan en cada push con GitHub Actions:

| Capa | Tipo de test | Qué comprueba |
|---|---|---|
| Servicio | Unitario (JUnit 5 + Mockito) | Lógica de negocio: alta, actualización, borrado, emails duplicados y clientes inexistentes |
| Controlador | `@WebMvcTest` + MockMvc | Códigos HTTP (200, 201, 204, 400, 404, 409) y formato de las respuestas JSON |
| Repositorio | `@DataJpaTest` con H2 | Consultas derivadas, unicidad de email y fecha de registro automática |

Para ejecutarlos en local:

```bash
./mvnw verify
```

Además, los endpoints se han probado manualmente con Postman.

---

## Qué he aprendido con este proyecto

Con este proyecto he practicado:

- Creación de una API REST con Spring Boot.
- Uso de controladores REST.
- Separación del proyecto por capas.
- Conexión con MySQL.
- Uso de Spring Data JPA.
- Creación de repositorios.
- Validación de datos con Jakarta Validation.
- Uso de DTOs para entrada y salida de datos.
- Gestión de excepciones personalizadas.
- Tests unitarios, de controlador y de repositorio.
- Contenerización con Docker y Docker Compose.
- Integración continua con GitHub Actions.
- Pruebas de endpoints con Postman.
- Organización de un proyecto backend para GitHub.

---

## Posibles mejoras futuras

- Añadir búsqueda por email.
- Añadir paginación y ordenación.
- Añadir Swagger/OpenAPI.
- Añadir Spring Security.
- Añadir autenticación con JWT.
- Crear un frontend con Angular o React.

---

## Autor

Proyecto desarrollado por **Javier Domínguez Sabido**.