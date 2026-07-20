package com.faturaapp.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SetupScreen(
    onSetupConcluido: () -> Unit,
    viewModel: SetupViewModel = viewModel(),
) {
    val url by viewModel.url.collectAsState()
    val testState by viewModel.testState.collectAsState()
    val setupConcluido by viewModel.setupConcluido.collectAsState()

    LaunchedEffect(setupConcluido) {
        if (setupConcluido) onSetupConcluido()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Configurar backend",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Endereço do seu servidor (ex: http://100.x.x.x:8000)",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )

        OutlinedTextField(
            value = url,
            onValueChange = viewModel::onUrlChange,
            label = { Text("URL do backend") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        when (val estado = testState) {
            is ConnectionTestState.Failure -> Text(
                text = estado.mensagem,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
            is ConnectionTestState.Success -> Text(
                text = "Conectado com sucesso!",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
            )
            else -> {}
        }

        Button(
            onClick = viewModel::testarConexao,
            enabled = testState != ConnectionTestState.Testing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            if (testState == ConnectionTestState.Testing) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            }
            Text("Testar conexão")
        }

        if (testState == ConnectionTestState.Success) {
            Button(
                onClick = viewModel::continuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text("Continuar")
            }
        }
    }
}
