package ec.edu.uisek.githubclient.models

/** Modelo de datos para crear o actualizar un repositorio. */
data class RepoRequest(
    val name: String,
    val description: String
)
