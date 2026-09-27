package com.example.data

import com.example.domain.model.StaffMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object StaffManager {
    private val _staffList = MutableStateFlow(
        listOf(
            StaffMember(1, "Kudehwa Admin", "+255 625 169 441", "ADMIN", "admin123", "+255 625 169 441", true, true, true, true),
            StaffMember(2, "Joseph driver", "+255 710 825 917", "DRIVER", "1234", "+255 710 825 917", false, false, true, false),
            StaffMember(3, "Vedastus Staff", "+255 766 480 515", "STAFF", "1234", "+255 766 480 515", true, true, false, false)
        )
    )
    val staffList: StateFlow<List<StaffMember>> = _staffList.asStateFlow()

    fun addStaff(name: String, phone: String, role: String, password: String = "1234", whatsappNumber: String = phone) {
        val newMember = StaffMember(
            id = (System.currentTimeMillis() % 100000).toInt(),
            name = name,
            phone = phone,
            role = role,
            password = if (password.isNotBlank()) password else "1234",
            whatsappNumber = if (whatsappNumber.isNotBlank()) whatsappNumber else phone,
            canManageProducts = role == "ADMIN" || role == "STAFF",
            canManageOrders = role == "ADMIN" || role == "STAFF",
            canManageDelivery = role == "ADMIN" || role == "DRIVER",
            canViewReports = role == "ADMIN"
        )
        _staffList.value = _staffList.value + newMember
    }

    fun updateStaff(id: Int, name: String, phone: String, whatsappNumber: String, password: String) {
        _staffList.value = _staffList.value.map {
            if (it.id == id) {
                it.copy(
                    name = if (name.isNotBlank()) name else it.name,
                    phone = if (phone.isNotBlank()) phone else it.phone,
                    whatsappNumber = if (whatsappNumber.isNotBlank()) whatsappNumber else it.whatsappNumber,
                    password = if (password.isNotBlank()) password else it.password
                )
            } else it
        }
    }

    fun updateStaffPermissions(id: Int, products: Boolean, orders: Boolean, delivery: Boolean, reports: Boolean) {
        _staffList.value = _staffList.value.map {
            if (it.id == id) {
                it.copy(
                    canManageProducts = products,
                    canManageOrders = orders,
                    canManageDelivery = delivery,
                    canViewReports = reports
                )
            } else it
        }
    }

    fun updateStaffPassword(id: Int, newPassword: String) {
        _staffList.value = _staffList.value.map {
            if (it.id == id) it.copy(password = newPassword) else it
        }
    }

    fun updateStaffWhatsApp(id: Int, newWhatsapp: String) {
        _staffList.value = _staffList.value.map {
            if (it.id == id) it.copy(whatsappNumber = newWhatsapp) else it
        }
    }

    fun deleteStaff(id: Int) {
        _staffList.value = _staffList.value.filter { it.id != id }
    }
}
