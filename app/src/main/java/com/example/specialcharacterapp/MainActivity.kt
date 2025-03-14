package com.example.specialcharacterapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpecialCharactersApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialCharactersApp() {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()

    var recentChars by remember { mutableStateOf(listOf<String>()) }
    var favoriteChars by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        dataStore.recentCharacters.collect { recentChars = it }
    }

    LaunchedEffect(Unit) {
        dataStore.favoriteCharacters.collect { favoriteChars = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Special Characters", fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = {
                        scope.launch { dataStore.clearRecentCharacters() }
                        recentChars = emptyList()
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear Recent")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (recentChars.isNotEmpty()) {
                item { SectionTitle("Recently Used") }
                items(recentChars) { char ->
                    CharacterItem(
                        char, context,
                        onCopy = {
                            val updated = (recentChars + char).distinct().takeLast(10)
                            recentChars = updated
                            scope.launch { dataStore.saveRecentCharacters(updated) }
                        },
                        isFavorite = favoriteChars.contains(char),
                        onFavorite = {
                            val updatedFavorites = favoriteChars.toMutableSet()
                            if (updatedFavorites.contains(char)) updatedFavorites.remove(char) else updatedFavorites.add(char)
                            favoriteChars = updatedFavorites
                            scope.launch { dataStore.saveFavoriteCharacters(updatedFavorites) }
                        }
                    )
                }
            }

            if (favoriteChars.isNotEmpty()) {
                item { SectionTitle("Favorites") }
                items(favoriteChars.toList()) { char ->
                    CharacterItem(
                        char, context,
                        onCopy = {
                            val updated = (recentChars + char).distinct().takeLast(10)
                            recentChars = updated
                            scope.launch { dataStore.saveRecentCharacters(updated) }
                        },
                        isFavorite = favoriteChars.contains(char),
                        onFavorite = {
                            val updatedFavorites = favoriteChars.toMutableSet()
                            if (updatedFavorites.contains(char)) updatedFavorites.remove(char) else updatedFavorites.add(char)
                            favoriteChars = updatedFavorites
                            scope.launch { dataStore.saveFavoriteCharacters(updatedFavorites) }
                        }
                    )
                }
            }

            item { SectionTitle("All Characters") }
            val specialChars = listOf("©", "®", "™", "✓", "✗", "★", "☆", "❤", "☺", "☹", "∞", "∑", "∂", "∆", "π", "√")
            items(specialChars) { char ->
                CharacterItem(
                    char, context,
                    onCopy = {
                        val updated = (recentChars + char).distinct().takeLast(10)
                        recentChars = updated
                        scope.launch { dataStore.saveRecentCharacters(updated) }
                    },
                    isFavorite = favoriteChars.contains(char),
                    onFavorite = {
                        val updatedFavorites = favoriteChars.toMutableSet()
                        if (updatedFavorites.contains(char)) updatedFavorites.remove(char) else updatedFavorites.add(char)
                        favoriteChars = updatedFavorites
                        scope.launch { dataStore.saveFavoriteCharacters(updatedFavorites) }
                    }
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun CharacterList(
    characters: List<String>,
    context: Context,
    onCopy: (List<String>) -> Unit,
    favoriteChars: Set<String>,
    onFavorite: (MutableSet<String>) -> Unit
) {
    LazyColumn {
        items(characters) { char ->
            CharacterItem(
                char, context,
                onCopy = { onCopy((characters + char).distinct().takeLast(10)) },
                isFavorite = favoriteChars.contains(char),
                onFavorite = {
                    val updatedFavorites = favoriteChars.toMutableSet()
                    if (updatedFavorites.contains(char)) updatedFavorites.remove(char) else updatedFavorites.add(char)
                    onFavorite(updatedFavorites)
                }
            )
        }
    }
}

@Composable
fun CharacterItem(
    char: String,
    context: Context,
    onCopy: () -> Unit,
    isFavorite: Boolean,
    onFavorite: () -> Unit
) {
    val favoriteColor by animateColorAsState(targetValue = if (isFavorite) Color.Red else Color.Gray)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                copyToClipboard(context, char)
                onCopy()
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = char,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    tint = favoriteColor,
                    contentDescription = "Favorite"
                )
            }
        }
    }
}
