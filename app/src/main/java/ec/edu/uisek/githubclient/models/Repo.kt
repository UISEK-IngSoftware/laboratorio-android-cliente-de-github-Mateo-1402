package ec.edu.uisek.githubclient.models

/** Modelo de datos para un repositorio de GitHub. */
data class Repo(
    val id: Long,
    val name: String,
    val description: String?, // Puede ser nulo
    val language: String?,    // Puede ser nulo
    val owner: RepoOwner      // Objeto anidado del propietario
)
