package ec.edu.uisek.githubclient.models

/**
 * Define cómo luce un repositorio de GitHub en nuestra app. 
 * Estos son los datos que nos interesan de la API. 
 */
data class Repo(
    val id: Long,
    val name: String,
    val description: String?, // La descripción puede no existir 
    val language: String?,    // El lenguaje de programación puede no existir 
    val owner: RepoOwner      // El dueño del repositorio 
)
