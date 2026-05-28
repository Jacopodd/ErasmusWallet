package com.example.erasmuswallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.erasmuswallet.data.local.AppDatabase
import com.example.erasmuswallet.data.repository.AppRepository
import com.example.erasmuswallet.ui.AppViewModel
import com.example.erasmuswallet.ui.AppViewModelFactory
import com.example.erasmuswallet.ui.ErasmusWalletApp
import com.example.erasmuswallet.ui.theme.ErasmusWalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ErasmusWalletTheme {
                val repository = AppRepository(AppDatabase.get(applicationContext))
                val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(repository))
                ErasmusWalletApp(viewModel)
            }
        }
    }
}
