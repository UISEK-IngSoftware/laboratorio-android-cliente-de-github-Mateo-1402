package ec.edu.uisek.githubclient

import ec.edu.uisek.githubclient.models.Repo

/** Define las acciones del usuario en un repositorio. */
interface RepoActionListener {
    fun onEditClick(repo: Repo)
    fun onDeleteClick(repo: Repo)
}
