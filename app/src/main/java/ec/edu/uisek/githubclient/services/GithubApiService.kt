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

/** Define las llamadas a la API. */
interface GithubApiService {

    /** Obtiene la lista de repositorios de un usuario. */
    @GET("/users/{owner}/repos")
    fun getRepos(
        @Path("owner") owner: String,
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc"
    ): Call<List<Repo>>

    /** Crea un nuevo repositorio. */
    @POST("/user/repos")
    fun addRepo(
        @Body repoRequest: RepoRequest
    ): Call<Repo>

    /** Actualiza un repositorio existente. */
    @PATCH("/repos/{owner}/{repo_name}")
    fun updateRepo(
        @Path("owner") owner: String,
        @Path("repo_name") repoName: String,
        @Body repoRequest: RepoRequest
    ): Call<Repo>

    /** Elimina un repositorio. */
    @DELETE("/repos/{owner}/{repo_name}")
    fun deleteRepo(
        @Path("owner") owner: String,
        @Path("repo_name") repoName: String
    ): Call<Void>
}
