package com.example.ccl3_scrabbling

import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ccl3_scrabbling.ui.components.FontSelector

@Composable
fun SettingsScreen(
    navController: NavHostController,
    statsViewModel: StatsViewModel
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Font Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        FontSelector()

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Data Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showConfirmDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(text = "Clear All Stats")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Back button
        Button(onClick = { navController.navigate("startScreen") }) {
            Text(text = "Back")
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Clear All Stats?") },
            text = { Text("This will permanently delete all your game statistics and word history. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        statsViewModel.clearAllStats()
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear Stats")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}