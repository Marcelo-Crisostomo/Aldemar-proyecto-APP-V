# AquaSample

Guía paso a paso para construir una app Android de **registro y trazabilidad de muestras de choritos** (caso ALDEMAR SpA, asignatura DSY1105), con **Kotlin, Jetpack Compose y arquitectura MVVM**, usando la menor cantidad de código posible.

La app permite a un operador registrar una muestra con foto (sin internet), a un supervisor validarla u observarla, y a un analista consultar el historial y sincronizar con una API de prueba.

## Contenido

- **[GUIA_PROYECTO.md](GUIA_PROYECTO.md)**: la guía completa. Incluye el problema, la arquitectura, el código de los 25 archivos en 6 etapas con puntos de control, una explicación de cada archivo y una sección final de ideas para llevar el proyecto al siguiente nivel.

El proyecto de Android Studio no está en este repositorio: lo construyes tú siguiendo la guía.

## Cómo empezar

1. Instala Android Studio (Ladybug o superior) y JDK 17.
2. Abre [GUIA_PROYECTO.md](GUIA_PROYECTO.md) y sigue las etapas en orden.
3. Usuarios de prueba (clave `1234`): `OP-01`, `OP-02` (operador), `SUP-01` (supervisor), `AN-01` (analista).

## Tecnologías

Kotlin 2.0 · Jetpack Compose + Material 3 · Navigation Compose · ViewModel · Room · Retrofit · Coil · JUnit
