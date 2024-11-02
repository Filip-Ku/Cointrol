package com.example.cointrol.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SavingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Tutaj będzie: stan główny (kilka walut), ostatnie trzy wpływy i ostatnie trzy wydatki"
    }
    val text: LiveData<String> = _text
}