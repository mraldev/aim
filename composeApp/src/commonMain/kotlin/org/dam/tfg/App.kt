import aim.composeapp.generated.resources.LogoTodo_sinFondo
import aim.composeapp.generated.resources.Res
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
import androidx.compose.ui.layout.ContentScale
import cafe.adriel.voyager.navigator.Navigator
import org.dam.tfg.crypto.CredentialStore
import org.dam.tfg.repository.HealthCheckRepository
import org.dam.tfg.repository.LoginRepository
import org.dam.tfg.screens.Home
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha

@Composable
fun App() {
    var listoParaMostrar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val creds = CredentialStore.load()
        if (creds != null) {
            if (HealthCheckRepository().isServerActive())
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
            Image(
                painter = painterResource(Res.drawable.LogoTodo_sinFondo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.3f),
                contentScale = ContentScale.Fit
            )
            Navigator(Home())
        }
    }
}
