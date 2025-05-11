package com.example.planingapp.logic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.planingapp.MainActivity
import com.example.planingapp.R
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
fun scheduleAppointmentReminders(context: Context, appointment: Appointment) {
    val appointmentDateTime = LocalDateTime.of(appointment.date, appointment.startTime)
    val now = LocalDateTime.now()

    // Define reminder times (1 day, 1 hour, 15 minutes before)
    val reminderTimes = listOf(
        Pair(1440, "1 day"), // 1 day in minutes
        Pair(60, "1 hour"),  // 1 hour in minutes
        Pair(15, "15 minutes") // 15 minutes
    )

    for ((minutesBefore, reminderLabel) in reminderTimes) {
        val notificationTime = appointmentDateTime.minusMinutes(minutesBefore.toLong())

        // Only schedule if the notification time is in the future
        if (notificationTime.isAfter(now)) {
            val delayUntilAppointment = ChronoUnit.MILLIS.between(now, notificationTime)

            // Create unique work tag to identify this specific reminder
            val workTag = "appointment_${appointment.id}_${reminderLabel}"

            // Create the work request
            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delayUntilAppointment, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(
                    "appointmentId" to appointment.id.toLong(),
                    "appointmentTitle" to appointment.title,
                    "reminderTime" to reminderLabel
                ))
                .addTag(workTag) // Add tag for identification
                .build()

            Log.d("Reminder", "Scheduled $reminderLabel reminder for ${appointment.title}")

            // Use unique work to ensure we don't schedule duplicates
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    workTag,
                    ExistingWorkPolicy.REPLACE,
                    notificationWork
                )
        }
    }
}

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val appointmentId = inputData.getLong("appointmentId", 0)
        val appointmentTitle = inputData.getString("appointmentTitle") ?: "appointment"
        val reminderTime = inputData.getString("reminderTime") ?: "soon" // Default fallback

        showNotification(appointmentId, appointmentTitle, reminderTime)

        return Result.success()
    }

    private fun showNotification(appointmentId: Long, appointmentTitle: String, reminderTime: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Create an intent to open the main activity
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Add navigation data to route to the calendar screen
            putExtra("DESTINATION", "CalendarScreen")
            putExtra("APPOINTMENT_ID", appointmentId)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            appointmentId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(applicationContext, "APPOINTMENT_CHANNEL")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Upcoming Appointment")
            .setContentText("You have '$appointmentTitle' in $reminderTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Notification disappears when tapped
            .setContentIntent(pendingIntent)

        notificationManager.notify(appointmentId.toInt(), builder.build())
    }
}