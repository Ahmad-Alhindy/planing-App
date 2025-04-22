package com.example.planingapp.logic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

// Create a notification channel (do this at app startup)
fun createNotificationChannel(context: Context) {
    // Only needed for Android 8.0 (API level 26) and higher
    val name = "Appointment Reminders"
    val descriptionText = "Notifications for upcoming appointments"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel("APPOINTMENT_CHANNEL", name, importance).apply {
        description = descriptionText
    }
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager?.createNotificationChannel(channel)
}

// Schedule notification for an appointment
fun scheduleAppointmentReminder(context: Context, appointment: Appointment) {
    // Calculate when to show the notification (e.g., 15 minutes before appointment)
    val appointmentDateTime = LocalDateTime.of(appointment.date, appointment.startTime)
    val notificationTime = appointmentDateTime.minusMinutes(1)
    val now = LocalDateTime.now()

    // Only schedule if the notification time is in the future
    if (notificationTime.isAfter(now)) {
        val delayUntilAppointment = ChronoUnit.MILLIS.between(now, notificationTime)

        // Create the work request
        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayUntilAppointment, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(
                "appointmentId" to appointment.id.toLong(),
                "appointmentTitle" to appointment.title
            ))
            .build()
        Log.d("Reminder", "Scheduled")
        WorkManager.getInstance(context).enqueue(notificationWork)
    }
}

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val appointmentId = inputData.getLong("appointmentId", -1)
        val appointmentTitle = inputData.getString("appointmentTitle") ?: "Unknown"
        Log.d("ReminderWorker", "doWork called: id=$appointmentId, title=$appointmentTitle")

        if (appointmentId != -1L) {
            showNotification(appointmentId, appointmentTitle)
        }
        return Result.success()
    }

    private fun showNotification(appointmentId: Long, appointmentTitle: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(applicationContext, "APPOINTMENT_CHANNEL")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Upcoming Appointment")
            .setContentText("You have '$appointmentTitle' in 15 minutes")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Notification disappears when tapped

        notificationManager.notify(appointmentId.toInt(), builder.build())
    }
}
