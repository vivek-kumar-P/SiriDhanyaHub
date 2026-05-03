package com.mindmatrix.siridhanyahub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.siridhanyahub.data.local.model.FavouriteRecipe
import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity
import com.mindmatrix.siridhanyahub.data.repository.FavouriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) : ViewModel() {
    private val _selectedRecipe = MutableStateFlow<RecipeEntity?>(null)
    val selectedRecipe: StateFlow<RecipeEntity?> = _selectedRecipe.asStateFlow()

    val favourites: StateFlow<List<FavouriteRecipe>> = favouriteRepository
        .observeFavouriteRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun save(recipeId: Int) {
        viewModelScope.launch { favouriteRepository.save(recipeId) }
    }

    fun remove(recipeId: Int) {
        viewModelScope.launch { favouriteRepository.remove(recipeId) }
    }

    fun openRecipe(recipe: RecipeEntity) {
        _selectedRecipe.value = recipe
    }

    fun closeRecipe() {
        _selectedRecipe.value = null
    }
}
