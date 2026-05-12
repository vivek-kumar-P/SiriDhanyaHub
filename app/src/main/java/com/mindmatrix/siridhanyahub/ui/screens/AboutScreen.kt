package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mindmatrix.siridhanyahub.ui.theme.ForestGreen
import com.mindmatrix.siridhanyahub.ui.theme.HarvestAmber
import com.mindmatrix.siridhanyahub.ui.theme.TerracottaRed
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "about")
    val farmerScale = transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(animation = tween(1300), repeatMode = RepeatMode.Reverse),
        label = "farmerScale"
    )
    val consumerScale = transition.animateFloat(
        initialValue = 1.06f,
        targetValue = 0.94f,
        animationSpec = infiniteRepeatable(animation = tween(1300), repeatMode = RepeatMode.Reverse),
        label = "consumerScale"
    )
    var donateName by remember { mutableStateOf("") }
    var donateAmount by remember { mutableStateOf("") }
    var donateMessage by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Siri-Dhanya Hub") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Why this app exists", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Siri-Dhanya Hub was built to create a warmer connection between the people who grow millets and the people who want to cook, buy, and trust them. Farmers get clearer selling tools. Consumers get a more human way to discover and request real millet stock.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedCircleIcon(
                                scale = farmerScale.value,
                                background = ForestGreen.copy(alpha = 0.14f),
                                tint = ForestGreen,
                                icon = Icons.Default.Agriculture
                            )
                            AnimatedCircleIcon(
                                scale = 1f,
                                background = HarvestAmber.copy(alpha = 0.18f),
                                tint = TerracottaRed,
                                icon = Icons.Default.Handshake
                            )
                            AnimatedCircleIcon(
                                scale = consumerScale.value,
                                background = TerracottaRed.copy(alpha = 0.12f),
                                tint = TerracottaRed,
                                icon = Icons.Default.Storefront
                            )
                        }
                    }
                }
            }
            item { AboutFeatureCard("Mandi Watch", "A smart farmer lane where mandi price cards help choose a better selling point before publishing stock.") }
            item { AboutFeatureCard("Buy From Real Farmers", "A consumer-friendly request flow that helps nearby farmers notice the need and respond outside the app.") }
            item { AboutFeatureCard("Recipe Lab", "A playful millet kitchen full of grouped recipes, tiny badges, and quick-save cooking inspiration.") }
            item { AboutFeatureCard("Health Benefits", "A bright learning corner where each millet becomes a wellness card with simple nutrition stories.") }
            item { AboutFeatureCard("Saved Recipes", "A neat personal shelf that keeps the dishes you loved from getting lost in the scroll.") }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Who benefits", style = MaterialTheme.typography.titleLarge)
                        Text("Farmers benefit by getting easier publishing, clearer local demand, and a direct path to serious buyers.", style = MaterialTheme.typography.bodyMedium)
                        Text("Consumers benefit by finding local millet supply faster, comparing options more confidently, and exploring recipes and health value in one place.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Donate us", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "If you like the idea of building a kinder millet ecosystem, you can leave a small support note here.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedTextField(
                            value = donateName,
                            onValueChange = {
                                donateName = it
                                submitted = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Your name") }
                        )
                        OutlinedTextField(
                            value = donateAmount,
                            onValueChange = {
                                donateAmount = it
                                submitted = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Support amount") }
                        )
                        OutlinedTextField(
                            value = donateMessage,
                            onValueChange = {
                                donateMessage = it
                                submitted = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Message for us") }
                        )
                        Button(
                            onClick = { submitted = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Send support note")
                        }
                        AnimatedVisibility(
                            visible = submitted,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                        ) {
                            Text(
                                "Thank you, ${donateName.ifBlank { "friend" }}. Your support note has been recorded locally for this version.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedCircleIcon(
    scale: Float,
    background: Color,
    tint: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .scale(scale)
            .background(background, androidx.compose.foundation.shape.CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint)
    }
}

@Composable
private fun AboutFeatureCard(title: String, body: String) {
    var highlighted by remember { mutableStateOf(false) }
    val background = if (highlighted) HarvestAmber.copy(alpha = 0.22f) else WheatSurface
    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        modifier = Modifier.clickable { highlighted = !highlighted }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(HarvestAmber.copy(alpha = 0.18f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalDining, contentDescription = null, tint = TerracottaRed)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(body, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.Favorite, contentDescription = null, tint = if (highlighted) TerracottaRed else ForestGreen.copy(alpha = 0.5f))
        }
    }
}
