package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoitemBinding
import ec.edu.uisek.githubclient.models.Repo

/** Dibuja cada repositorio en la lista y maneja los clics en los botones. */
class ReposViewHolder(
    private val binding: FragmentRepoitemBinding,
    private val listener: RepoActionListener
) : RecyclerView.ViewHolder(binding.root) {

    /** Rellena la vista con los datos de un repositorio. */
    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description
        binding.repoLang.text = repo.language
        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .into(binding.repoOwnerImage)

        // Define la acción de los botones de editar y eliminar.
        binding.editButton.setOnClickListener {
            listener.onEditClick(repo)
        }
        binding.deleteButton.setOnClickListener {
            listener.onDeleteClick(repo)
        }
    }
}

/** Adaptador que gestiona la lista de repositorios en el RecyclerView. */
class ReposAdapter(private val listener: RepoActionListener) : RecyclerView.Adapter<ReposViewHolder>() {
    // Lista de repositorios que se está mostrando.
    private var repositories: MutableList<Repo> = mutableListOf()

    // Devuelve el número total de items en la lista.
    override fun getItemCount(): Int = repositories.size

    // Crea un nuevo ViewHolder (una "caja") para un item de la lista.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReposViewHolder(binding, listener)
    }

    // Asocia los datos de un repositorio con un ViewHolder existente.
    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    /** Actualiza la lista completa de repositorios. */
    fun updateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories.toMutableList()
        notifyDataSetChanged()
    }

    /** Elimina un repositorio de la lista. */
    fun removeRepo(repo: Repo) {
        val position = repositories.indexOfFirst { it.id == repo.id }
        if (position > -1) {
            repositories.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
