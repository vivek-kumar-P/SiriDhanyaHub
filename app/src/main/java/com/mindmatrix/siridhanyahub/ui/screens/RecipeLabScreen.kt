package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.i18n.RecipeTranslations
import com.mindmatrix.siridhanyahub.ui.theme.ForestGreen
import com.mindmatrix.siridhanyahub.ui.theme.HarvestAmber
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeLabScreen(
    language: AppLanguage,
    onBack: () -> Unit,
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

    val sections = recipes.groupBy { it.milletType }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Lab") },
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
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::updateQuery,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(searchLabel(language)) }
                )
            }
            item {
                Text(
                    text = recipeHint(language),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            sections.forEach { (category, itemsInCategory) ->
                item {
                    Text(category, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                }
                items(itemsInCategory, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        language = language,
                        onOpen = { viewModel.openRecipe(recipe.id) }
                    )
                }
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
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RecipeBadge(recipe = recipe)
                Column(modifier = Modifier.weight(1f)) {
                    Text(localized.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(recipe.nameEnglish, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text("${recipe.milletType} • ${recipe.prepTimeMinutes} min • ${recipe.servesCount} serves", style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick = onOpen) {
                Text(openRecipeLabel(language))
            }
        }
    }
}

@Composable
private fun RecipeBadge(recipe: RecipeEntity) {
    val initials = recipe.nameEnglish.split(" ").take(2).joinToString("") { it.take(1) }.uppercase()
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(ForestGreen.copy(alpha = 0.14f), androidx.compose.foundation.shape.CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = ForestGreen, style = MaterialTheme.typography.labelLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    language: AppLanguage,
    recipe: RecipeEntity,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    onBack: () -> Unit
) {
    val localized = RecipeTranslations.localize(recipe, language)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localized.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    OutlinedButton(onClick = onToggleFavourite) {
                        Text(if (isFavourite) savedLabel(language) else saveLabel(language))
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RecipeBadge(recipe = recipe)
                    Column {
                        Text(recipe.nameEnglish, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${milletLabel(language)}: ${recipe.milletType} • ${servesLabel(language)}: ${recipe.servesCount} • ${recipe.prepTimeMinutes} min",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(ingredientsLabel(language), style = MaterialTheme.typography.titleMedium)
                        Text(localized.ingredients, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stepsLabel(language), style = MaterialTheme.typography.titleMedium)
                        Text(localized.steps, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

private fun searchLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Search by millet type"
    AppLanguage.KANNADA -> "à²¸à²¿à²°à²¿à²§à²¾à²¨à³à²¯ à²ªà³à²°à²•à²¾à²° à²¹à³à²¡à³à²•à²¿"
    AppLanguage.HINDI -> "à¤®à¤¿à¤²à¥‡à¤Ÿ à¤ªà¥à¤°à¤•à¤¾à¤° à¤¸à¥‡ à¤–à¥‹à¤œà¥‡à¤‚"
}

private fun recipeHint(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Recipes are grouped by millet type so you can scan fast and open the one that fits your meal."
    AppLanguage.KANNADA -> "à²°à³†à²¸à²¿à²ªà²¿à²—à²³à²¨à³à²¨à³ à²¸à²¿à²°à²¿à²§à²¾à²¨à³à²¯ à²ªà³à²°à²•à²¾à²°à²¦ à²†à²§à²¾à²°à²¦ à²®à³‡à²²à³† à²—à³à²‚à²ªà²¿à²¸à²²à²¾à²—à²¿à²¦à³†."
    AppLanguage.HINDI -> "à¤°à¥‡à¤¸à¤¿à¤ªà¥€ à¤•à¥‹ à¤®à¤¿à¤²à¥‡à¤Ÿ à¤ªà¥à¤°à¤•à¤¾à¤° à¤•à¥‡ à¤¹à¤¿à¤¸à¤¾à¤¬ à¤¸à¥‡ à¤—à¥à¤°à¥à¤ª à¤•à¤¿à¤¯à¤¾ à¤—à¤¯à¤¾ à¤¹à¥ˆà¥¤"
}

private fun openRecipeLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Open recipe"
    AppLanguage.KANNADA -> "à²°à³†à²¸à²¿à²ªà²¿ à²¤à³†à²°à³†à²¯à²¿à²°à²¿"
    AppLanguage.HINDI -> "à¤°à¥‡à¤¸à¤¿à¤ªà¥€ à¤–à¥‹à¤²à¥‡à¤‚"
}

private fun saveLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Save"
    AppLanguage.KANNADA -> "à²‰à²³à²¿à²¸à²¿"
    AppLanguage.HINDI -> "à¤¸à¥‡à¤µ"
}

private fun savedLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Saved"
    AppLanguage.KANNADA -> "à²‰à²³à²¿à²¸à²²à²¾à²—à²¿à²¦à³†"
    AppLanguage.HINDI -> "à¤¸à¥‡à¤µ à¤¹à¥‹ à¤—à¤¯à¤¾"
}

private fun milletLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Millet"
    AppLanguage.KANNADA -> "à²¸à²¿à²°à²¿à²§à²¾à²¨à³à²¯"
    AppLanguage.HINDI -> "à¤®à¤¿à¤²à¥‡à¤Ÿ"
}

private fun servesLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Serves"
    AppLanguage.KANNADA -> "à²ªà²°à²¿à²®à²¾à²£"
    AppLanguage.HINDI -> "à¤ªà¤°à¥‹à¤¸à¤¤à¤¾ à¤¹à¥ˆ"
}

private fun ingredientsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Ingredients"
    AppLanguage.KANNADA -> "à²ªà²¦à²¾à²°à³à²¥à²—à²³à³"
    AppLanguage.HINDI -> "à¤¸à¤¾à¤®à¤—à¥à¤°à¥€"
}

private fun stepsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Steps"
    AppLanguage.KANNADA -> "à²¹à²‚à²¤à²—à²³à³"
    AppLanguage.HINDI -> "à¤•à¤¦à¤®"
}
