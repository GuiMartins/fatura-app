package com.faturaapp.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faturaapp.data.PreferencesRepository
import com.faturaapp.data.model.Fatura
import com.faturaapp.data.network.ApiClientProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class DashboardState {
    data object Loading : DashboardState()
    data class Carregado(val faturas: List<Fatura>) : DashboardState()
    data class Erro(val mensagem: String) : DashboardState()
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository(application)

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    val temaPreferido: StateFlow<String> = preferencesRepository.temaPreferido.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PreferencesRepository.TEMA_SISTEMA,
    )

    init {
        carregarFaturas()
    }

    fun alternarTema() {
        val proximo = when (temaPreferido.value) {
            PreferencesRepository.TEMA_SISTEMA -> PreferencesRepository.TEMA_CLARO
            PreferencesRepository.TEMA_CLARO -> PreferencesRepository.TEMA_ESCURO
            else -> PreferencesRepository.TEMA_SISTEMA
        }
        viewModelScope.launch { preferencesRepository.setTemaPreferido(proximo) }
    }

    fun carregarFaturas() {
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            val backendUrl = preferencesRepository.backendUrl.first()
            if (backendUrl.isNullOrBlank()) {
                _state.value = DashboardState.Erro("Backend não configurado")
                return@launch
            }
            try {
                val faturas = ApiClientProvider.getApi(backendUrl).listarFaturas()
                _state.value = DashboardState.Carregado(faturas)
            } catch (e: Exception) {
                _state.value = DashboardState.Erro(e.message ?: "Erro ao carregar faturas")
            }
        }
    }
}
