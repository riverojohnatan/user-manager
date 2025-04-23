# User Service

Este proyecto es un microservicio RESTful construido con Java 11, Spring Boot 2.5.14 y Maven. Permite el registro y autenticación de usuarios, incluyendo generación y validación de tokens JWT.

## Requisitos
- Java 11
- Maven 3.6+

## Construcción y ejecución

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación se ejecutará en `http://localhost:8080`

### Endpoints

#### POST `/api/sign-up`
Crea un nuevo usuario.

##### Request Body:
```json
{
  "name": "Julio Gonzalez",
  "email": "julio@testssw.cl",
  "password": "a2asfGfdfdf4",
  "phones": [
    {
      "number": 87650009,
      "citycode": 7,
      "countrycode": "25"
    }
  ]
}
```

#### GET `/api/login`
Consulta la información del usuario mediante token.
Debe enviar en el header:
```http
Authorization: Bearer <token>
```

### Consola H2
Disponible en: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Contraseña: *(vacío)*

### Cobertura y pruebas
Se utilizan pruebas unitarias con JUnit y Mockito. Cobertura mínima del 80% en servicios.