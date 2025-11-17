package ec.edu.uisek.githubclient.services

import android.util.Log
import ec.edu.uisek.githubclient.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Se encarga de crear y configurar Retrofit para que podamos
 * hablar con la API de GitHub de forma sencilla.
 */
object RetrofitClient {

    private const val TAG = "RetrofitClient"

    // La dirección base de la API de GitHub
    private const val BASE_URL = "https://api.github.com/"

    /**
     * Este interceptor es como un guardián que añade nuestro token de acceso
     * a cada petición para que GitHub sepa quiénes somos.
     */
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // Leemos el token que guardamos de forma segura
        val token = BuildConfig.GITHUB_API_TOKEN

        // Si tenemos un token, lo añadimos a la cabecera de la petición
        val newRequest = if (token.isNotEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "token $token")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build()
        } else {
            // Si no hay token, solo avisamos en el log
            Log.w(TAG, "⚠️ No hay un token de GitHub configurado.")
            originalRequest.newBuilder()
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build()
        }

        chain.proceed(newRequest)
    }

    /**
     * Este es un interceptor "chismoso" que nos muestra en el Logcat
     * un resumen de las peticiones que hacemos y las respuestas que recibimos.
     * Es muy útil para depurar, así que solo lo activamos en modo DEBUG.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    /**
     * Aquí creamos el cliente HTTP que usará Retrofit.
     * Le añadimos nuestros interceptores para la autenticación y el log.
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Creamos la instancia principal de Retrofit. Le decimos la URL base,
     * el cliente HTTP que debe usar y cómo convertir el JSON a nuestras clases.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Esta es la pieza final: el servicio que usaremos en la app para
     * hacer las llamadas a la API de GitHub de forma sencilla.
     */
    val gitHubApiService: GithubApiService by lazy {
        retrofit.create(GithubApiService::class.java)
    }
}
