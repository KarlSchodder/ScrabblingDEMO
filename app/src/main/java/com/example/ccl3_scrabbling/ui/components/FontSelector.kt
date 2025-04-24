package com.example.ccl3_scrabbling.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ccl3_scrabbling.data.FontRepository

@Composable
fun FontSelector() {
    val unlockedFonts = FontRepository.getUnlockedFonts()
    var currentFontIndex by remember { mutableStateOf(unlockedFonts.indexOf(FontRepository.getCurrentFont())) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                currentFontIndex = (currentFontIndex - 1 + unlockedFonts.size) % unlockedFonts.size
                FontRepository.setCurrentFont(unlockedFonts[currentFontIndex])
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous font"
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = unlockedFonts[currentFontIndex].name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = unlockedFonts[currentFontIndex].fontFamily
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        IconButton(
            onClick = {
                currentFontIndex = (currentFontIndex + 1) % unlockedFonts.size
                FontRepository.setCurrentFont(unlockedFonts[currentFontIndex])
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next font"
            )
        }
    }
}