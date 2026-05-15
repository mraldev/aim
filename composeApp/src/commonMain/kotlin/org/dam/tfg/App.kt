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
import kotlinx.serialization.builtins.ListSerializer
import org.dam.tfg.api.endpoints.TiradaEndpoint
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.crypto.ObjectStore
import org.dam.tfg.dto.DatosCompeticionDto
import org.dam.tfg.helpers.HelperSerializadorTiradasPorFaltaDeConexion.pairSerializer
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.repository.TiradaRepository

@Composable
fun App() {
    var listoParaMostrar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val creds = CredentialStore.load()
        if (creds != null) {
            if (HealthCheckRepository().isServerActive())
                LoginRepository().login(creds.correo, creds.contrasenya)
        }

        val sesionesPendientes: List<SesionEnviar>? =
            ObjectStore.load(
                "sesiones_pendientes",
                ListSerializer(SesionEnviar.serializer())
            )

        val ligasPendientes: List<Pair<SesionEnviar, DatosCompeticionDto>>? =
            ObjectStore.load(
                key = "ligas_pendientes",
                serializer = ListSerializer(
                    pairSerializer<SesionEnviar, DatosCompeticionDto>()
                )
            )

        sesionesPendientes?.let {
            it.forEach { tirada ->
                TiradaRepository().registrar(tirada)
            }

            ObjectStore.remove("sesiones_pendientes")
        }

        ligasPendientes?.let {
            it.forEach { tirada ->
                TiradaEndpoint().enviarRegistroCompetitivo(tirada.first, tirada.second)
            }

            ObjectStore.remove("ligas_pendientes")
        }

        val ultimaSesion: SesionEnviar? = ObjectStore.load("ultima_sesion", SesionEnviar.serializer())
        val ultimaLiga: DatosCompeticionDto? = ObjectStore.load("ultima_liga", DatosCompeticionDto.serializer())

        ultimaSesion?.let { ultimaSesion ->
            ultimaLiga?.let { ultimaLiga ->
                SesionManager.setDatosLiga(ultimaLiga)
            }
            SesionManager.setSesion(ultimaSesion)
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
