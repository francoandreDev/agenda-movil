package actividades.agenda.codigo.notificaciones

import actividades.agenda.codigo.modelos.tarea.Tarea
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast

class NotificationHelper(private val context: Context) {

    private val CHANNEL_ID = "my_channel_01"

    // Crear un canal de notificación (solo en Android 8.0 o superior)
    fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Mi Canal"
            val descriptionText = "Canal de notificación para mi app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Registrar el canal de notificación en el sistema
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun mostrarNotificacion(context: Context, tarea: Tarea) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Verificamos si la versión de Android es >= 26 (Android 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crear el canal de notificación solo si es necesario (API >= 26)
            val channelId = "my_channel_01"
            val channelName = "Tareas Pendientes"
            val channelDescription = "Canal para notificaciones de tareas pendientes"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            // Solo se crea el canal si no existe previamente
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)

            // Crear la notificación usando el canal
            val notification = Notification.Builder(context, channelId)
                .setContentTitle("Tarea Pendiente: ${tarea.nombre}")
                .setContentText("Tienes una tarea pendiente: ${tarea.nombre}. Categoría: ${tarea.categoria}.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()

            // Mostrar la notificación
            notificationManager.notify(tarea.id, notification)
        } else {
            // Para versiones anteriores a Android 8.0 (API < 26)
            val notification = Notification.Builder(context)
                .setContentTitle("Tarea Pendiente: ${tarea.nombre}")
                .setContentText("Tienes una tarea pendiente: ${tarea.nombre}. Categoría: ${tarea.categoria}.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()

            // Mostrar la notificación sin canal (para API < 26)
            notificationManager.notify(tarea.id, notification)
        }
        val texto = "Tiene una tarea pendiente: ${tarea.nombre}"
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
    }
}
