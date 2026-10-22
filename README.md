# SafeBox

Aplicación web académica para administrar usuarios y una bóveda de mensajes. Este repositorio presenta el proyecto como una muestra de trabajo práctico con Java, Spring Boot y pruebas básicas de API.

## Mi participación

Desarrollé y probé el proyecto de forma individual. Utilicé ChatGPT como apoyo intensivo para aclarar conceptos, explorar soluciones y revisar código; adapté las propuestas al proyecto, configuré y ejecuté la aplicación, revisé sus pantallas y flujos, y validé el endpoint de salud con Postman.

## Funcionalidades

- Inicio de sesión y autorización por roles con Spring Security.
- Administración de usuarios y mensajes.
- Persistencia con Spring Data JPA y H2 en memoria.
- Cifrado de mensajes con AES-GCM y derivación de claves.
- Registro de eventos de auditoría.
- Endpoint público de diagnóstico: GET /api/health.

Respuesta esperada:

~~~json
{
  "status": "UP",
  "application": "SafeBox"
}
~~~

## Tecnologías

- Java 21
- Spring Boot 3.5.7
- Spring MVC, Spring Security, Spring Data JPA
- Thymeleaf
- H2
- Maven
- Postman para verificar el endpoint de salud

## Ejecutar en Windows

Se requiere un JDK 21. Desde PowerShell, en la carpeta raíz del proyecto:

~~~powershell
$env:SAFEBOX_MASTER_KEY = [guid]::NewGuid().ToString('N')
$env:SAFEBOX_ADMIN_EMAIL = 'admin@localhost'
$env:SAFEBOX_ADMIN_PASSWORD = [guid]::NewGuid().ToString('N')
.\mvnw.cmd spring-boot:run
~~~

Las variables crean una clave y credenciales solo para esa sesión local de PowerShell. Si la base H2 está vacía, la aplicación crea el administrador usando esos valores. No se imprime la contraseña en la consola.

Abrir la aplicación en http://localhost:8080. Probar la API con:

~~~text
GET http://localhost:8080/api/health
~~~

En Postman, la solicitud debe responder 200 OK y el JSON mostrado arriba. El proyecto fue compilado para Java 21; Java 17 no es suficiente.

## Pruebas

El proyecto contiene pruebas de contexto con Spring Boot. Para ejecutarlas en PowerShell, define las variables de entorno indicadas arriba y corre:

~~~powershell
.\mvnw.cmd test
~~~

## Alcance y limitaciones

Es una demostración académica para ejecución local, no una aplicación lista para producción. Usa una base H2 en memoria, por lo que sus datos se reinician al detener la aplicación. La configuración de seguridad simplifica el uso de la consola H2 y desactiva CSRF; no se debe desplegar públicamente sin una revisión y endurecimiento de seguridad.

## Seguridad

- No subir archivos .env, contraseñas, claves, bases locales ni datos personales.
- La clave de cifrado y las credenciales iniciales se leen desde variables de entorno.
- Cambiar los valores locales cada vez que se reinicie el proyecto.

## Autoría y uso de IA

Byron Villalobos Sánchez. Proyecto académico desarrollado individualmente con asistencia de ChatGPT durante el aprendizaje, la exploración de soluciones y la revisión de código.
