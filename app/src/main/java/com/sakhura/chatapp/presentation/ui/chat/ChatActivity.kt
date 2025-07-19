package com.sakhura.chatapp.presentation.ui.chat

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sakhura.chatapp.databinding.ActivityChatBinding
import com.sakhura.chatapp.presentation.adapter.MensajeAdapter
import com.sakhura.chatapp.presentation.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: MensajeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val salaId = intent.getStringExtra("salaId") ?: "1"

        adapter = MensajeAdapter()
        binding.rvMensajes.layoutManager = LinearLayoutManager(this)
        binding.rvMensajes.adapter = adapter

        viewModel.mensajes.observe(this) {
            adapter.submitList(it.toList()) // copia para actualizar
            binding.rvMensajes.scrollToPosition(it.size - 1)
        }

        viewModel.conectarWebSocket(salaId)

        binding.btnEnviar.setOnClickListener {
            val texto = binding.etMensaje.text.toString()
            if (texto.isNotBlank()) {
                viewModel.enviarMensaje(texto)
                binding.etMensaje.text.clear()
            }
        }
    }
}
