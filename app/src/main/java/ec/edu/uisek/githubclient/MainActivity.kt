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
import ec.edu.uisek.githubclient.services.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** Pantalla principal que muestra la lista de repositorios. */
class MainActivity : AppCompatActivity(), RepoActionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter
    private lateinit var apiService: GithubApiService

    /** Maneja el resultado del formulario de creación/edición. */
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

        // Prepara el servicio de la API
        try {
            apiService = RetrofitClient.getApiService()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: Sesión no inicializada", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupRecyclerView()

        // Abre el formulario para crear un repo
        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }

        // Configura el botón de logout
        binding.logoutFab.setOnClickListener {
            performLogout()
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualiza la lista de repositorios al volver a la pantalla
        if (::apiService.isInitialized) {
            fetchRepositories()
        }
    }

    /** Configura el RecyclerView. */
    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(this) // "this" es el listener de los clics
        binding.reposRecyclerView.apply {
            adapter = reposAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun performLogout() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres salir?")
            .setPositiveButton("Salir") { _, _ ->
                // Limpiar credenciales (opcional, depende de tu implementación de SessionManager)
                 val sessionManager = SessionManager(this)
                 sessionManager.saveCredentials("", "") // O crear un método clearCredentials()

                // Volver al LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Obtiene y muestra la lista de repositorios desde la API. */
    private fun fetchRepositories() {
        if (!::apiService.isInitialized) return
        
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
                    // Muestra un mensaje de error si algo falla
                    val errorMessage = when (response.code()) {
                        401 -> "No autorizado. Revisa tu token."
                        else -> "Error: Código ${response.code()}"
                    }
                    showMessage (errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("Error de conexión. Revisa tu internet.")
            }
        })
    }

    /** Muestra un mensaje Toast en la pantalla. */
    private fun showMessage (message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /** Abre el formulario para crear un nuevo repositorio. */
    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            repoFormLauncher.launch(this)
        }
    }

    /** Se ejecuta al pulsar "editar" en un repositorio. */
    override fun onEditClick(repo: Repo) {
        val intent = Intent(this, RepoForm::class.java).apply {
            putExtra(RepoForm.EXTRA_IS_EDIT_MODE, true)
            putExtra(RepoForm.EXTRA_REPO_NAME, repo.name)
            putExtra(RepoForm.EXTRA_REPO_DESCRIPTION, repo.description)
        }
        repoFormLauncher.launch(intent)
    }

    /** Se ejecuta al pulsar "eliminar" en un repositorio. */
    override fun onDeleteClick(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Seguro que quieres eliminar el repositorio '${repo.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                executeDeleteRepo(repo) // Si confirma, lo elimina
            }
            .setNegativeButton("Cancelar", null) // Si cancela, no hace nada
            .show()
    }

    /** Llama a la API para eliminar el repositorio. */
    private fun executeDeleteRepo(repo: Repo) {
        if (!::apiService.isInitialized) return

        apiService.deleteRepo(RepoForm.GITHUB_OWNER, repo.name).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio '${repo.name}' eliminado.")
                    reposAdapter.removeRepo(repo) // Quita el repo de la lista localmente
                } else {
                    showMessage("Error al eliminar. Código: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Error de conexión al eliminar.")
            }
        })
    }
}
