package ec.edu.uisek.githubclient.services

import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Configura y provee la instancia de Retrofit para la API de GitHub. */
object RetrofitClient {

    private const val TAG = "RetrofitClient"
    private const val BASE_URL = "https://api.github.com/"

    private var apiService: GithubApiService? = null

    fun createApiService(username: String, password: String): GithubApiService {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val auth = Credentials.basic(username, password)
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", auth)
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GithubApiService::class.java)
        apiService = service
        return service
    }

    fun getApiService(): GithubApiService {
        return apiService ?: throw IllegalStateException("El cliente retrofit no ha sido inicializado. Llama a createApiService primero.")
    }
}