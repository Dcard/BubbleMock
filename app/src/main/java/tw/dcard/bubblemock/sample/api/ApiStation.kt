package tw.dcard.bubblemock.sample.api

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import tw.dcard.bubblemock.sample.api.member.MemberService

class ApiStation {

    companion object {
        private const val BASE_URL = "test.com"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: ApiStation? = null

        fun getInstance() = INSTANCE ?: synchronized(ApiStation::class.java) {
            INSTANCE ?: ApiStation().also { INSTANCE = it }
        }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create(Gson())
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
//                    .addInterceptor(BubbleMockInterceptor())
                    .build()
            )
            .build()
    }

    val memberService: MemberService by lazy {
        retrofit.create(MemberService::class.java)
    }

}