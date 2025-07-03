//import androidx.compose.desktop.ui.tooling.preview.Preview
//import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlin.random.Random

// --- Game Configuration ---
const val GAME_DURATION_SECONDS = 30
const val BALLOON_WIDTH = 100
const val BALLOON_HEIGHT = 120

fun main() = application {
    // Defines the main application window
    Window(
        onCloseRequest = ::exitApplication,
        title = "Tap the Balloon!",
        state = rememberWindowState(width = 800.dp, height = 600.dp)
    ) {
        // Sets the visual theme for the game
        MaterialTheme {
            GameScreen()
        }
    }
}

@Composable
fun GameScreen() {
    // --- State Management ---
    // These `remember` variables hold the game's state. Compose "remembers" them
    // and redraws the screen when they change.
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(GAME_DURATION_SECONDS) }
    var balloonPosition by remember { mutableStateOf(Pair(0.5f, 0.5f)) } // X, Y bias
    var isGameActive by remember { mutableStateOf(false) }

    // --- Game Timer ---
    // This `LaunchedEffect` block runs a coroutine that acts as our game timer.
    // It only runs when `isGameActive` is true.
    if (isGameActive) {
        LaunchedEffect(Unit) {
            while (timeLeft > 0) {
                delay(1000) // wait for 1 second
                timeLeft--
            }
            isGameActive = false // Game over when time runs out
        }
    }

    // --- UI Layout ---
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF87CEEB)), // Sky blue background
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top bar showing the score and time
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Time: $timeLeft", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        // Main game area
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (isGameActive) {
                // The balloon button appears when the game is active
                BalloonButton(
                    position = balloonPosition,
                    onBalloonTapped = {
                        score++
                        // Move balloon to a new random position
                        balloonPosition = Pair(
                            Random.nextFloat(), // New random horizontal bias (0.0 to 1.0)
                            Random.nextFloat()  // New random vertical bias (0.0 to 1.0)
                        )
                    }
                )
            }
        }

        // Game Over or Start Button
        if (!isGameActive) {
            Button(
                onClick = {
                    // Reset game state and start
                    score = 0
                    timeLeft = GAME_DURATION_SECONDS
                    isGameActive = true
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(if (timeLeft == 0) "Play Again?" else "Start Game", fontSize = 20.sp)
            }
        } else {
            // A spacer to keep the layout consistent during gameplay
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun BalloonButton(position: Pair<Float, Float>, onBalloonTapped: () -> Unit) {
    // This Box will contain the balloon.
    // We use BoxWithConstraints to know the size of the game area.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Calculate the maximum X and Y coordinates the balloon can be placed at.
        // We subtract the balloon's size so it doesn't go off-screen.
        val xPos = (position.first * (maxWidth.value - BALLOON_WIDTH)).dp
        val yPos = (position.second * (maxHeight.value - BALLOON_HEIGHT)).dp

        // A button with an image inside.
        Button(
            // The offset modifier places the balloon at the calculated xPos and yPos.
            modifier = Modifier
                .offset(x = xPos, y = yPos)
                .size(BALLOON_WIDTH.dp, BALLOON_HEIGHT.dp),
            onClick = onBalloonTapped,
            shape = MaterialTheme.shapes.small,
            elevation = null,
            contentPadding = PaddingValues(0.dp)
        ) {
            // This will show a red circle as a placeholder.
            Box(Modifier.fillMaxSize().background(Color.Red, shape = MaterialTheme.shapes.medium))
            // --- To use a real image, uncomment the next line and add the file ---
            // Image(painter = painterResource("balloon.png"), contentDescription = "Balloon")
        }
    }
}