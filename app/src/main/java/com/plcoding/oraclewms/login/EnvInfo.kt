package com.plcoding.oraclewms.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class EnvInfo(var host: MutableState<String> = mutableStateOf(""),
                   var port: MutableState<String> = mutableStateOf(""),
                   var userName: MutableState<String> = mutableStateOf(""),
                   var password: MutableState<String> = mutableStateOf(""),
                   var name: MutableState<String> = mutableStateOf(""),
                   var description: MutableState<String> = mutableStateOf(""))
