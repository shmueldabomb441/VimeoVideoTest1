package contributors

import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

val baseUrl = "https://player.vimeo.com/video/"

interface VimeoService {
    @GET("{id}")
    suspend fun getVideo(
        @Path("id") id: Int,
    ): Response<ResponseBody>
}

data class Video(
    val id: Int,
    val title: String
)

fun createVimeoService(): VimeoService {
    val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
            val request = builder.build()
            chain.proceed(request)
        }
        .connectTimeout(10, TimeUnit.DAYS)
        .readTimeout(10, TimeUnit.DAYS)
        .writeTimeout(10, TimeUnit.DAYS)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(httpClient)
        .build()
    return retrofit.create(VimeoService::class.java)
}
