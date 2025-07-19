package com.sakhura.chatapp.data.repository

import com.sakhura.chatapp.data.datasource.websocket.ChatWebSocketClient
import com.sakhura.chatapp.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val cliente: ChatWebSocketClient
) : ChatRepository {

    override fun conectar(salaId: String, onMensaje: (String) -> Unit) {
        val url = "wss://echo.websocket.org" // puedes reemplazar con backend real
        cliente.connect(url, object : ChatWebSocketClient.ChatWebSocketListener() {
            override fun onNuevoMensaje(mensaje: String) {
                onMensaje(mensaje)
            }
        })
    }

    override fun enviar(mensaje: String) {
        cliente.sendMessage(mensaje)
    }

    override fun cerrar() {
        cliente.close()
    }
}
