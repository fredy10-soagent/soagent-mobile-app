SO-AGENT — proyecto Android revisado v0.1.2
===========================================

Este ZIP contiene código fuente, no un APK instalable.

OBJETIVO
Aplicación Android con un botón para ejecutar el respaldo de SO-ANDROIDE mediante el servicio RUN_COMMAND de Termux y mostrar el resultado recibido.

SEGURIDAD Y FIRMAS
- La aplicación usa el identificador independiente com.soagent.app.
- No comparte la firma ni el identificador de paquete de Termux.
- No requiere instalar Termux:API.
- El APK de prueba se firmará automáticamente como APK debug durante la compilación.
- No desinstales Termux ni modifiques el horario de respaldo diario de las 03:00.

ARCHIVOS
- app/: código de la aplicación.
- .github/workflows/build.yml: compilación automática en GitHub Actions.
- Archivos Gradle: configuración del proyecto.

PASOS DESDE EL MÓVIL
1. Descargar y extraer este ZIP.
2. Crear un repositorio privado en GitHub.
3. Subir los archivos extraídos conservando las carpetas.
4. Abrir la pestaña Actions y ejecutar el flujo "Construir APK SO-AGENT".
5. Descargar el artefacto SO-AGENT-debug.
6. Extraer app-debug.apk e instalarlo.
7. En Ajustes de Android > Aplicaciones > SO-AGENT > Permisos, conceder "Run commands in Termux environment" si aparece.
8. Abrir SO-AGENT y probar el botón.

La compatibilidad del permiso y el resultado real deberán comprobarse en el teléfono. Este proyecto no se ha compilado ni instalado aún.
