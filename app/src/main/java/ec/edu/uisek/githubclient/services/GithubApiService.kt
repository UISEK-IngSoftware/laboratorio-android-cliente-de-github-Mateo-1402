package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Define las diferentes llamadas a la API de GitHub que nuestra app puede hacer. 
 * Es como un menú de las acciones que podemos realizar. 
 */
interface GithubApiService {

    /**
     * Pide a GitHub la lista de repositorios de un usuario. 
     * Los ordena por fecha de creación, del más nuevo al más antiguo. 
     */
    @GET("/users/{owner}/repos")
    fun getRepos(
        @Path("owner") owner: String,
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc"
    ): Call<List<Repo>>

    /**
     * Envía una petición para crear un nuevo repositorio. 
     */
    @POST("/user/repos")
    fun addRepo(
        @Body repoRequest: RepoRequest
    ): Call<Repo>

    /**
     * Envía los datos para actualizar un repositorio que ya existe. 
     */
    @PATCH("/repos/{owner}/{repo_name}")
    fun updateRepo(
        @Path("owner") owner: String,
        @Path("repo_name") repoName: String,
        @Body repoRequest: RepoRequest
    ): Call<Repo>

    /**
     * Envía una orden para eliminar un repositorio. 
     * No esperamos recibir datos de vuelta, solo saber si se completó. 
     */
    @DELETE("/repos/{owner}/{repo_name}")
    fun deleteRepo(
        @Path("owner") owner: String,
        @Path("repo_name") repoName: String
    ): Call<Void>
}
