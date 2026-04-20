<div align="center">
  
  <img src="https://img.icons8.com/fluency/144/000000/shield.png" alt="NetSentinel Logo">
  
  <h1>🛡️ NetSentinel SIEM</h1>
  <p><em>Plataforma Moderna, Rápida y Segura de Gestión de Eventos y Detección de Amenazas</em></p>

  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
  [![Java 21](https://img.shields.io/badge/Java-21-orange.svg?logo=java)](https://jdk.java.net/21/)
  [![H2 Database](https://img.shields.io/badge/H2-In--Memory-blue.svg?logo=nodedotjs)](#)
  [![Swagger](https://img.shields.io/badge/OpenAPI-3.0-85EA2D.svg?logo=swagger)](https://swagger.io/)
</div>

---

## 📜 Sobre el Proyecto

**NetSentinel SIEM** (Security Information and Event Management) es un backend de recolección y análisis de eventos orientado a ciberseguridad. Proporciona una ingesta ultrarrápida de logs a través de un API RESTful, analizando todo el tráfico a través de un **Motor de Reglas Heurísticas** en tiempo real.

Ideal para interceptar ciberataques como fuerza bruta o inyecciones SQL antes de que escalen, presentando toda la actividad en un espectacular y envolvente Dashboard analítico de grado hacker.

---

## ✨ Características Principales

*   **⚡ Motor de Reglas en Tiempo Real (Threat Detection):**
    *   **💥 Brute Force:** Detecta ráfagas de intentos de inicio de sesión fallidos (`LOGIN_FAILED`).
    *   **💉 SQL Injection:** Analiza el payload de las peticiones buscando comandos o inyecciones destructivas (`SELECT`, `DROP`, `UNION`, `--`).
    *   **🌊 DoS / Flood Attack:** Alerta sobre un número anormalmente alto de peticiones desde una misma IP de origen temporal limit.
    *   **🔭 Endpoint Scanning:** Identifica un exceso de errores `404 Not Found`, previniendo herramientas de escaneo y reconocimiento web.
*   **👁️ Dashboard Visual Autónomo:** Panel UI en "Dark Mode" diseñado con estética Cyber. Totalmente dinámico, no requiere refrescar la página. Estadísticas reactivas de gráficos enriquecidos con datos generados minuto a minuto.
*   **🔒 Seguridad JWT:** Rutas críticas protegidas por implementaciones asíncronas de JSON Web Tokens adaptadas al stack de configuración moderno en Spring Security.
*   **📚 Documentación Automática:** Integración natural con la consola interactiva **Swagger UI / OpenAPI**, lista para usar y testear sin aplicaciones externas.
*   **💾 Zero-Config Database:** Usa el motor de bases de datos *H2 In-Memory*. Se crea localmente en la caché de ejecución: tú solo levantas el proyecto y no te peleas con bases de datos SQL de terceros.

---

## 🛠️ Stack Tecnológico

### Arquitectura Servidor (Backend)
*   **Java 21** ☕
*   **Spring Boot 3.2.x** (Núcleo)
*   **Spring Security & JWT** (Manejo de identidad y filtros)
*   **Spring Data JPA / Hibernate 6** (Capa de abstracciones de bases de datos)
*   **H2 Database Engine** (Persistencia relacional temporal)
*   **SpringDoc OpenAPI v2** (Auto-generador Swagger)

### Capa Analítica y Cliente (Frontend Dashboard)
*   **Thymeleaf** (Motor de plantillas nativo de Spring)
*   **Bootstrap 5** (Rejilla y estilo)
*   **Chart.js** (Representación matemática abstracta / Doughnuts & Lines)
*   **Vanilla JS + Fetch API** (Manejo local asíncrono con *Polling Loop* de 5 segundos)

---

## 🚀 Guía de Instalación (Modo Relámpago)

La filosofía de este proyecto es que puedas correrlo en menos de 1 minuto sin instalar herramientas tediosas de bases de datos. Contiene su propia envoltura de ejecución (Maven Wrapper).

1. **Abre el código en tu directorio local**:
   Asegúrate de estar en la ruta raíz del proyecto (la carpeta donde se encuentra este `README.md`).

2. **Inicia la aplicación local** usando la terminal:
   * En **Windows** (CMD o PowerShell):
     ```cmd
     .\mvnw.cmd clean spring-boot:run
     ```
   * En **Linux o Mac**:
     ```bash
     ./mvnw clean spring-boot:run
     ```

3. **¡Magia!** La base de datos, las utilidades, el volcado analítico, los tokens JWT y las llaves se habrán configurado mediante el inicializador. Una vez veas en terminal `Started NetSentinelApplication`, todo estará corriendo en el puerto **`8080`**.

---

## 🎮 ¿Cómo utilizarlo y atacarlo? (Simulación de Testing)

Queremos que pruebes cómo tus defensas reaccionan bajo fuego real.

### Paso 1: Abre el Centro de Mandos
Accede desde un navegador web al panel principal:
👉 **[http://localhost:8080/dashboard](http://localhost:8080/dashboard)**
> *Tip: Déjalo abierto en un monitor auxiliar o en una pestaña dividida; el panel cuenta con recarga programada cada 5 segundos y los números saltarán mágicamente.*

### Paso 2: Entorno de Ingesta (El atacante)
A través del siguiente enlace podrás lanzar ataques ficticios.
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
> *Busca el botón `Alert Controller` y `Log Ingestion Controller`.*

### Paso 3: Tipos de Disparo

Ve al endpoint de **Ingesta `POST /api/logs/ingest`**, presiona **Try it out** y manda comandos:

*   **🕵️‍♂️ Prueba Brute Force:** Manda un JSON donde el `eventType` sea `"LOGIN_FAILED"`. Haz click al botón azul *Execute* más de 5 veces rápidamente.
*   **💉 Prueba SQL Injection:** Cambia tu `payload` local en el JSON de pruebas y pon una cadena con código de bases de datos dañino cómo: `"GET /users HTTP/1.1 payload= UNION SELECT username, password FROM Admins;"`. Dale a *Execute*.
*   **📡 Prueba de Endpoint Scann:** Configura el `eventType` enviando `"HTTP_404"` y machaca el botón de enviar unas 8 ocasiones seguidas.

**Mira directamente tu pestaña de Dashboard.** El *Polling Script* detectará a los cinco segundos la infracción, subirá las métricas de amenaza y representará el tipo de ataque en la gráfica de distribución cibernética de manera visual.

---

<p align="center">
  <i>Construido para hacer más segura la red del mañana.</i> 🦇
</p>
