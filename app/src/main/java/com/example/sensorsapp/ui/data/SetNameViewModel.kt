package com.example.sensorsapp.ui.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SetNameViewModel: ViewModel() {
    var name by mutableStateOf("")
        private set

    var showBottomSheetState by mutableStateOf(false)
        private set

    var isEmpty by mutableStateOf(false)

    fun showBottomSheet(){
        showBottomSheetState = true
    }

    fun updateName(input: String) {
        name = input
    }

    fun resetState(){
        showBottomSheetState = false
        //updateName("")
    }

    fun setError(value: Boolean){
        isEmpty = value
    }
}