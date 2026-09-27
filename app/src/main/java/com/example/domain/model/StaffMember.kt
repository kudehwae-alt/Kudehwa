package com.example.domain.model

data class StaffMember(
    val id: Int,
    val name: String,
    val phone: String,
    val role: String, // "ADMIN", "STAFF", "DRIVER"
    val password: String = "1234",
    val whatsappNumber: String = phone,
    val canManageProducts: Boolean = true,
    val canManageOrders: Boolean = true,
    val canManageDelivery: Boolean = true,
    val canViewReports: Boolean = true
)
