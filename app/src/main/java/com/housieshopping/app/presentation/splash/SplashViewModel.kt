package com.housieshopping.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.data.local.UserPreferencesRepository
import com.housieshopping.app.navigation.ScreenRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent

    init {
        checkStartDestination()
    }

    private fun checkStartDestination() {
        viewModelScope.launch {
            delay(1500) // Splash animation duration
            val onboardingCompleted = userPreferencesRepository.isOnboardingCompleted.first()
            val token = userPreferencesRepository.authToken.first()

            val route = when {
                !onboardingCompleted -> ScreenRoute.Onboarding.route
                token.isNull_or_empty() -> ScreenRoute.Home.route // Allow guest browsing
                else -> ScreenRoute.Home.route
            }
            _navigationEvent.emit(route)
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()
}
