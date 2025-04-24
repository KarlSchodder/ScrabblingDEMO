package com.example.ccl3_scrabbling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ccl3_scrabbling.ui.theme.*

@Composable
fun StartScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SCRABBLING",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            CustomButton(
                text = "Start Scrabbling",
                onClick = { navController.navigate("gameScreen") },
                containerColor = BrownLight,
                contentColor = BrownDark
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                text = "How to Play",
                onClick = { showDialog = true },
                containerColor = BrownDark,
                contentColor = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                text = "Stats",
                onClick = { navController.navigate("statsScreen") },
                containerColor = RedLight,
                contentColor = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                text = "Settings",
                onClick = { navController.navigate("settingsScreen") },
                containerColor = BlueLight,
                contentColor = Color.White
            )
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "How to Play",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Tap letters and build words.\n" +
                            "Get points based on the letters used.\n" +
                            "The longer you go, the harder it gets.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            },
            confirmButton = {
                CustomButton(
                    text = "OK",
                    onClick = { showDialog = false },
                    containerColor = BlueLight,
                    contentColor = Color.White
                )
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = BrownLight,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun CustomButton(text: String, onClick: () -> Unit, containerColor: Color, contentColor: Color) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}