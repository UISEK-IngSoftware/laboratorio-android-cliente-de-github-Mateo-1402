package ec.edu.uisek.githubclient

import ec.edu.uisek.githubclient.models.Repo

interface RepoActionListener {
    fun onEditClick(repo: Repo)
    fun onDeleteClick(repo: Repo)
}
