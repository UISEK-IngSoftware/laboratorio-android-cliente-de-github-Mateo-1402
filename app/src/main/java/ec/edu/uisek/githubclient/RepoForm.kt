package ec.edu.uisek.githubclient

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** Formulario para crear o editar un repositorio. */
class RepoForm : AppCompatActivity() {
    private lateinit var binding: ActivityRepoFormBinding
    private var isEditMode = false
    private var repoName: String? = null

    companion object {
        // Claves para pasar datos entre pantallas
        const val EXTRA_IS_EDIT_MODE = "is_edit_mode"
        const val EXTRA_REPO_NAME = "repo_name"
        const val EXTRA_REPO_DESCRIPTION = "repo_description"
        const val GITHUB_OWNER = "Mateo-1402" // Tu nombre de usuario de GitHub
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEditMode = intent.getBooleanExtra(EXTRA_IS_EDIT_MODE, false)
        repoName = intent.getStringExtra(EXTRA_REPO_NAME)

        if (isEditMode && repoName != null) {
            // En modo edición, rellena el formulario con los datos existentes.
            val repoDescription = intent.getStringExtra(EXTRA_REPO_DESCRIPTION)
            binding.repoNameInput.setText(repoName)
            binding.repoDescriptionInput.setText(repoDescription)
            binding.repoNameInput.isEnabled = false // El nombre del repo no se puede editar.
            binding.saveButton.text = "Actualizar"
            binding.saveButton.setOnClickListener { updateRepo() }
        } else {
            // En modo creación, prepara el formulario para un nuevo repo.
            binding.saveButton.setOnClickListener { createRepo() }
        }

        binding.cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish() // Cierra el formulario.
        }
    }

    /** Valida el nombre del repositorio. */
    private fun validateForm(): Boolean {
        val name = binding.repoNameInput.text.toString()
        if (name.isBlank()) {
            binding.repoNameInput.error = "El nombre es obligatorio."
            return false
        }
        if (name.contains(" ")) {
            binding.repoNameInput.error = "El nombre no puede contener espacios."
            return false
        }
        binding.repoNameInput.error = null
        return true
    }

    /** Envía la petición para crear el repositorio. */
    private fun createRepo() {
        if (!validateForm()) {
            return
        }

        val repoRequest = RepoRequest(
            binding.repoNameInput.text.toString().trim(),
            binding.repoDescriptionInput.text.toString().trim()
        )

        try {
            RetrofitClient.getApiService().addRepo(repoRequest).enqueue(object : Callback<Repo> {
                override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                    if (response.isSuccessful) {
                        showMessage("Repositorio creado con éxito.")
                        setResult(Activity.RESULT_OK) // Avisa que la operación fue exitosa.
                        finish()
                    } else {
                        val errorMessage = when (response.code()) {
                            422 -> "El repositorio ya existe o el nombre no es válido."
                            else -> "Error al crear el repositorio: ${response.code()}"
                        }
                        showMessage(errorMessage)
                    }
                }

                override fun onFailure(call: Call<Repo>, t: Throwable) {
                    showMessage("Error de red: ${t.message}")
                }
            })
        } catch (e: Exception) {
            showMessage("Error: ${e.message}")
        }
    }

    /** Envía la petición para actualizar el repositorio. */
    private fun updateRepo() {
        val updatedRepo = RepoRequest(
            binding.repoNameInput.text.toString().trim(),
            binding.repoDescriptionInput.text.toString().trim()
        )

        repoName?.let { name ->
            try {
                RetrofitClient.getApiService().updateRepo(GITHUB_OWNER, name, updatedRepo)
                    .enqueue(object : Callback<Repo> {
                        override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                            if (response.isSuccessful) {
                                showMessage("Repositorio actualizado.")
                                setResult(Activity.RESULT_OK) // Avisa que la operación fue exitosa.
                                finish()
                            } else {
                                showMessage("Error al actualizar: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<Repo>, t: Throwable) {
                            showMessage("Error de red: ${t.message}")
                        }
                    })
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
