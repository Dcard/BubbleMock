package tw.dcard.bubblemock.module

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import okhttp3.*
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
                notifyBubble(activity)
            } else {
                removeBubbleChannel(activity)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
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

        // Create bubble metadata
        val bubbleData = Notification.BubbleMetadata.Builder()
            .setDesiredHeight(600)
            .setIcon(Icon.createWithResource(activity, R.drawable.ic_router_white_24dp))
            .setIntent(bubbleIntent)
            .setAutoExpandBubble(true)
            .build()

        // Create notification
        val chatBot = Person.Builder()
            .setBot(true)
            .setName(NOTIFICATION_PERSON_NAME)
            .setImportant(true)
            .build()

        val builder = Notification.Builder(activity, CHANNEL_ID)
            .setContentTitle(CHANNEL_NAME)
            .setContentText(CHANNEL_MESSAGE)
            .setContentIntent(bubbleIntent)
            .setSmallIcon(R.drawable.ic_router_white_24dp)
            .setBubbleMetadata(bubbleData)
            .setAutoCancel(true)
            .addPerson(chatBot)

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
        body(ResponseBody.create(MediaType.parse(MimeType.JSON), Gson().toJson(obj)))
        code(200)
        message("Mocked response of ${request.method()} ${request.url()}")
        protocol(Protocol.HTTP_1_1)
        request(request)
        build()
    }

    private fun jsonResponse(request: Request, bodyString: String): Response =
        Response.Builder().run {
            addHeader("content-type", MimeType.JSON)
            body(ResponseBody.create(MediaType.parse(MimeType.JSON), bodyString))
            code(200)
            message("Mocked response of ${request.method()} ${request.url()}")
            protocol(Protocol.HTTP_1_1)
            request(request)
            build()
        }

    private fun emptyResponse(request: Request): Response = Response.Builder().run {
        addHeader("content-type", MimeType.JSON)
        code(204)
        body(ResponseBody.create(MediaType.parse(MimeType.JSON), ""))
        message("")
        protocol(Protocol.HTTP_1_1)
        request(request)
        build()
    }

    private fun errorResponse(request: Request): Response = Response.Builder().run {
        addHeader("content-type", MimeType.JSON)
        code(501)
        body(ResponseBody.create(MediaType.parse(MimeType.JSON), ""))
        message("mock error")
        protocol(Protocol.HTTP_1_1)
        request(request)
        build()
    }

    private fun serverErrorResponse(request: Request): Response = Response.Builder().run {
        val content = "{\"error\":1202,\"message\":\"Post not found\"}"
        addHeader("content-type", MimeType.JSON)
        code(404)
        body(ResponseBody.create(MediaType.parse(MimeType.JSON), content))
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
        body(ResponseBody.create(MediaType.parse(MimeType.JSON), content))
        message("mock error")
        protocol(Protocol.HTTP_1_1)
        request(request)
        build()
    }
}