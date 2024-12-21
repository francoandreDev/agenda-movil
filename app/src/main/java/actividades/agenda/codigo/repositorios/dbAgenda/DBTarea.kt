package actividades.agenda.codigo.repositorios.dbAgenda

import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.modelos.tarea.attributos.Estados
import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper.Companion.COLUMN_TAREA_CATEGORIA
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper.Companion.COLUMN_TAREA_ESTADO
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper.Companion.COLUMN_TAREA_FECHA_FIN
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper.Companion.COLUMN_TAREA_FECHA_INICIO
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper.Companion.COLUMN_TAREA_ID
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper.Companion.COLUMN_TAREA_NOMBRE
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper.Companion.TABLE_TAREA
import android.content.ContentValues
import android.database.Cursor
import java.util.Date

class DBTarea {
    companion object {
        // Métodos para la tabla Tarea
        fun insertarTarea(dbHelper: DBHelper, tarea: Tarea): Long {
            return try {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put(COLUMN_TAREA_ID, tarea.id)
                    put(COLUMN_TAREA_NOMBRE, tarea.nombre)
                    put(COLUMN_TAREA_CATEGORIA, tarea.categoria.name)  // STRING
                    put(COLUMN_TAREA_FECHA_INICIO, tarea.fechaInicio.time)  // LONG
                    put(COLUMN_TAREA_FECHA_FIN, tarea.fechaFin.time)
                    put(COLUMN_TAREA_ESTADO, tarea.estado.name) // STRING
                }
                db.insert(TABLE_TAREA, null, values)
            } catch (e: Exception) {
                -1L
            }

        }

        fun obtenerTodasLasTareas(dbHelper: DBHelper): List<Tarea> {
            val listaTareas = mutableListOf<Tarea>()
            val db = dbHelper.readableDatabase
            val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_TAREA", null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TAREA_ID))
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAREA_NOMBRE))
                    val categoria = Categorias.valueOf(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAREA_CATEGORIA))
                    )
                    val fechaInicio = Date(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TAREA_FECHA_INICIO))
                    )
                    val fechaFin = Date(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TAREA_FECHA_FIN))
                    )
                    val estado = Estados.valueOf(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAREA_ESTADO))
                    )

                    listaTareas.add(Tarea(id, nombre, categoria, fechaInicio, fechaFin, estado))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return listaTareas.toList()
        }

        fun actualizarTarea(dbHelper: DBHelper, tarea: Tarea): Int {
            return try {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put(COLUMN_TAREA_NOMBRE, tarea.nombre)
                    put(COLUMN_TAREA_CATEGORIA, tarea.categoria.name)
                    put(COLUMN_TAREA_FECHA_INICIO, tarea.fechaInicio.time)
                    put(COLUMN_TAREA_FECHA_FIN, tarea.fechaFin.time)
                    put(COLUMN_TAREA_ESTADO, tarea.estado.name)
                }

                val selection = "$COLUMN_TAREA_ID = ?"
                val selectionArgs = arrayOf(tarea.id.toString())

                db.update(TABLE_TAREA, values, selection, selectionArgs)
            } catch (e: Exception) {
                -1
            }
        }

        fun eliminarTarea(dbHelper: DBHelper, tareaId: Int): Int {
            return try {
                val db = dbHelper.writableDatabase
                val selection = "$COLUMN_TAREA_ID = ?"
                val selectionArgs = arrayOf(tareaId.toString())

                db.delete(TABLE_TAREA, selection, selectionArgs)
            } catch (e: Exception) {
                -1
            }
        }

        //Agregado Buscar Categoria
        fun obtenerTareasPorCategoria(dbHelper: DBHelper, categoria: Categorias): List<Tarea> {
            val listaTareas = mutableListOf<Tarea>()
            val db = dbHelper.readableDatabase
            val selection = "$COLUMN_TAREA_CATEGORIA = ?"
            val selectionArgs = arrayOf(categoria.name)

            val cursor: Cursor = db.query(
                TABLE_TAREA,
                null, // Seleccionar todas las columnas
                selection,
                selectionArgs,
                null, null, null
            )

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TAREA_ID))
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAREA_NOMBRE))
                    val categoria = Categorias.valueOf(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAREA_CATEGORIA))
                    )
                    val fechaInicio = Date(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TAREA_FECHA_INICIO))
                    )
                    val fechaFin = Date(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TAREA_FECHA_FIN))
                    )
                    val estado = Estados.valueOf(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAREA_ESTADO))
                    )

                    listaTareas.add(Tarea(id, nombre, categoria, fechaInicio, fechaFin, estado))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return listaTareas.toList()
        }
    }
}