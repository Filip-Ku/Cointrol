package com.example.cointrol.ui.income

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IncomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Tutaj będą wpływy (zarobki)"
    }
    val text: LiveData<String> = _text
}