package cl.duoc.aquasample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.aquasample.navigation.AppNavigation
import cl.duoc.aquasample.ui.theme.AquaSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AquaSampleTheme { AppNavigation() }
        }
    }
}
