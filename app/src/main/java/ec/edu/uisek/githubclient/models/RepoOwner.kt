package ec.edu.uisek.githubclient.models

import com.google.gson.annotations.SerializedName

/** Modelo de datos para el dueño de un repositorio. */
data class RepoOwner(
    val login: String, // Nombre de usuario en GitHub

    @SerializedName("avatar_url")
    val avatarUrl: String // URL de la imagen de perfil
)
