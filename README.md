# Microservicio de Orden de Pedido

## Descripción

Este microservicio es un componente central en la gestión de órdenes de pedido. Diseñado bajo una Arquitectura Hexagonal y siguiendo los principios SOLID, proporciona una API REST desacoplada y mantenible para crear, consultar, actualizar y eliminar órdenes, interactuando con múltiples sistemas de bases de datos y otros servicios de forma asíncrona.

## Tabla de Contenidos

1.  [Tecnologías](#tecnologías)
2.  [Arquitectura y Diseño](#arquitectura-y-diseño)
3.  [Monitoreo](#monitoreo)
4.  [Endpoints de la API](#endpoints-de-la-api)
5.  [Configuración](#configuración)
6.  [Cómo Construir y Ejecutar](#cómo-construir-y-ejecutar)

## Tecnologías

Este proyecto está construido con un stack de tecnologías moderno basado en Java y Spring.

*   **Core Framework:** Spring Boot 3.5.5
*   **Lenguaje:** Java 21
*   **API REST:** Spring Web (con Tomcat embebido).
*   **Cliente HTTP Reactivo:** Spring WebFlux (`WebClient`) para realizar llamadas no bloqueantes a otros servicios.
*   **Persistencia de Datos:**
    *   Spring Data JPA: Para el mapeo objeto-relacional (ORM).
    *   Spring JDBC: Para acceso a bajo nivel y consultas complejas.
    *   Drivers: Conectores para Oracle Database y AS/400 (jt400).
*   **Seguridad:**
    *   Spring Security: Para la gestión de autenticación y autorización.
    *   JSON Web Tokens (JWT): Utilizado para la seguridad a nivel de API (implementado con `com.auth0:java-jwt`).
*   **Herramientas de Desarrollo:**
    *   Lombok: Para reducir el código repetitivo.
    *   MapStruct: Para generar mapeadores de beans de alto rendimiento.
*   **Build Tool:** Apache Maven.

## Arquitectura y Diseño

El diseño del microservicio se fundamenta en principios de software robustos para garantizar la flexibilidad, escalabilidad y mantenibilidad a largo plazo.

### Arquitectura Hexagonal (Puertos y Adaptadores)

El microservicio sigue el patrón de **Arquitectura Hexagonal**. Esto significa que la lógica de negocio del dominio (el "hexágono") está completamente aislada de las preocupaciones externas como la API, la persistencia o los clientes de servicios de terceros.

*   **Puertos (Ports):** Definen interfaces claras que exponen la funcionalidad del núcleo de la aplicación (lógica de negocio). Por ejemplo, un `PedidoServicePort` define las operaciones que se pueden realizar sobre un pedido, sin conocer los detalles de cómo se exponen (REST) o cómo se persisten (JPA).

*   **Adaptadores (Adapters):** Son la implementación de los puertos. Se dividen en dos tipos:
    *   **Adaptadores Primarios (o de Conducción):** Impulsan la aplicación. El controlador REST es un adaptador primario que traduce las solicitudes HTTP en llamadas a los puertos del núcleo de la aplicación.
    *   **Adaptadores Secundarios (o Conducidos):** Son controlados por la aplicación. Las implementaciones de repositorios (usando Spring Data JPA) o el cliente `WebClient` son adaptadores secundarios que implementan los puertos de salida para interactuar con sistemas externos.

Esta separación permite intercambiar tecnologías (por ejemplo, cambiar de una API REST a gRPC, o de una base de datos Oracle a PostgreSQL) con un impacto mínimo en la lógica de negocio.

### Principios SOLID

El código base se adhiere a los principios SOLID para crear un software comprensible, mantenible y flexible.

*   **(S) Principio de Responsabilidad Única:** Cada clase tiene una única razón para cambiar.
*   **(O) Principio de Abierto/Cerrado:** Las entidades de software están abiertas a la extensión, pero cerradas a la modificación.
*   **(L) Principio de Sustitución de Liskov:** Los objetos de una superclase deben poder ser reemplazados por objetos de una subclase sin afectar la corrección del programa.
*   **(I) Principio de Segregación de Interfaces:** Es preferible tener muchas interfaces específicas para un cliente que una sola interfaz de propósito general.
*   **(D) Principio de Inversión de Dependencia:** Los módulos de alto nivel no deben depender de los módulos de bajo nivel. Ambos deben depender de abstracciones.

### Modelo Híbrido (Blocking/Non-Blocking)

El servicio expone una API REST síncrona (blocking) con Spring Web, mientras que utiliza `WebClient` de Spring WebFlux para la comunicación interna asíncrona (non-blocking) con otros servicios, optimizando el uso de recursos.

## Monitoreo

El monitoreo se implementa a través de dos estrategias principales:

1.  **Monitoreo Personalizado con Interceptores:** Se utiliza un `HandlerInterceptor` de Spring para aislar la lógica de monitoreo (métricas, logs, etc.) a través de un contexto, manteniéndola separada de la lógica de negocio.

2.  **Métricas con Spring Boot Actuator:** Se exponen endpoints (`/actuator/health`, `/actuator/metrics`, etc.) para observar la salud y el estado interno del servicio en tiempo real.

## Endpoints de la API

*Aquí puedes detallar los endpoints principales, incluyendo el método HTTP, la ruta, los parámetros y un ejemplo de respuesta.*

## Configuración

Asegúrate de configurar las conexiones a las bases de datos en el archivo `application.properties` o `application.yml`.

```properties
# Configuración de Base de Datos Oracle
spring.datasource.oracle.url=jdbc:oracle:thin:@//host:port/service
spring.datasource.oracle.username=user
spring.datasource.oracle.password=password

# Configuración de Base de Datos AS/400
spring.datasource.as400.url=jdbc:as400://host/schema
spring.datasource.as400.username=user
spring.datasource.as400.password=password

# Propiedades de JWT
jwt.secret=tu-secreto
jwt.issuer=mi-app
```

## Cómo Construir y Ejecutar

1.  Clona este repositorio: `git clone <url-del-repositorio>`
2.  Navega al directorio del proyecto: `cd orden-pedido`
3.  Asegúrate de tener Java 21 y Maven instalados.
4.  Configura las propiedades de la aplicación en `src/main/resources/application.properties`.
5.  Ejecuta el siguiente comando en la raíz del proyecto para construir y ejecutar la aplicación:

```bash
./mvnw spring-boot:run
```
