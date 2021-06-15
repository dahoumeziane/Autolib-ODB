package com.crewmates.autolibodb.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crewmates.autolibodb.repository.Repository
import com.crewmates.autolibodb.viewModel.MainViewModel


class MainViewModelFactory(private val  repository: Repository) : ViewModelProvider.Factory{


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}