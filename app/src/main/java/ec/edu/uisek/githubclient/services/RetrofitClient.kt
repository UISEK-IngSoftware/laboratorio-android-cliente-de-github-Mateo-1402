package ec.edu.uisek.githubclient.services

import android.util.Log
import ec.edu.uisek.githubclient.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Configura y provee la instancia de Retrofit para la API de GitHub. */
object RetrofitClient {

    private const val TAG = "RetrofitClient"

    // URL base de la API de GitHub
    private const val BASE_URL = "https://api.github.com/"

    /** Interceptor para añadir el token de autenticación a las peticiones. */
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // Obtiene el token desde BuildConfig
        val token = BuildConfig.GITHUB_API_TOKEN

        // Añade el token a la cabecera si existe
        val newRequest = if (token.isNotEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "token $token")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build()
        } else {
            // Advierte si el token no está configurado
            Log.w(TAG, "Token de GitHub no configurado.")
            originalRequest.newBuilder()
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build()
        }

        chain.proceed(newRequest)
    }

    /** Interceptor para registrar las peticiones y respuestas en Logcat (solo en DEBUG). */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    /** Cliente HTTP con los interceptores de autenticación y logging. */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    /** Instancia de Retrofit configurada. */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Servicio de la API de GitHub listo para usar. */
    val gitHubApiService: GithubApiService by lazy {
        retrofit.create(GithubApiService::class.java)
    }
}
