package ru.fefu.task_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import ru.fefu.task_3.data.AnimeRepository
import ru.fefu.task_3.network.RetrofitClient
import ru.fefu.task_3.ui.navigation.NavGraph
import ru.fefu.task_3.ui.theme.Task3Theme
import ru.fefu.task_3.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val api = RetrofitClient.api
        val repository = AnimeRepository(api)
        val viewModelFactory = MainViewModel.Factory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            Task3Theme {
                NavGraph(viewModel = viewModel)
            }
        }
    }
}