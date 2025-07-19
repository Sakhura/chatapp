package com.sakhura.chatapp.presentation.viewmodel

import androidx.lifecycle.*
import com.sakhura.chatapp.domain.usecase.ConectarChatUseCase
import com.sakhura.chatapp.domain.usecase.EnviarMensajeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conectar: ConectarChatUseCase,
    private val enviar: EnviarMensajeUseCase
) : ViewModel() {

    private val _mensajes = MutableLiveData<MutableList<String>>(mutableListOf())
    val mensajes: LiveData<MutableList<String>> = _mensajes

    fun conectarWebSocket(salaId: String) {
        viewModelScope.launch {
            // cargar mensajes locales
            val mensajesLocales = chatLocal.obtenerMensajes(salaId)
            _mensajes.value = mensajesLocales.map { "${it.remitente}: ${it.contenido}" }.toMutableList()

            conectar.conectar(salaId) { nuevoMensaje ->
                viewModelScope.launch {
                    _mensajes.value?.add(nuevoMensaje)
                    _mensajes.postValue(_mensajes.value)

                    chatLocal.guardarMensaje(
                        Mensaje(
                            contenido = nuevoMensaje,
                            remitente = "otro", // simplificado
                            timestamp = System.currentTimeMillis()
                        ),
                        salaId
                    )
                }
            }
        }
    }

    fun enviarMensaje(texto: String, salaId: String) {
        enviar.enviar(texto)
        val mensaje = "Yo: $texto"
        _mensajes.value?.add(mensaje)
        _mensajes.postValue(_mensajes.value)

        viewModelScope.launch {
            chatLocal.guardarMensaje(
                Mensaje(
                    contenido = texto,
                    remitente = "yo",
                    timestamp = System.currentTimeMillis()
                ),
                salaId
            )
        }
    }
    private suspend fun cargarMensajesLocalmente(salaId: String) {
        val mensajes = chatLocal.obtenerMensajes(salaId)
        _mensajes.postValue(mensajes.map {
            "${it.remitente}: ${it.contenido} (${it.estado.name.lowercase()})"
        }.toMutableList())
    }
}
