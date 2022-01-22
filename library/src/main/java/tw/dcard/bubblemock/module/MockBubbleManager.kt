package tw.dcard.bubblemock.module

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ShortcutManager
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
import tw.dcard.bubblemock.model.MimeType
import tw.dcard.bubblemock.model.MockScenario

/**
 * @author Batu
 */
class MockBubbleManager {
    companion object {

        const val RESPONSE_EMPTY = "empty"
        const val RESPONSE_ERROR = "error"
        const val RESPONSE_SERVER_ERROR = "server_error"
        const val RESPONSE_SCOPE_ERROR = "scope_error"

        private const val CHANNEL_ID = "mock_bubble_channel"
        private const val CHANNEL_NAME = "Mock Bubble Channel"

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
                                        val jsonElement = JsonParser.parseString(it)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notifyBubble(activity: AppCompatActivity) {
        val notificationManager =
            activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shortcutManager =
            activity.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager

        // create channel
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = BubbleBuilder(activity, shortcutManager, CHANNEL_ID).build()
        notificationManager.notify(1, notification)
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