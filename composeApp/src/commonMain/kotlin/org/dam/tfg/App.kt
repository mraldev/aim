import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.crypto.CredentialStore
import org.dam.tfg.repository.LoginRepository
import org.dam.tfg.screens.Home

@Composable
fun App() {
    var listoParaMostrar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val creds = CredentialStore.load()
        if (creds != null) {
            LoginRepository().login(creds.correo, creds.contrasenya)
        }
        listoParaMostrar = true
    }

    if (!listoParaMostrar) return

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Eggshell)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .consumeWindowInsets(WindowInsets.safeDrawing)
        ) {
            Navigator(Home())
        }
    }
}
