package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity
import com.mindmatrix.siridhanyahub.data.repository.FavouriteRepository
import com.mindmatrix.siridhanyahub.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val favouriteRepository: FavouriteRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedRecipeId = MutableStateFlow<Int?>(null)
    val selectedRecipeId: StateFlow<Int?> = _selectedRecipeId.asStateFlow()

    val recipes: StateFlow<List<RecipeEntity>> = _query
        .flatMapLatest { query -> recipeRepository.search(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedRecipe: StateFlow<RecipeEntity?> = _selectedRecipeId
        .flatMapLatest { recipeId ->
            if (recipeId == null) flowOf(null)
            else recipeRepository.observeById(recipeId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val isSelectedRecipeFavourite: StateFlow<Boolean> = _selectedRecipeId
        .flatMapLatest { recipeId ->
            if (recipeId == null) flowOf(false)
            else favouriteRepository.observeIsFavourite(recipeId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun updateQuery(query: String) {
        _query.value = query
    }

    fun openRecipe(recipeId: Int) {
        _selectedRecipeId.value = recipeId
    }

    fun closeRecipe() {
        _selectedRecipeId.value = null
    }

    fun toggleFavourite() {
        val recipeId = _selectedRecipeId.value ?: return
        viewModelScope.launch {
            if (isSelectedRecipeFavourite.value) {
                favouriteRepository.remove(recipeId)
            } else {
                favouriteRepository.save(recipeId)
            }
        }
    }
}
