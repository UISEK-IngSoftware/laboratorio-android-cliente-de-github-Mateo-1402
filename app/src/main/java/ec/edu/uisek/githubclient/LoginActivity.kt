package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ec.edu.uisek.githubclient.databinding.ActivityLoginBinding
import ec.edu.uisek.githubclient.services.RetrofitClient
import ec.edu.uisek.githubclient.services.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura el listener del botón para evitar el error de onClick en XML
        binding.loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val username = binding.userInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            // 1. Inicializar el servicio API con las credenciales
            RetrofitClient.createApiService(username, password)
            
            // 2. Guardar las credenciales localmente
            val sessionManager = SessionManager(this)
            sessionManager.saveCredentials(username, password)
            
            // 3. Ir a la pantalla principal
            navigateToMain()
        } else {
            if (username.isEmpty()) binding.userInput.error = "Ingresa un usuario"
            if (password.isEmpty()) binding.passwordInput.error = "Ingresa una contraseña"
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
