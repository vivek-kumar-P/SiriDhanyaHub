package com.mindmatrix.siridhanyahub.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.i18n.RecipeTranslations
import com.mindmatrix.siridhanyahub.ui.theme.ForestGreen
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.FavouriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    viewModel: FavouriteViewModel = hiltViewModel()
) {
    val favourites by viewModel.favourites.collectAsStateWithLifecycle()
    val selectedRecipe by viewModel.selectedRecipe.collectAsStateWithLifecycle()

    if (selectedRecipe != null) {
        RecipeDetailScreen(
            language = language,
            recipe = selectedRecipe!!,
            isFavourite = true,
            onToggleFavourite = { viewModel.remove(selectedRecipe!!.id) },
            onBack = viewModel::closeRecipe
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Recipes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (favourites.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = emptySavedTitle(language), style = MaterialTheme.typography.headlineSmall)
                Text(text = emptySavedBody(language), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(favourites, key = { it.favourite.id }) { item ->
                val localized = RecipeTranslations.localize(item.recipe, language)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openRecipe(item.recipe) },
                    colors = CardDefaults.cardColors(containerColor = WheatSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(ForestGreen.copy(alpha = 0.14f), androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                item.recipe.nameEnglish.take(1).uppercase(),
                                color = ForestGreen,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(localized.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(item.recipe.nameEnglish, style = MaterialTheme.typography.bodySmall)
                            Text("${item.recipe.milletType} • ${item.recipe.prepTimeMinutes} min", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                        }
                        OutlinedButton(onClick = { viewModel.remove(item.recipe.id) }) {
                            Text(removeSavedLabel(language))
                        }
                    }
                }
            }
        }
    }
}

private fun emptySavedTitle(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "No saved recipes yet"
    AppLanguage.KANNADA -> "à²‡à²¨à³à²¨à³‚ à²‰à²³à²¿à²¸à²¿à²¦ à²°à³†à²¸à²¿à²ªà²¿à²—à²³à³ à²‡à²²à³à²²"
    AppLanguage.HINDI -> "à¤…à¤­à¥€ à¤¤à¤• à¤•à¥‹à¤ˆ à¤¸à¥‡à¤µ à¤•à¥€ à¤—à¤ˆ à¤°à¥‡à¤¸à¤¿à¤ªà¥€ à¤¨à¤¹à¥€à¤‚ à¤¹à¥ˆ"
}

private fun emptySavedBody(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Save a recipe from Recipe Lab to keep it here for quick cooking access."
    AppLanguage.KANNADA -> "à²°à³†à²¸à²¿à²ªà²¿ à²²à³à²¯à²¾à²¬à³â€Œà²¨à²¿à²‚à²¦ à²°à³†à²¸à²¿à²ªà²¿ à²‰à²³à²¿à²¸à²¿à²¦à²°à³† à²‡à²²à³à²²à²¿ à²¦à³à²°à³à²¤à²µà²¾à²—à²¿ à²¸à²¿à²—à³à²¤à³à²¤à²¦à³†."
    AppLanguage.HINDI -> "à¤°à¥‡à¤¸à¤¿à¤ªà¥€ à¤²à¥ˆà¤¬ à¤¸à¥‡ à¤à¤• à¤°à¥‡à¤¸à¤¿à¤ªà¥€ à¤¸à¥‡à¤µ à¤•à¤°à¥‡à¤‚ à¤¤à¤¾à¤•à¤¿ à¤¯à¤¹ à¤¯à¤¹à¤¾à¤ à¤°à¤¹à¥‡."
}

private fun removeSavedLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Remove"
    AppLanguage.KANNADA -> "à²¤à³†à²—à³†à²¦à³à²¹à²¾à²•à²¿"
    AppLanguage.HINDI -> "à¤¹à¤Ÿà¤¾à¤à¤"
}
