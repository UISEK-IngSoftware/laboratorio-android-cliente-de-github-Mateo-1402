package ec.edu.uisek.githubclient.models

import com.google.gson.annotations.SerializedName

/**
 * Guarda la información del dueño de un repositorio. 
 */
data class RepoOwner(
    val login: String, // El nombre de usuario en GitHub 

    @SerializedName("avatar_url")
    val avatarUrl: String // La URL de la imagen de perfil 
)
