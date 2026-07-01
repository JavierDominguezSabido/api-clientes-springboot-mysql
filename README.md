# API REST de Clientes con Spring Boot y MySQL

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
- Gestionar errores personalizados.
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

Configurar el archivo `src/main/resources/application.properties`:

```properties
spring.application.name=api-clientes-springboot-mysql

spring.datasource.url=jdbc:mysql://localhost:3306/api_clientes_db?useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

server.port=8080
```

> Sustituir `TU_PASSWORD` por la contraseña local de MySQL.

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
  "mensaje": "Cliente no encontrado con ID: 999"
}
```

---

## Cómo ejecutar el proyecto

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

4. Configurar usuario y contraseña en `application.properties`.

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

## Pruebas con Postman

El proyecto ha sido probado con Postman utilizando los métodos:

- GET
- POST
- PUT
- DELETE

Capturas recomendadas para documentar el proyecto:

- Listado de clientes.
- Creación de cliente.
- Actualización de cliente.
- Eliminación de cliente.
- Error de validación.
- Error de cliente no encontrado.

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
- Pruebas de endpoints con Postman.
- Organización de un proyecto backend para GitHub.

---

## Posibles mejoras futuras

- Añadir búsqueda por email.
- Añadir paginación y ordenación.
- Añadir tests unitarios.
- Añadir Swagger/OpenAPI.
- Añadir Spring Security.
- Añadir autenticación con JWT.
- Añadir Docker.
- Crear un frontend con Angular o React.

---

## Autor

Proyecto desarrollado por **Javier Domínguez Sabido**.