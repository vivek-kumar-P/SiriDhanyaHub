package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.i18n.RecipeTranslations
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.RecipeViewModel

@Composable
fun RecipeLabScreen(
    language: AppLanguage,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val recipes by viewModel.recipes.collectAsStateWithLifecycle()
    val selectedRecipe by viewModel.selectedRecipe.collectAsStateWithLifecycle()
    val isSelectedRecipeFavourite by viewModel.isSelectedRecipeFavourite.collectAsStateWithLifecycle()

    if (selectedRecipe != null) {
        RecipeDetailScreen(
            language = language,
            recipe = selectedRecipe!!,
            isFavourite = isSelectedRecipeFavourite,
            onToggleFavourite = viewModel::toggleFavourite,
            onBack = viewModel::closeRecipe
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Recipe Lab", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(searchLabel(language)) }
        )
        Text(
            text = recipeHint(language),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(recipes, key = { it.id }) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    language = language,
                    onOpen = { viewModel.openRecipe(recipe.id) }
                )
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: RecipeEntity,
    language: AppLanguage,
    onOpen: () -> Unit
) {
    val localized = RecipeTranslations.localize(recipe, language)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WheatSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(localized.title, style = MaterialTheme.typography.titleLarge)
            Text(recipe.nameEnglish, style = MaterialTheme.typography.bodyLarge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(recipe.milletType, style = MaterialTheme.typography.bodyMedium)
                Text("${recipe.prepTimeMinutes} min", style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedButton(
                onClick = onOpen,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(openRecipeLabel(language))
            }
        }
    }
}

@Composable
fun RecipeDetailScreen(
    language: AppLanguage,
    recipe: RecipeEntity,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    onBack: () -> Unit
) {
    val localized = RecipeTranslations.localize(recipe, language)
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack) {
                Text(backToRecipesLabel(language))
            }
            Button(onClick = onToggleFavourite) {
                Text(if (isFavourite) savedLabel(language) else saveLabel(language))
            }
        }
        Text(
            text = localized.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(recipe.nameEnglish, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "${milletLabel(language)}: ${recipe.milletType} | ${servesLabel(language)}: ${recipe.servesCount} | ${recipe.prepTimeMinutes} min",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(containerColor = WheatSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(ingredientsLabel(language), style = MaterialTheme.typography.titleMedium)
                Text(localized.ingredients, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            colors = CardDefaults.cardColors(containerColor = WheatSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stepsLabel(language), style = MaterialTheme.typography.titleMedium)
                Text(localized.steps, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

private fun searchLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Search by millet type"
    AppLanguage.KANNADA -> "ಸಿರಿಧಾನ್ಯ ಪ್ರಕಾರ ಹುಡುಕಿ"
    AppLanguage.HINDI -> "मिलेट प्रकार से खोजें"
}

private fun recipeHint(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Try Navane, Sajje, Baragu, or Ragi."
    AppLanguage.KANNADA -> "ನವಣೆ, ಸಜ್ಜೆ, ಬರಗು ಅಥವಾ ರಾಗಿ ಪ್ರಯತ್ನಿಸಿ."
    AppLanguage.HINDI -> "नवणे, सज्जे, बरगु या रागी आज़माएँ।"
}

private fun openRecipeLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Open recipe"
    AppLanguage.KANNADA -> "ರೆಸಿಪಿ ತೆರೆಯಿರಿ"
    AppLanguage.HINDI -> "रेसिपी खोलें"
}

private fun backToRecipesLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Back to recipes"
    AppLanguage.KANNADA -> "ರೆಸಿಪಿಗಳಿಗೆ ಹಿಂತಿರುಗಿ"
    AppLanguage.HINDI -> "रेसिपी पर वापस जाएँ"
}

private fun saveLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Save"
    AppLanguage.KANNADA -> "ಉಳಿಸಿ"
    AppLanguage.HINDI -> "सेव"
}

private fun savedLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Saved"
    AppLanguage.KANNADA -> "ಉಳಿಸಲಾಗಿದೆ"
    AppLanguage.HINDI -> "सेव हो गया"
}

private fun milletLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Millet"
    AppLanguage.KANNADA -> "ಸಿರಿಧಾನ್ಯ"
    AppLanguage.HINDI -> "मिलेट"
}

private fun servesLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Serves"
    AppLanguage.KANNADA -> "ಪರಿಮಾಣ"
    AppLanguage.HINDI -> "परोसता है"
}

private fun ingredientsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Ingredients"
    AppLanguage.KANNADA -> "ಪದಾರ್ಥಗಳು"
    AppLanguage.HINDI -> "सामग्री"
}

private fun stepsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Steps"
    AppLanguage.KANNADA -> "ಹಂತಗಳು"
    AppLanguage.HINDI -> "कदम"
}
