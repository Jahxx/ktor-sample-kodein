# Ktor + Kodein Example

Este proyecto es una aplicación server-side creada con [Ktor](https://ktor.io/) y [Kodein](https://kodein.org/), diseñada para demostrar el uso de controladores basados en Inyeción de dependencias (DI)

## Características
- **Ktor**: Framework para crear servidores rápidos y escalables en Kotlin. 
- **Kodein**: Proveedor de inyección de dependencias para manejar controladores y repositorios.
- **Rutas con Resources**: Organización de rutas usando anotacione `@Resource`.
- **HTML Dinámico**: Respuestas HTML generadas dínamicamente.

## Endpoints
1. **`GET /users`**: Lista de usuarios con enlaces a sus perfiles.
2. **`Get /users/{name}`**: Muestra información detallada de un usuario por nombre. 