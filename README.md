# Lara 365 — Sistema de Gestión Ecoturística y Fidelización ✈️

Este repositorio contiene el ecosistema digital Lara 365, un proyecto académico diseñado para potenciar el turismo en el Estado Lara. El sistema está compuesto por una aplicación móvil nativa y un backend centralizado para la administración de módulos institucionales y dinámicas de gamificación.

## 🧩 Componentes del Sistema

1. **Backend (Laravel):** API encargada de la persistencia de datos, autenticación segura basada en tokens, control del sistema de rangos (XP) y gestión de los módulos de comercios, rutas y gastronomía.
2. **Frontend Móvil (Kotlin / Jetpack Compose):** Aplicación móvil nativa fluida, adaptada a la nueva identidad corporativa, que consume los servicios de la API de manera asíncrona.

---

## 🛠️ Arquitectura del Entorno y Despliegue Local

El sistema ha sido estructurado y testeado de manera óptima para funcionar en un **entorno local de desarrollo**. 

### ⚙️ Justificación Técnica de la Infraestructura
Originalmente se evaluó el despliegue del backend en un servidor en la nube; sin embargo, para efectos del alcance y la naturaleza de este **proyecto estrictamente académico**, se determinó que la infraestructura local es la opción idónea debido a los siguientes factores:

* **Sostenibilidad Financiera:** Mantener servicios en la nube (vps, bases de datos gestionadas, hosting con soporte técnico para Laravel) implica costos de mantenimiento recurrentes que no se justifican para una fase de evaluación interna y defensa de grado.
* **Control del Entorno de Demostración:** El despliegue local garantiza cero latencia, disponibilidad absoluta durante la defensa frente al jurado y aislamiento total de factores externos de conectividad de red, mitigando riesgos técnicos durante la presentación presencial.
* **Consumo Eficiente de Recursos:** El backend ha sido configurado para responder de manera óptima dentro de la red local, simulando con total precisión el comportamiento que tendría en un servidor de producción real.

---

## 📦 Contenido del Paquete de Entrega

Para facilitar la revisión exhaustiva por parte de los tutores, se adjuntan los siguientes recursos:
* **Código Fuente Completo:** Repositorios del Backend (Laravel) y Frontend (Android).
* **Video Demostrativo de Usuario:** Grabación de pantalla en alta resolución que documenta el flujo completo del sistema: flujo de Login/Registro desde cero, navegación por el menú lateral personalizado, visualización de la tarjeta VIP y consumo en tiempo real de los módulos.
* **Paquete de Instalación (APK):** Archivo listo para ser ejecutado en dispositivos Android de prueba.

---

## 📌 Requisitos para la Ejecución Local

### 💻 Backend (API)
* PHP >= 8.x
* Composer
* Servidor MySQL local (XAMPP / Laragon)
* Visual Studio Code (Laravel) y Android Studio (Kotlin/Jetpack Compose) 

En el archivo adjuntado .rar, también se enviará un instalador de cada uno, En caso de ser muy complejo los pasos de "Configuración del Backend (Laravel)" explicados más adelante, se recomienda ver tutoriales sobre como instalar cada uno.

### 📱 Móvil
* Dispositivo físico Android (SDK mínimo 26) conectado a la misma red local (wifi) que el servidor, o emulador de Android Studio configurado con la dirección IP local correspondiente en el "RetrofitClient".

### 👨‍💻 Configuración del Backend (Laravel)

Para levantar el servidor de la API local, asegúrese de tener instalados **PHP (>= 8.x)** y **Composer** en su dispositivo. Luego, siga estos pasos desde su entorno de desarrollo (ej. Visual Studio Code):

1. **Activar Servidor Local (XAMPP):** Inicie el panel de XAMPP y presione **Start** en los módulos de **Apache** y **MySQL**.
2. **Configurar el Entorno:** Abra la carpeta del proyecto `lara-365-api` en su editor. Verifique que el archivo `.env` tenga configuradas correctamente las credenciales de su servidor MySQL local.
3. **Instalar Dependencias (Opcional):** Si es la primera vez que se ejecuta en esa máquina, abra una terminal en la raíz del proyecto y ejecute:
   composer install
4. **Estructurar la Base de Datos:** En la misma terminal de Visual Studio Code, ejecute el siguiente comando para correr las migraciones, crear la estructura de tablas e ingresar todos los datos iniciales del sistema (seeders):
    php artisan migrate --seed
5. **Encender el Servidor Laravel:** Finalmente, ejecute el comando para desplegar la API en la red local:
    php artisan serve --host=0.0.0.0 --port=8000

### 📝 Revisión de Código Fuente Móvil (Opcional)

En caso de que deseen inspeccionar la arquitectura de software, el flujo de navegación o la implementación de la interfaz gráfica nativa desarrollada en Jetpack Compose, pueden abrir el proyecto directamente en **Android Studio** siguiendo estos pasos:

1. **Abrir el Proyecto:** Iniciar Android Studio, seleccionar la opción *Open* y buscar la carpeta raíz del proyecto de la aplicación móvil (`Lara_365_app` o el nombre que le hayas asignado).
2. **Sincronización de Gradle:** Permitir que el IDE descargue las dependencias y sincronice el proyecto de manera automática (se requiere conexión a internet).
3. **Ubicación de los Composables (Interfaz Gráfica):** Para evaluar la maquetación visual, el nuevo menú lateral corporativo, el sistema de perfiles y la tarjeta de fidelización, el código fuente principal se encuentra estructurado en la siguiente ruta:
   `app/src/main/java/com/nombre_de_paquete/lara365/ui/` (Ajustar nombre de paquete según la real)
4. **Configuración de la IP de la API:** Si desea compilar el código directamente en un emulador o dispositivo físico propio desde el IDE, recuerden modificar la URL base en el objeto del cliente de red (`RetrofitClient`) colocando la dirección IP local de la máquina que está corriendo el backend en Laravel.
