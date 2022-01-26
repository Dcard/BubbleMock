package tw.dcard.bubblemock.module

import android.app.Notification
import android.app.PendingIntent
import android.app.Person
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import tw.dcard.bubblemock.R
import tw.dcard.bubblemock.sample.screen.BubbleActivity

/**
 * @author Batu
 */
class BubbleBuilder(
    private val context: Context,
    private val shortcutManager: ShortcutManager,
    private val channelId: String
) {

    companion object {
        private const val PERSON_NAME = "Mock Bubble Bot"
        private const val MOCK_TEXT = "Mock Bot"
        private const val MESSAGE = "Click right-bottom icon to launch bubble."
        private const val MESSAGE_FOR_BUBBLE_DISABLE =
            "The bubble setting is not enable in android system."
        private const val SHORTCUT_INFO_ID = "shortcut_info_id"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun build(): Notification {
        val icon = Icon.createWithResource(context, R.drawable.ic_sharp_account_circle_24)
        val notificationBuilder = getNotificationBuilder()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val person = Person.Builder()
                .setName(PERSON_NAME)
                .setIcon(icon)
                .setImportant(true)
                .build()
            val bubbleData = createBubbleMetadata(icon)

            val shortcut = createDynamicShortcut(
                icon = icon,
                person = person
            )
            addDynamicShortcut(shortcut)
            with(notificationBuilder) {
                setBubbleMetadata(bubbleData)
                style = Notification.MessagingStyle(person)
                    .addMessage(
                        Notification.MessagingStyle.Message(
                            MESSAGE,
                            System.currentTimeMillis(),
                            person
                        )
                    )
                setShortcutId(shortcut.id) // Must SET!
                addPerson(person)
            }
        }

        // The user can turn off the bubble in system settings. In that case, this notification
        // is shown as a normal notification instead of a bubble. Make sure that this
        // notification works as a normal notification as well.
        with(notificationBuilder) {
            setContentText(MESSAGE_FOR_BUBBLE_DISABLE)
            setSmallIcon(R.drawable.ic_round_adb_24)
            setCategory(Notification.CATEGORY_MESSAGE)
            setContentIntent(getPendingIntent())
            setShowWhen(true)
        }

        return notificationBuilder.build()
    }

    private fun getPendingIntent(): PendingIntent {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, BubbleActivity::class.java).setAction(Intent.ACTION_VIEW),
            flags
        )
    }

    private fun getNotificationBuilder(): Notification.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun addDynamicShortcut(shortcut: ShortcutInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            shortcutManager.pushDynamicShortcut(shortcut)
        } else {
            shortcutManager.addDynamicShortcuts(listOf(shortcut))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createBubbleMetadata(
        icon: Icon
    ): Notification.BubbleMetadata {
        // Create bubble intent
        val bubbleIntent = getPendingIntent()

        // Create bubble metadata
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Notification.BubbleMetadata.Builder(bubbleIntent, icon)
        } else {
            Notification.BubbleMetadata.Builder()
                .setIntent(bubbleIntent)
                .setIcon(icon)
        }
        return builder
            .setDesiredHeight(600)
            .setAutoExpandBubble(true)
            .setSuppressNotification(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createDynamicShortcut(
        icon: Icon,
        person: Person
    ): ShortcutInfo {
        return ShortcutInfo.Builder(context, SHORTCUT_INFO_ID)
            .setLongLived(true)
            .setIntent(
                Intent(context, BubbleActivity::class.java)
                    .setAction(Intent.ACTION_VIEW)
            )
            .setShortLabel(MOCK_TEXT)
            .setIcon(icon)
            .setPerson(person)
            .build()
    }
}