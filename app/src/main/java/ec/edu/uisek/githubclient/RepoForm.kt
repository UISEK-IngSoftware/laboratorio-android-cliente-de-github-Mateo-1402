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

class RepoForm : AppCompatActivity() {
    private lateinit var binding: ActivityRepoFormBinding
    private var isEditMode = false
    private var repoName: String? = null

    companion object {
        const val EXTRA_IS_EDIT_MODE = "is_edit_mode"
        const val EXTRA_REPO_NAME = "repo_name"
        const val EXTRA_REPO_DESCRIPTION = "repo_description"
        const val GITHUB_OWNER = "Mateo-1402"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEditMode = intent.getBooleanExtra(EXTRA_IS_EDIT_MODE, false)
        repoName = intent.getStringExtra(EXTRA_REPO_NAME)

        if (isEditMode && repoName != null) {
            // If in edit mode, populate the form with existing data
            val repoDescription = intent.getStringExtra(EXTRA_REPO_DESCRIPTION)
            binding.repoNameInput.setText(repoName)
            binding.repoDescriptionInput.setText(repoDescription)
            binding.repoNameInput.isEnabled = false // The name of the repo cannot be edited
            binding.saveButton.text = "Update"
            binding.saveButton.setOnClickListener { updateRepo() }
        } else {
            // Otherwise, set up the form for creating a new repo
            binding.saveButton.setOnClickListener { createRepo() }
        }

        binding.cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun validateForm(): Boolean {
        val name = binding.repoNameInput.text.toString()
        if (name.isBlank()) {
            binding.repoNameInput.error = "El nombre del repositorio es obligatorio"
            return false
        }
        if (name.contains(" ")) {
            binding.repoNameInput.error = "El nombre del repositorio no puede contener espacios"
            return false
        }
        binding.repoNameInput.error = null
        return true
    }

    private fun createRepo() {
        if (!validateForm()) {
            return
        }

        val repoRequest = RepoRequest(
            binding.repoNameInput.text.toString().trim(),
            binding.repoDescriptionInput.text.toString().trim()
        )

        RetrofitClient.gitHubApiService.addRepo(repoRequest).enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado exitosamente")
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        422 -> "El repositorio ya existe o el nombre es inválido."
                        else -> "Error al crear el repositorio: ${response.code()}"
                    }
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Error de red: ${t.message}")
            }
        })
    }

    private fun updateRepo() {
        val updatedRepo = RepoRequest(
            binding.repoNameInput.text.toString().trim(),
            binding.repoDescriptionInput.text.toString().trim()
        )

        repoName?.let {
            RetrofitClient.gitHubApiService.updateRepo(GITHUB_OWNER, it, updatedRepo)
                .enqueue(object : Callback<Repo> {
                    override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                        if (response.isSuccessful) {
                            showMessage("Repositorio actualizado exitosamente")
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            showMessage("Error al actualizar: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Repo>, t: Throwable) {
                        showMessage("Error de red: ${t.message}")
                    }
                })
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
