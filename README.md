# TodoApp - Android (Kotlin + SQLite)

Esta aplicación es un gestor de tareas (TODO List) nativo para Android escrito en **Kotlin**. Implementa un ciclo completo **CRUD** (Crear, Leer, Actualizar, Eliminar) persistiendo los datos en una base de datos local **SQLite**.

---

## 📂 Documentación Técnica Detallada

A continuación se detalla la función de cada clase y método dentro del proyecto.

### 1. Modelo de Datos (`Todo.kt`)
Este archivo define la estructura de la información.

*   **`data class Todo(val id: Int, val task: String)`**
    *   **Qué hace:** Es un contenedor de datos inmutable.
    *   **Cómo funciona:** Al ser una `data class`, Kotlin genera automáticamente métodos útiles como `toString()` o `equals()`.
    *   **`id`:** Es la clave primaria de la base de datos. Se necesita para identificar qué fila específica queremos borrar o editar.
    *   **`task`:** Es el contenido de texto que el usuario escribió.

---

### 2. Controlador de Base de Datos (`DatabaseHelper.kt`)
Esta clase gestiona toda la interacción SQL. Hereda de `SQLiteOpenHelper`, que es la clase estándar de Android para gestión de bases de datos.

#### Métodos del Ciclo de Vida
*   **`onCreate(db: SQLiteDatabase)`**
    *   **Cuándo se ejecuta:** Solo una vez, cuando la aplicación se instala o se abre por primera vez y la base de datos no existe.
    *   **Qué hace:** Ejecuta el comando SQL `CREATE TABLE todos (id INTEGER PRIMARY KEY AUTOINCREMENT, task TEXT)`. Esto crea la estructura física donde se guardarán los datos.
*   **`onUpgrade(db, oldVersion, newVersion)`**
    *   **Cuándo se ejecuta:** Si cambias la constante `DATABASE_VERSION` en el código.
    *   **Qué hace:** Elimina la tabla existente (`DROP TABLE`) y llama a `onCreate` para recrearla de nuevo. Es útil para migraciones de esquema.

#### Métodos CRUD (Operaciones)
*   **`addTodo(task: String): Long`**
    *   **Propósito:** Guardar una nueva tarea.
    *   **Cómo funciona:**
        1.  Obtiene la base de datos en modo escritura (`writableDatabase`).
        2.  Usa `ContentValues` (un mapa de clave-valor) para emparejar el nombre de la columna (`task`) con el texto recibido.
        3.  Ejecuta `db.insert()`.
    *   **Retorno:** Devuelve el ID de la fila insertada o -1 si hubo error.

*   **`getAllTodos(): List<Todo>`**
    *   **Propósito:** Leer todas las tareas para mostrarlas en la lista.
    *   **Cómo funciona:**
        1.  Obtiene la base de datos en modo lectura (`readableDatabase`).
        2.  Ejecuta `rawQuery("SELECT * FROM todos", null)`. Esto devuelve un objeto `Cursor` (un puntero que recorre los resultados).
        3.  Usa un bucle `while (cursor.moveToNext())` para ir fila por fila.
        4.  En cada vuelta, extrae el `Int` del ID y el `String` de la tarea, crea un objeto `Todo` y lo añade a una lista `ArrayList`.
    *   **Retorno:** Una lista completa de objetos `Todo`.

*   **`updateTodo(id: Int, newTask: String): Int`**
    *   **Propósito:** Modificar el texto de una tarea existente.
    *   **Cómo funciona:**
        1.  Prepara un `ContentValues` con el nuevo texto.
        2.  Ejecuta `db.update()`.
        3.  **Clave:** Usa la cláusula `WHERE id = ?` para asegurarse de que solo se modifique la fila que tiene ese ID específico.

*   **`deleteTodo(id: Int)`**
    *   **Propósito:** Eliminar una tarea.
    *   **Cómo funciona:** Ejecuta `db.delete()` buscando la fila donde el ID coincida con el parámetro recibido.

---

### 3. Adaptador de Lista (`TodoAdapter.kt`)
El `RecyclerView` no sabe cómo mostrar datos por sí mismo. Este adaptador actúa como puente entre la lista de datos (`List<Todo>`) y el diseño XML (`item_todo.xml`).

*   **Constructor:** Recibe la lista de datos y dos funciones "Lambda" (`onEditClick`, `onDeleteClick`). Estas funciones permiten que el adaptador "avise" a la Activity cuando el usuario toca un botón, sin que el adaptador tenga que saber de bases de datos.

*   **Clase Interna `TodoViewHolder`**
    *   **Qué hace:** Guarda las referencias a los elementos visuales (El texto `tvTask`, el botón `btnEdit`, el botón `btnDelete`) para no tener que buscarlos (`findViewById`) cada vez que se hace scroll, optimizando el rendimiento.

*   **`onCreateViewHolder(...)`**
    *   **Qué hace:** "Infla" el diseño. Toma el archivo XML `item_todo.xml` y lo convierte en objetos View de Java/Kotlin en memoria. Se ejecuta solo unas pocas veces (las suficientes para llenar la pantalla).

*   **`onBindViewHolder(holder, position)`**
    *   **Qué hace:** Se ejecuta **constantemente** mientras haces scroll.
    *   **Funcionamiento:**
        1.  Toma el objeto `Todo` de la posición actual.
        2.  Pone el texto en el `TextView`.
        3.  Asigna los "Listeners" (clics) a los botones.
        4.  Cuando se hace click en Editar, ejecuta `onEditClick(todo)`, pasando el objeto completo.
        5.  Cuando se hace click en Borrar, ejecuta `onDeleteClick(todo.id)`, pasando solo el ID.

*   **`refreshData(newList: List<Todo>)`**
    *   **Qué hace:** Actualiza la lista interna del adaptador con nuevos datos traídos de la BD y llama a `notifyDataSetChanged()`. Esto obliga a la pantalla a redibujarse para mostrar los cambios.

---

### 4. Actividad Principal (`MainActivity.kt`)
Es el "cerebro" de la pantalla. Coordina la BD, el Adaptador y los Diálogos.

*   **`onCreate()`**
    *   **Inicialización:** Configura el `RecyclerView` y crea la instancia de `DatabaseHelper`.
    *   **Configuración del Adaptador:** Aquí se define **qué pasa** cuando el adaptador reporta un clic.
        *   Si es *Editar* -> Llama a `showEditDialog`.
        *   Si es *Borrar* -> Llama a `deleteTask`.
    *   **Botón Flotante (FAB):** Configura el clic del botón "+" para llamar a `showAddDialog`.

*   **`showAddDialog()`**
    *   **Qué hace:** Crea y muestra una ventana emergente (`AlertDialog`) con un campo de texto vacío.
    *   **Lógica:** Al pulsar "Guardar", valida que el texto no esté vacío y llama a `addTask`.

*   **`showEditDialog(todo: Todo)`**
    *   **Qué hace:** Muestra el mismo diseño de diálogo, pero **precarga** la información.
    *   **Detalle:** `etTask.setText(todo.task)` coloca el texto actual de la tarea en el input para que el usuario lo corrija en lugar de escribirlo desde cero.
    *   **Lógica:** Al pulsar "Actualizar", llama a `updateTask` pasando el ID original y el texto modificado.

*   **`addTask(task: String)`**
    *   Llama al método `addTodo` de la base de datos.
    *   Si tiene éxito, llama a `todoAdapter.refreshData` obteniendo la lista nueva (`dbHelper.getAllTodos()`). Esto hace que la nueva tarea aparezca instantáneamente.

*   **`updateTask(id: Int, newTask: String)`**
    *   Llama al método `updateTodo` de la base de datos.
    *   Refresca el adaptador para que el cambio de texto se vea en la lista.

*   **`deleteTask(id: Int)`**
    *   Llama al método `deleteTodo` de la base de datos usando el ID.
    *   Refresca el adaptador. La tarea desaparece visualmente de la lista.

---

## 🎨 Archivos XML (Vistas)

*   `activity_main.xml`: Contenedor principal (`ConstraintLayout`). Define la posición de la lista (`RecyclerView`) y fija el botón flotante en la esquina inferior derecha.
*   `item_todo.xml`: Define la estética de cada fila. Usa `LinearLayout` horizontal para alinear: [Texto ... Botón Editar ... Botón Borrar].
*   `dialog_add_todo.xml`: Es el contenido interno de las ventanas emergentes. Solo contiene un título y un `EditText`.
