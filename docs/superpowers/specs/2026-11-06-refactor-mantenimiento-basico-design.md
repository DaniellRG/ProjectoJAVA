# Refactor básico del sistema de mantenimiento

## Objetivo y límites

Hacer que el proyecto sea más claro y fácil de explicar con los patrones básicos de Java Swing, JDBC y MySQL que ya aparecen en `iNxthing/Projecto-Java`, sin copiar su código. Se conserva el comportamiento actual y no se añaden dependencias, frameworks ni componentes visuales.

Se revisarán los archivos Java del proyecto y `ProjectoJAVA/BasedeDatosProyecto.sql`. Solo se cambiarán archivos relacionados con una corrección o con una interfaz que deba actualizarse; no se harán cambios cosméticos para tocar todos los archivos.

## Funcionalidad que se conserva

- Inicio de sesión, cierre de sesión y permisos de administrador/técnico.
- Registro, consulta, actualización y eliminación de mantenimientos.
- Informes de equipos pendientes, mantenimientos por técnico y costos por equipo/costo total.
- Tablas y datos de la aplicación, así como la auditoría y las validaciones de la base de datos.
- El proyecto NetBeans/Swing, Java 8, MySQL y los formularios existentes.

## Diseño

### Java y flujo de datos

- `FrmLogin` hará la consulta JDBC de autenticación con `sp_iniciar_sesion`. No habrá una clase u objeto Java `Usuario`; la tabla `usuario` y el procedimiento SQL se mantienen.
- Tras autenticar, `FrmLogin` pasará valores simples (usuario, nombre del rol e indicador de administrador) a `FrmMain`. Este conservará la navegación MDI y pasará el permiso necesario al formulario de mantenimientos. Se eliminará el punto de entrada alternativo de `FrmMain` que crea un usuario de prueba y evita el login.
- Los formularios de mantenimiento e informes ejecutarán las consultas JDBC que necesitan, usando `Conexion` para abrir conexiones. `Mantenimiento` e `ItemCombo` serán objetos de datos simples, sin métodos estáticos de acceso a la base de datos.
- Se usarán las construcciones ya presentes en la referencia: `PreparedStatement`, `CallableStatement`, `ResultSet`, métodos, clases sencillas, `try`/`catch` y cierre de recursos.
- Las ventanas internas conservarán la posibilidad de cerrarse. No se agregarán botones, campos, controles ni cambios de distribución por código; se quitarán las opciones explícitas de minimizar, maximizar y redimensionar.

### MySQL

- La base de datos seguirá siendo responsable de las operaciones y reportes definidos allí; Java llamará las vistas, funciones y procedimientos correspondientes en vez de recalcular los informes en los formularios.
- Se conservarán y alinearán los procedimientos existentes de insertar, actualizar y eliminar mantenimientos (`sp_insertar_mantenimiento`, `sp_actualizar_mantenimiento`, `sp_eliminar_mantenimiento`). No se crearán duplicados. Los formularios los invocarán mediante `CallableStatement`.
- Se mantendrán los procedimientos, vistas, funciones y triggers asociados con las funciones actuales. Los objetos no usados no se eliminarán sin confirmar que no sean requisitos de la entrega.
- La creación/búsqueda de equipos deberá devolver el ID real o reportar el error; nunca devolverá un ID fijo como resultado de una falla.
- El script SQL actualmente elimina y recrea `bd_mantenimiento`. La validación no lo ejecutará contra una base con datos que se deban conservar.

### Errores

- Una falla de conexión o SQL no se presentará como credenciales inválidas, resultado vacío, costo cero ni registro exitoso.
- Las consultas podrán distinguir “sin filas” de “falló la consulta”. Los formularios mostrarán los errores de base de datos con el diálogo existente y solo indicarán éxito cuando la operación se complete.

## Verificación

- Compilar el proyecto desde la raíz con `ant -f ProjectoJAVA/build.xml clean jar`.
- No hay pruebas Java en `ProjectoJAVA/test/`; hacer una verificación manual con una base local desechable: login válido e inválido, permisos por rol, registro/consulta/actualización/eliminación, cada informe y auditoría.
- Si una corrección requiere un control visual nuevo, detenerse y pedir al usuario que lo agregue desde NetBeans antes de implementar el código asociado.
