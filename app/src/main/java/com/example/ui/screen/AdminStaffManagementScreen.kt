package com.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.StaffManager
import com.example.domain.model.StaffMember
import com.example.ui.theme.KudehwaSuccess
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStaffManagementScreen(
    onBackClick: () -> Unit = {}
) {
    val staffList by StaffManager.staffList.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var whatsappInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("1234") }
    var selectedRole by remember { mutableStateOf("DRIVER") }

    val scope = rememberCoroutineScope()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var oldPassInput by remember { mutableStateOf("") }
    var newPassInput by remember { mutableStateOf("") }
    var passError by remember { mutableStateOf<String?>(null) }
    var passSuccess by remember { mutableStateOf<String?>(null) }
    val currentAdminPass by com.example.data.AdminPasswordManager.adminPassword.collectAsState()

    // Staff/Driver edit dialog state
    var selectedStaffForPass by remember { mutableStateOf<StaffMember?>(null) }
    var staffNewNameInput by remember { mutableStateOf("") }
    var staffNewPhoneInput by remember { mutableStateOf("") }
    var staffNewPassInput by remember { mutableStateOf("") }
    var staffNewWaInput by remember { mutableStateOf("") }

    // Access Control Dialog State
    var selectedStaffForAccess by remember { mutableStateOf<StaffMember?>(null) }
    var accessProducts by remember { mutableStateOf(true) }
    var accessOrders by remember { mutableStateOf(true) }
    var accessDelivery by remember { mutableStateOf(true) }
    var accessReports by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact & Staff Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Rudi")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    nameInput = ""
                    phoneInput = ""
                    whatsappInput = ""
                    passwordInput = "1234"
                    selectedRole = "DRIVER"
                    showAddDialog = true 
                },
                containerColor = KudehwaSuccess
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ongeza Mtu", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    oldPassInput = ""
                    newPassInput = ""
                    passError = null
                    passSuccess = null
                    showPasswordDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Badilisha Password ya Admin")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Orodha ya Timu (${staffList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (staffList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Hakuna rekodi zilizopatikana.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(staffList, key = { it.id }) { staff ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(staff.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Simu: ${staff.phone} | WA: ${staff.whatsappNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("PIN/Pass: ${staff.password}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    // Permissions summary
                                    val perms = mutableListOf<String>()
                                    if (staff.canManageProducts) perms.add("Bidhaa")
                                    if (staff.canManageOrders) perms.add("Oda")
                                    if (staff.canManageDelivery) perms.add("Delivery")
                                    if (staff.canViewReports) perms.add("Ripoti")
                                    Text("Access: ${if (perms.isEmpty()) "Hakuna" else perms.joinToString(", ")}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = when (staff.role) {
                                            "ADMIN" -> Color(0xFFD32F2F).copy(alpha = 0.15f)
                                            "DRIVER" -> Color(0xFF1976D2).copy(alpha = 0.15f)
                                            else -> Color(0xFF388E3C).copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = staff.role,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = when (staff.role) {
                                                "ADMIN" -> Color(0xFFD32F2F)
                                                "DRIVER" -> Color(0xFF1976D2)
                                                else -> Color(0xFF388E3C)
                                            }
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = {
                                        selectedStaffForAccess = staff
                                        accessProducts = staff.canManageProducts
                                        accessOrders = staff.canManageOrders
                                        accessDelivery = staff.canManageDelivery
                                        accessReports = staff.canViewReports
                                    }) {
                                        Icon(Icons.Default.Security, contentDescription = "Access Control", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedStaffForPass = staff
                                        staffNewNameInput = staff.name
                                        staffNewPhoneInput = staff.phone
                                        staffNewPassInput = staff.password
                                        staffNewWaInput = staff.whatsappNumber
                                    }) {
                                        Icon(Icons.Default.Lock, contentDescription = "Hariri", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        StaffManager.deleteStaff(staff.id)
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Futa", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Ongeza Admin, Driver au Staff Mpya") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Jina Kamili") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Namba ya Simu (+255...)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = whatsappInput,
                            onValueChange = { whatsappInput = it },
                            label = { Text("Namba ya WhatsApp (+255...)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password / PIN ya Kuingia") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Chagua Nafasi (Role):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        val roles = listOf("ADMIN", "DRIVER", "STAFF")
                        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(roles) { r ->
                                FilterChip(
                                    selected = selectedRole == r,
                                    onClick = { selectedRole = r },
                                    label = { Text(r) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nameInput.isNotBlank() && phoneInput.isNotBlank()) {
                                StaffManager.addStaff(nameInput, phoneInput, selectedRole, passwordInput, if (whatsappInput.isBlank()) phoneInput else whatsappInput)
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KudehwaSuccess)
                    ) {
                        Text("Hifadhi")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Ghairi")
                    }
                }
            )
        }

        // Access Control Dialog
        if (selectedStaffForAccess != null) {
            val st = selectedStaffForAccess!!
            AlertDialog(
                onDismissRequest = { selectedStaffForAccess = null },
                title = { Text("Access Control: ${st.name}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Chagua ruhusa anazoweza kuaccess mfanyakazi huyu:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = accessProducts, onCheckedChange = { accessProducts = it })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dhibiti Bidhaa & Stoo (Products)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = accessOrders, onCheckedChange = { accessOrders = it })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dhibiti Oda (Orders)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = accessDelivery, onCheckedChange = { accessDelivery = it })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dhibiti Usafirishaji (Delivery)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = accessReports, onCheckedChange = { accessReports = it })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tazama Ripoti na Takwimu (Reports)")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        StaffManager.updateStaffPermissions(st.id, accessProducts, accessOrders, accessDelivery, accessReports)
                        selectedStaffForAccess = null
                    }) {
                        Text("Hifadhi Ruhusa")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedStaffForAccess = null }) {
                        Text("Ghairi")
                    }
                }
            )
        }

        // Dialog for editing staff/driver name, phone, WhatsApp & password
        if (selectedStaffForPass != null) {
            val st = selectedStaffForPass!!
            AlertDialog(
                onDismissRequest = { selectedStaffForPass = null },
                title = { Text("Hariri Taarifa za ${st.name}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = staffNewNameInput,
                            onValueChange = { staffNewNameInput = it },
                            label = { Text("Jina Kamili") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = staffNewPhoneInput,
                            onValueChange = { staffNewPhoneInput = it },
                            label = { Text("Namba ya Simu") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = staffNewPassInput,
                            onValueChange = { staffNewPassInput = it },
                            label = { Text("Password Mpya / PIN") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = staffNewWaInput,
                            onValueChange = { staffNewWaInput = it },
                            label = { Text("WhatsApp Namba") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        StaffManager.updateStaff(
                            st.id,
                            staffNewNameInput,
                            staffNewPhoneInput,
                            staffNewWaInput,
                            staffNewPassInput
                        )
                        selectedStaffForPass = null
                    }) {
                        Text("Hifadhi")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedStaffForPass = null }) {
                        Text("Ghairi")
                    }
                }
            )
        }

        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                title = { Text("Badilisha Password ya Admin") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = oldPassInput,
                            onValueChange = { oldPassInput = it },
                            label = { Text("Password ya Zamani") },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newPassInput,
                            onValueChange = { newPassInput = it },
                            label = { Text("Password Mpya (Angalau herufi 4)") },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        val err = passError
                        if (err != null) {
                            Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        val succ = passSuccess
                        if (succ != null) {
                            Text(succ, color = KudehwaSuccess, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (oldPassInput != currentAdminPass) {
                            passError = "Password ya zamani siyo sahihi"
                        } else if (newPassInput.length < 4) {
                            passError = "Password mpya lazima iwe na angalau herufi 4"
                        } else {
                            com.example.data.AdminPasswordManager.updatePassword(newPassInput)
                            passError = null
                            passSuccess = "Password imebadilishwa mafanikio!"
                            scope.launch {
                                kotlinx.coroutines.delay(1000)
                                showPasswordDialog = false
                            }
                        }
                    }) {
                        Text("Hifadhi")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPasswordDialog = false }) {
                        Text("Funga")
                    }
                }
            )
        }
    }
}
