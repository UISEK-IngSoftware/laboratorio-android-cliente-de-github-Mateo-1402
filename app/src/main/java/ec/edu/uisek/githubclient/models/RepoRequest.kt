package ec.edu.uisek.githubclient.models

/**
 * Prepara los datos para crear o actualizar un repositorio.
 * Esto es lo que enviamos a la API de GitHub.
 */
data class RepoRequest(
    val name: String,
    val description: String
)
