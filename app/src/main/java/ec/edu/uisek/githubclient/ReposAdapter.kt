package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoitemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposViewHolder(
    private val binding: FragmentRepoitemBinding,
    private val listener: RepoActionListener
) : RecyclerView.ViewHolder(binding.root) {

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

        // Set listeners for edit and delete actions
        binding.editButton.setOnClickListener {
            listener.onEditClick(repo)
        }
        binding.deleteButton.setOnClickListener {
            listener.onDeleteClick(repo)
        }
    }
}

class ReposAdapter(private val listener: RepoActionListener) : RecyclerView.Adapter<ReposViewHolder>() {
    private var repositories: MutableList<Repo> = mutableListOf()

    override fun getItemCount(): Int = repositories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReposViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    fun updateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories.toMutableList()
        notifyDataSetChanged()
    }

    fun removeRepo(repo: Repo) {
        val position = repositories.indexOfFirst { it.id == repo.id }
        if (position > -1) {
            repositories.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
