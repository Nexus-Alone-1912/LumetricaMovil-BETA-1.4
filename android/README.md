# Lumétrica para Android

## Arquitectura
- `MainActivity`: WebView que carga la app existente publicada en GitHub Pages. Mantiene JavaScript y DOM storage para conservar el comportamiento actual basado en localStorage.
- `NativeWidgetBridge`: interfaz JavaScript mínima. Recibe un JSON resumido, lo guarda en preferencias privadas de Android y solicita refrescar el widget.
- `FinanceWidgetProvider`: AppWidgetProvider con RemoteViews. Presenta saldo, gastos mensuales, próximos pagos, cuentas, ingresos recientes y gastos por categoría.
- Panel web: seis tarjetas minimalistas que leen los mismos datos y funciones de la app, sin sustituir pantallas ni flujos existentes.

## Compilar
Abrir `android/` en Android Studio con JDK 17 y Android SDK 35. Sincronizar Gradle y ejecutar la configuración `app`. Para crear un APK: Build > Build Bundle(s) / APK(s) > Build APK(s).

## Añadir el widget
Instalar la app, mantener pulsada la pantalla de inicio, entrar en Widgets y arrastrar Lumétrica. Abrir la app una vez para publicar el primer resumen al widget.

## Datos y limitaciones
El widget nativo es de lectura y abre la app al tocarlo. Muestra la última instantánea guardada; se refresca al guardar datos desde la app. No se añade backend ni sincronización en la nube. La WebView carga GitHub Pages, por lo que necesita conexión para abrir la app y recibir las actualizaciones publicadas. La gráfica nativa se representa como resumen por categoría; el panel web ofrece las barras visuales. Antes de Play Store faltan firma de publicación, icono definitivo, política de privacidad y pruebas en dispositivos reales.
