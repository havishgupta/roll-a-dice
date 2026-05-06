package com.example.tosswidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tosswidget.logic.TossLogic
import com.example.tosswidget.logic.TossResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TossAppScreen()
                }
            }
        }
    }
}

@Composable
fun TossAppScreen() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isFlipping by remember { mutableStateOf(false) }
    var tossResult by remember { mutableStateOf<TossResult?>(null) }
    
    var targetRotation by remember { mutableFloatStateOf(0f) }
    
    // Smooth 3D rotation animation
    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "coinFlip"
    )
    
    // Determine which side of the coin is currently visible based on the rotation angle
    val normalizedRotation = (rotation % 360 + 360) % 360
    val isHeadsVisible = normalizedRotation < 90 || normalizedRotation > 270

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .background(if (isHeadsVisible) Color(0xFFFFD700) else Color(0xFFC0C0C0), CircleShape)
                .clickable(enabled = !isFlipping) {
                    coroutineScope.launch {
                        isFlipping = true
                        tossResult = null
                        
                        val result = TossLogic.performToss(context)
                        
                        // Calculate target rotation for realistic multiple spins
                        val extraSpins = 10 * 360f 
                        val endRotation = if (result == TossResult.HEADS) 0f else 180f
                        targetRotation += extraSpins + endRotation - (targetRotation % 360f)
                        
                        delay(2000)
                        tossResult = result
                        isFlipping = false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Inner circle styling
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(if (isHeadsVisible) Color(0xFFB8860B) else Color(0xFF808080), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isHeadsVisible) "HEADS" else "TAILS",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.graphicsLayer {
                        // Prevent the text from being drawn backwards when the coin is rotated 180 degrees
                        if (!isHeadsVisible) {
                            rotationY = 180f
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = if (isFlipping) "Tossing..." else (tossResult?.name ?: "Tap the Coin to Toss"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
