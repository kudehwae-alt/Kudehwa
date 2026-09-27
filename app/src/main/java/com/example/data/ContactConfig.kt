package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ContactConfig {
    private val _phone = MutableStateFlow("+255 625 169 441")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _whatsapp = MutableStateFlow("255625169441")
    val whatsapp: StateFlow<String> = _whatsapp.asStateFlow()

    private val _whatsappMessage = MutableStateFlow("Habari Kudehwa Phone Store, nahitaji msaada / nataka kununua simu")
    val whatsappMessage: StateFlow<String> = _whatsappMessage.asStateFlow()

    private val _instagram = MutableStateFlow("https://instagram.com/kudehwa_phone_store")
    val instagram: StateFlow<String> = _instagram.asStateFlow()

    private val _facebook = MutableStateFlow("https://facebook.com/kudehwaphonestore")
    val facebook: StateFlow<String> = _facebook.asStateFlow()

    private val _tiktok = MutableStateFlow("https://tiktok.com/@kudehwaphones")
    val tiktok: StateFlow<String> = _tiktok.asStateFlow()

    private val _email = MutableStateFlow("info@kudehwaphones.co.tz")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _address = MutableStateFlow("Kariakoo, Dar es Salaam - Tanzania")
    val address: StateFlow<String> = _address.asStateFlow()

    fun updateContacts(
        newPhone: String,
        newWhatsapp: String,
        newWhatsappMessage: String,
        newInstagram: String,
        newFacebook: String,
        newTiktok: String,
        newEmail: String,
        newAddress: String
    ) {
        if (newPhone.isNotBlank()) _phone.value = newPhone
        if (newWhatsapp.isNotBlank()) _whatsapp.value = newWhatsapp
        if (newWhatsappMessage.isNotBlank()) _whatsappMessage.value = newWhatsappMessage
        if (newInstagram.isNotBlank()) _instagram.value = newInstagram
        if (newFacebook.isNotBlank()) _facebook.value = newFacebook
        if (newTiktok.isNotBlank()) _tiktok.value = newTiktok
        if (newEmail.isNotBlank()) _email.value = newEmail
        if (newAddress.isNotBlank()) _address.value = newAddress
    }

    fun openWhatsApp(context: android.content.Context, phone: String, message: String) {
        val cleanPhone = phone.replace("+", "").replace(" ", "").trim()
        val encodedMsg = android.net.Uri.encode(message)
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("whatsapp://send?phone=$cleanPhone&text=$encodedMsg"))
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://wa.me/$cleanPhone?text=$encodedMsg"))
            context.startActivity(intent)
        }
    }
}
