package com.example.cointrol.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OutcomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Tutaj będą wydatki"
    }
    val text: LiveData<String> = _text
}