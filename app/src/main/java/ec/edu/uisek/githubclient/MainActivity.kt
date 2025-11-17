package ec.edu.uisek.githubclient

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Esta es la pantalla principal de la aplicación.
 * Muestra la lista de repositorios y nos permite interactuar con ellos.
 */
class MainActivity : AppCompatActivity(), RepoActionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter
    private lateinit var apiService: GithubApiService

    /**
     * Se encarga de recibir la respuesta del formulario de creación/edición.
     * Si todo salió bien (RESULT_OK), actualiza la lista de repositorios.
     */
    private val repoFormLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fetchRepositories()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Preparamos el servicio de la API para poder usarlo
        apiService = RetrofitClient.gitHubApiService

        setupRecyclerView()

        // Cuando se presiona el botón flotante, abrimos el formulario para crear un repo
        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }
    }

    override fun onResume() {
        super.onResume()
        // Cada vez que la pantalla vuelve a estar visible, actualizamos los repositorios
        fetchRepositories()
    }

    /**
     * Configura el RecyclerView para que muestre la lista de repositorios.
     */
    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(this) // "this" es la propia MainActivity, que escucha los clics
        binding.reposRecyclerView.apply {
            adapter = reposAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    /**
     * Pide a la API de GitHub la lista de repositorios y la muestra en pantalla.
     */
    private fun fetchRepositories() {
        val call = apiService.getRepos(RepoForm.GITHUB_OWNER)

        call.enqueue(object: Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if(response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null) {
                        reposAdapter.updateRepositories(repos) // Actualiza la lista en el adaptador
                    } else {
                        showMessage("No se encontraron repositorios.")
                    }
                } else {
                    // Si algo salió mal, mostramos un mensaje de error claro
                    val errorMessage = when (response.code()) {
                        401 -> "No estás autorizado. Revisa tu token de GitHub."
                        else -> "Ocurrió un error: Código ${response.code()}"
                    }
                    showMessage (errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("No se pudo conectar con el servidor. Revisa tu conexión a internet.")
            }
        })
    }

    /**
     * Muestra un mensaje Toast (una notificación flotante) en la pantalla.
     */
    private fun showMessage (message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Abre el formulario para crear un nuevo repositorio.
     */
    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            repoFormLauncher.launch(this)
        }
    }

    /**
     * Se ejecuta cuando el usuario presiona el botón de editar en un repositorio.
     * Abre el formulario en modo de edición.
     */
    override fun onEditClick(repo: Repo) {
        val intent = Intent(this, RepoForm::class.java).apply {
            putExtra(RepoForm.EXTRA_IS_EDIT_MODE, true)
            putExtra(RepoForm.EXTRA_REPO_NAME, repo.name)
            putExtra(RepoForm.EXTRA_REPO_DESCRIPTION, repo.description)
        }
        repoFormLauncher.launch(intent)
    }

    /**
     * Se ejecuta cuando el usuario presiona el botón de eliminar en un repositorio.
     * Muestra un diálogo para confirmar la acción.
     */
    override fun onDeleteClick(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Seguro que quieres eliminar el repositorio '${repo.name}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                executeDeleteRepo(repo) // Si confirma, procedemos a eliminarlo
            }
            .setNegativeButton("Cancelar", null) // Si cancela, no hacemos nada
            .show()
    }

    /**
     * Llama a la API para eliminar el repositorio de GitHub.
     */
    private fun executeDeleteRepo(repo: Repo) {
        apiService.deleteRepo(RepoForm.GITHUB_OWNER, repo.name).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio '${repo.name}' eliminado.")
                    reposAdapter.removeRepo(repo) // Lo quitamos de la lista al instante
                } else {
                    showMessage("Error al eliminar. Código: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Error de conexión al intentar eliminar. Revisa tu internet.")
            }
        })
    }
}
