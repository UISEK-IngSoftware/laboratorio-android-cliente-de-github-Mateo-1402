package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoitemBinding
import ec.edu.uisek.githubclient.models.Repo

/**
 * Es el "cerebro" que se encarga de dibujar cada repositorio en la lista. 
 * Sabe qué datos poner y cómo reaccionar cuando se pulsa un botón. 
 */
class ReposViewHolder(
    private val binding: FragmentRepoitemBinding,
    private val listener: RepoActionListener
) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Rellena la vista de un item de la lista con los datos de un repositorio. 
     */
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

        // Le decimos qué hacer cuando se pulsan los botones de editar o eliminar. 
        binding.editButton.setOnClickListener {
            listener.onEditClick(repo)
        }
        binding.deleteButton.setOnClickListener {
            listener.onDeleteClick(repo)
        }
    }
}

/**
 * El adaptador es el gestor general de la lista. 
 * Conecta los datos (la lista de repos) con el RecyclerView que los muestra. 
 */
class ReposAdapter(private val listener: RepoActionListener) : RecyclerView.Adapter<ReposViewHolder>() {
    // Aquí guardamos la lista de repositorios que se está mostrando. 
    private var repositories: MutableList<Repo> = mutableListOf()

    // Devuelve cuántos items hay en la lista. 
    override fun getItemCount(): Int = repositories.size

    // Crea una nueva "cajita" (ViewHolder) para un repositorio. 
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReposViewHolder(binding, listener)
    }

    // Conecta los datos de un repositorio con una "cajita" que ya existe. 
    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    /**
     * Actualiza la lista completa de repositorios y le dice al RecyclerView que se redibuje. 
     */
    fun updateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories.toMutableList()
        notifyDataSetChanged()
    }

    /**
     * Elimina un solo repositorio de la lista y notifica al RecyclerView para que lo quite. 
     */
    fun removeRepo(repo: Repo) {
        val position = repositories.indexOfFirst { it.id == repo.id }
        if (position > -1) {
            repositories.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
