package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.i18n.RecipeTranslations
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.FavouriteViewModel

@Composable
fun FavouritesScreen(
    language: AppLanguage,
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

    if (favourites.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emptySavedTitle(language),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = emptySavedBody(language),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Saved Recipes", style = MaterialTheme.typography.headlineSmall)
        }
        items(favourites, key = { it.favourite.id }) { item ->
            val localized = RecipeTranslations.localize(item.recipe, language)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openRecipe(item.recipe) },
                colors = CardDefaults.cardColors(containerColor = WheatSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(localized.title, style = MaterialTheme.typography.titleLarge)
                    Text(item.recipe.nameEnglish, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "${item.recipe.milletType} | ${item.recipe.prepTimeMinutes} min",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    OutlinedButton(
                        onClick = { viewModel.remove(item.recipe.id) },
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(removeSavedLabel(language))
                    }
                }
            }
        }
    }
}

private fun emptySavedTitle(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "No saved recipes yet"
    AppLanguage.KANNADA -> "ಇನ್ನೂ ಉಳಿಸಿದ ರೆಸಿಪಿಗಳು ಇಲ್ಲ"
    AppLanguage.HINDI -> "अभी तक कोई सेव की गई रेसिपी नहीं है"
}

private fun emptySavedBody(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Save a recipe from Recipe Lab to see it here."
    AppLanguage.KANNADA -> "ಇಲ್ಲಿ ನೋಡಲು ರೆಸಿಪಿ ಲ್ಯಾಬ್‌ನಿಂದ ಒಂದು ರೆಸಿಪಿಯನ್ನು ಉಳಿಸಿ."
    AppLanguage.HINDI -> "इसे यहाँ देखने के लिए रेसिपी लैब से एक रेसिपी सेव करें।"
}

private fun removeSavedLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Remove from saved"
    AppLanguage.KANNADA -> "ಉಳಿಸಿದವುಗಳಿಂದ ತೆಗೆದುಹಾಕಿ"
    AppLanguage.HINDI -> "सेव से हटाएँ"
}
