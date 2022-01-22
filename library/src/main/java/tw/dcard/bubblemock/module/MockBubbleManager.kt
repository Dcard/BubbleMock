package tw.dcard.bubblemock.module

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import tw.dcard.bubblemock.R
import tw.dcard.bubblemock.model.MimeType
import tw.dcard.bubblemock.model.MockScenario
import tw.dcard.bubblemock.sample.screen.BubbleActivity

/**
 * @author Batu
 */
class MockBubbleManager {
    companion object {

        const val RESPONSE_EMPTY = "empty"
        const val RESPONSE_ERROR = "error"
        const val RESPONSE_SERVER_ERROR = "server_error"
        const val RESPONSE_SCOPE_ERROR = "scope_error"

        private const val CHANNEL_ID = "mock_bubble"
        private const val CHANNEL_NAME = "Mock Bubble "
        private const val CHANNEL_MESSAGE = "choose mock data sources"
        private const val NOTIFICATION_PERSON_NAME = "Mock Bubble Bot"

        private const val CHANNEL_NEW_BUBBLE = "new_bubble"

        private var INSTANCE: MockBubbleManager? = null

        @JvmStatic
        fun getInstance(): MockBubbleManager =
            INSTANCE ?: MockBubbleManager().apply { INSTANCE = this }

        @JvmStatic
        fun init(mockSource: MockSource) {
            getInstance().apply {
                mockOperationModels = mockSource.create()
            }
        }
    }

    var mockOperationModels: List<MockScenario>? = null

    fun handle(request: Request): Response? {
        mockOperationModels?.forEach { mockRequest ->
            if (mockRequest.selected) {
                mockRequest.mockApiList.forEach { mockApi ->
                    mockApi.handle(request)?.let {
                        try {
                            Thread.sleep(mockApi.delay)
                        } catch (t: Throwable) {
                        }

                        return if (it is String) {
                            when (it) {
                                RESPONSE_EMPTY -> emptyResponse(request)
                                RESPONSE_ERROR -> errorResponse(request)
                                RESPONSE_SERVER_ERROR -> serverErrorResponse(request)
                                RESPONSE_SCOPE_ERROR -> scopeErrorResponse(request)
                                else -> {
                                    try {
                                        val jsonElement = JsonParser().parse(it)
                                        if (jsonElement.isJsonObject || jsonElement.isJsonArray) {
                                            jsonResponse(request, it)
                                        } else {
                                            null
                                        }
                                    } catch (e: JsonParseException) {
                                        null
                                    }
                                }
                            }
                        } else {
                            jsonResponse(request, it)
                        }
                    }
                }
            }
        }

        return null
    }

    fun launchBubble(activity: AppCompatActivity, isEnable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (isEnable) {
                notifyBubble2(activity)
            } else {
                removeBubbleChannel(activity)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notifyBubble2(activity: AppCompatActivity) {
        val notificationManager =
            activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shortcutManager = activity.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        //create person
        val icon = Icon.createWithResource(activity, R.drawable.ic_router_white_24dp)
//        val contentUri = createContentUri(simpleMessage.sender)

        val builder = getNotificationBuilder(activity)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val person = Person.Builder()
                .setName(NOTIFICATION_PERSON_NAME)
                .setIcon(icon)
                .setImportant(true)
                .build()
            val bubbleData = createBubbleMetadata(activity, icon)

            val shortcut = createDynamicShortcut(
                context = activity,
                icon = icon,
                person = person
            )
            addDynamicShortcut(shortcutManager, shortcut)
            with(builder) {
                setBubbleMetadata(bubbleData)
                style = Notification.MessagingStyle(person).addMessage(
                    Notification.MessagingStyle.Message(
                        "Messaging Style Message",
                        System.currentTimeMillis(),
                        person
                    )
                )
                setShortcutId(shortcut.id)
                addPerson(person)
            }

        }

        // The user can turn off the bubble in system settings. In that case, this notification
        // is shown as a normal notification instead of a bubble. Make sure that this
        // notification works as a normal notification as well.
        with(builder) {
            setContentTitle(
                "Content Title"
            )
            setSmallIcon(R.drawable.ic_router_white_24dp)
            setCategory(Notification.CATEGORY_MESSAGE)
            setContentIntent(
                PendingIntent.getActivity(
                    activity,
                    0,
                    // Launch BubbleActivity as the expanded bubble.
                    Intent(activity, BubbleActivity::class.java)
                        .setAction(Intent.ACTION_VIEW),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            setShowWhen(true)
        }

        notificationManager.notify(1, builder.build())
    }

    private fun getNotificationBuilder(context : Context): Notification.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            Notification.Builder(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createBubbleMetadata(
        context: Context,
        icon: Icon
    ): Notification.BubbleMetadata {
        // Create bubble intent
        val bubbleIntent =
            PendingIntent.getActivity(
                context,
                0,
                // Launch BubbleActivity as the expanded bubble.
                Intent(context, BubbleActivity::class.java)
                    .setAction(Intent.ACTION_VIEW),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        // Create bubble metadata
        val builder = if (atLeastAndroid11()) {
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
        context: Context,
        icon: Icon,
        person: Person
    ): ShortcutInfo {
        return ShortcutInfo.Builder(context, "XD")
            .setLongLived(true)
            .setIntent(
                Intent(context, BubbleActivity::class.java)
                    .setAction(Intent.ACTION_VIEW)
            )
            .setShortLabel("Sender")
            .setIcon(icon)
            .setPerson(person)
            .build()
    }

    private fun atLeastAndroid11() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun addDynamicShortcut(shortcutManager: ShortcutManager, shortcut: ShortcutInfo) {
        if (atLeastAndroid11()) {
            shortcutManager.pushDynamicShortcut(shortcut)
        } else {
            shortcutManager.addDynamicShortcuts(listOf(shortcut))
        }
    }


    @TargetApi(Build.VERSION_CODES.R)
    private fun notifyBubble(activity: AppCompatActivity) {
        val notificationManager =
            activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Create bubble intent
        val target = Intent(activity, BubbleActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(activity, 0, target, 0 /* flags */)
        val category = "com.example.category.IMG_SHARE_TARGET"



        // Create notification
        val chatBot = Person.Builder()
            .setName(NOTIFICATION_PERSON_NAME)
            .setImportant(true)
            .build()

        // Create sharing shortcut
        val shortcutId = "shortcut_id".toString()
        val shortcut =
            ShortcutInfo.Builder(activity, shortcutId)
                .setCategories(setOf(category))
                .setIntent(Intent(Intent.ACTION_DEFAULT))
                .setLongLived(true)
                .setShortLabel(chatBot.name.toString())
                .build()

        // Create bubble metadata
        val bubbleData = Notification.BubbleMetadata.Builder(
            bubbleIntent, Icon.createWithResource(activity, R.drawable.ic_router_white_24dp)
        )
            .setDesiredHeight(600)
//            .setIcon()
//            .setIntent(bubbleIntent)
//            .setAutoExpandBubble(true)
            .build()

        val builder = Notification.Builder(activity, CHANNEL_ID)
            .setContentTitle(CHANNEL_NAME)
            .setContentText(CHANNEL_MESSAGE)
            .setContentIntent(bubbleIntent)
            .setSmallIcon(R.drawable.ic_router_white_24dp)
            .setBubbleMetadata(bubbleData)
            .setShortcutId(shortcutId)
//            .setAutoCancel(true)
            .addPerson(chatBot).apply{
                style = Notification.MessagingStyle(chatBot).addMessage(
                    Notification.MessagingStyle.Message(
                        "XDD",
                        System.currentTimeMillis(),
                        chatBot
                    )
                )
            }

        val notificationId = 1
        val notification = builder.build()

        notificationManager.notify(notificationId, notification)
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun removeBubbleChannel(activity: AppCompatActivity) {
        val notificationManager =
            activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.deleteNotificationChannel(CHANNEL_ID)
    }

    private fun jsonResponse(request: Request, obj: Any): Response = Response.Builder().run {
        addHeader("content-type", MimeType.JSON)
        body(Gson().toJson(obj).toResponseBody(MimeType.JSON.toMediaTypeOrNull()))
        code(200)
        message("Mocked response of ${request.method} ${request.url}")
        protocol(Protocol.HTTP_1_1)
        request(request)
        build()
    }

    private fun jsonResponse(request: Request, bodyString: String): Response =
        Response.Builder().run {
            addHeader("content-type", MimeType.JSON)
            body(bodyString.toResponseBody(MimeType.JSON.toMediaTypeOrNull()))
            code(200)
            message("Mocked response of ${request.method} ${request.url}")
            protocol(Protocol.HTTP_1_1)
            request(request)
            build()
        }

    private fun emptyResponse(request: Request): Response = Response.Builder().run {
        addHeader("content-type", MimeType.JSON)
        code(204)
        body("".toResponseBody(MimeType.JSON.toMediaTypeOrNull()))
        message("")
        protocol(Protocol.HTTP_1_1)
        request(request)
        build()
    }

    private fun errorResponse(request: Request): Response = Response.Builder().run {
        addHeader("content-type", MimeType.JSON)
        code(501)
        body("".toResponseBody(MimeType.JSON.toMediaTypeOrNull()))
        message("mock error")
        protocol(Protocol.HTTP_1_1)
        request(request)
        build()
    }

    private fun serverErrorResponse(request: Request): Response = Response.Builder().run {
        val content = "{\"error\":1202,\"message\":\"Post not found\"}"
        addHeader("content-type", MimeType.JSON)
        code(404)
        body(content.toResponseBody(MimeType.JSON.toMediaTypeOrNull()))
        message("mock error")
        protocol(Protocol.HTTP_1_1)
        request(request)
        build()
    }

    private fun scopeErrorResponse(request: Request): Response = Response.Builder().run {
        val content =
            "{\"error\":\"insufficient_scope\",\"error_description\":\"Scope is insufficient\",\"scope\":\"match\"}"
        addHeader("content-type", MimeType.JSON)
        code(403)
        body(content.toResponseBody(MimeType.JSON.toMediaTypeOrNull()))
        message("mock error")
        protocol(Protocol.HTTP_1_1)
        request(request)
        build()
    }
}