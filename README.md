# TodoApp - Android (Kotlin + SQLite)

Esta es una aplicación nativa de Android desarrollada en **Kotlin** que implementa un sistema **CRUD** (Create, Read, Update, Delete) básico para gestionar una lista de tareas pendientes.

La persistencia de datos se maneja localmente utilizando **SQLite** nativo, sin librerías ORM externas como Room, para propósitos educativos y de ligereza.

---

## 📋 Funcionalidades

1.  **Guardar (Create):** Permite agregar nuevas tareas mediante un botón flotante y un diálogo emergente.
2.  **Leer (Read):** Muestra todas las tareas guardadas en una lista desplazable (`RecyclerView`).
3.  **Editar (Update):** Permite modificar el texto de una tarea existente pulsando el icono de lápiz.
4.  **Eliminar (Delete):** Permite borrar tareas de la base de datos permanentemente pulsando el icono de basura.

---

## 📂 Estructura del Proyecto y Documentación

A continuación se explica qué hace cada archivo y por qué es necesario.

### 1. Modelo de Datos
#### `Todo.kt`
Es la representación de objeto de una fila de la base de datos.
*   **Función:** Es una `data class` simple.
*   **Propiedades:**
    *   `id`: Identificador único (necesario para encontrar qué fila editar o borrar).
    *   `task`: El texto (String) de la tarea.

### 2. Base de Datos
#### `DatabaseHelper.kt`
Esta clase maneja toda la comunicación con SQLite. Hereda de `SQLiteOpenHelper`.

*   **`onCreate()`:** Se ejecuta una sola vez cuando la app se instala. Aquí se ejecuta el comando SQL `CREATE TABLE`.
*   **`addTodo(task)`:** Recibe un texto, crea un `ContentValues` y hace un `INSERT` en la tabla.
*   **`getAllTodos()`:** Hace una consulta `SELECT *`, recorre el cursor (el iterador de resultados) fila por fila, convierte los datos en objetos `Todo` y devuelve una `List<Todo>`.
*   **`updateTodo(id, newTask)`:** Recibe el ID y el nuevo texto. Ejecuta un `UPDATE` buscando la fila por su ID.
*   **`deleteTodo(id)`:** Ejecuta un comando `DELETE` donde el ID coincida con el seleccionado.

### 3. Interfaz de Usuario (Vistas XML)
Los archivos de diseño definen cómo se ve la app.

*   **`activity_main.xml`:**
    *   Es la pantalla principal.
    *   Contiene el `RecyclerView` (la lista vacía que se llenará dinámicamente).
    *   Contiene el `FloatingActionButton` (el botón flotante "+" en la esquina).
*   **`item_todo.xml`:**
    *   Define el diseño de **una sola fila** de la lista.
    *   Usa un `CardView` para dar efecto de tarjeta.
    *   Contiene el `TextView` (texto de la tarea) y dos `ImageView` (botones de editar y borrar).
*   **`dialog_add_todo.xml`:**
    *   Es el diseño del cuadro de diálogo emergente.
    *   Contiene un `EditText` donde el usuario escribe o edita la tarea. Se reutiliza tanto para crear como para editar.

### 4. Adaptador
#### `TodoAdapter.kt`
Es el intermediario entre la base de datos (la lista de objetos `Todo`) y la vista (`RecyclerView`).

*   **`onCreateViewHolder`:** "Infla" (convierte) el archivo `item_todo.xml` en una vista real de Android.
*   **`onBindViewHolder`:** Asigna los datos a cada fila.
    *   Pone el texto del objeto `Todo` en el `TextView`.
    *   **Configura los Clicks:** Aquí se definen los "listeners". Cuando tocas el botón editar o borrar, el adaptador no borra nada directamente, sino que avisa a la `MainActivity` a través de una *función lambda*.
*   **`refreshData`:** Método auxiliar para recibir una lista nueva de la base de datos y avisar a la vista que debe repintarse.

### 5. Lógica Principal
#### `MainActivity.kt`
Es el punto de entrada de la aplicación y conecta todos los componentes.

1.  **Inicialización:** Crea la instancia de `DatabaseHelper` y configura el `RecyclerView`.
2.  **Configuración del Adaptador:** Instancia el `TodoAdapter` pasándole dos funciones lógicas:
    *   *Qué hacer al editar:* Llama a `showEditDialog`.
    *   *Qué hacer al borrar:* Llama a `deleteTask`.
3.  **Manejo de Diálogos (`AlertDialog`):**
    *   `showAddDialog()`: Muestra el popup vacío para guardar. Llama a `dbHelper.addTodo`.
    *   `showEditDialog()`: Muestra el popup pero precarga el texto actual de la tarea en el input. Llama a `dbHelper.updateTodo`.
4.  **Actualización de UI:** Después de cada operación (guardar, editar, borrar), se llama a `todoAdapter.refreshData(dbHelper.getAllTodos())` para que la lista en pantalla refleje los cambios de la base de datos inmediatamente.

---

## 🚀 Flujo de Ejecución

1.  La app inicia (`onCreate`).
2.  Se piden todos los datos a SQLite (`getAllTodos`).
3.  Se pasan al Adaptador y se muestran en pantalla.
4.  **Si el usuario agrega:**
    *   Clic en FAB -> Diálogo -> Escribir -> Guardar -> `INSERT` en SQL -> Refrescar lista.
5.  **Si el usuario edita:**
    *   Clic en Lápiz -> Diálogo (con texto) -> Modificar -> Actualizar -> `UPDATE` en SQL -> Refrescar lista.
6.  **Si el usuario borra:**
    *   Clic en Basura -> `DELETE` en SQL -> Refrescar lista.

---

## 📦 Requisitos (Dependencies)

Para que el XML funcione correctamente, asegúrate de tener estas dependencias estándar en tu `build.gradle` (Module: app):

```groovy
dependencies {
    implementation("androidx.core:core-ktx:1.x.x")
    implementation("androidx.appcompat:appcompat:1.x.x")
    implementation("com.google.android.material:material:1.x.x") // Para FloatingActionButton y CardView
    implementation("androidx.constraintlayout:constraintlayout:2.x.x")
}
