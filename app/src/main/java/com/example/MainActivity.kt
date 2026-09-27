package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.repository.CartRepository
import com.example.data.repository.InventoryRepository
import com.example.data.repository.OrderRepository
import com.example.data.repository.PaymentRepository
import com.example.data.repository.ProductRepository
import com.example.ui.screen.AdminDashboardScreen
import com.example.ui.screen.AdminLoginScreen
import com.example.ui.screen.AdminOrderScreen
import com.example.ui.screen.AdminProductManagementScreen
import com.example.ui.screen.AdminReportsScreen
import com.example.ui.screen.AdminStaffManagementScreen
import com.example.ui.screen.CartScreen
import com.example.ui.screen.CheckoutScreen
import com.example.ui.screen.HomeScreen
import com.example.ui.screen.OrderTrackingScreen
import com.example.ui.screen.ProductDetailScreen
import com.example.ui.screen.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AdminOrderViewModel
import com.example.ui.viewmodel.AnalyticsViewModel
import com.example.ui.viewmodel.CartViewModel
import com.example.ui.viewmodel.InventoryViewModel
import com.example.ui.viewmodel.OrderTrackingViewModel
import com.example.ui.viewmodel.ReportViewModel
import com.example.ui.viewmodel.StorefrontViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room Database
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "kudehwa-db")
            .fallbackToDestructiveMigration()
            .build()

        // Seed data if empty (Phones, Accessories, Categories)
        lifecycleScope.launch(Dispatchers.IO) {
            DatabaseSeeder.seedDatabaseIfEmpty(db)
        }

        // Repositories
        val productRepository = ProductRepository(db.productDao(), db.categoryDao())
        val cartRepository = CartRepository(db.cartDao())
        val orderRepository = OrderRepository(db)
        val paymentRepository = PaymentRepository(db)
        val inventoryRepository = InventoryRepository(db)

        // ViewModels
        val storefrontViewModel = StorefrontViewModel(productRepository, cartRepository)
        val cartViewModel = CartViewModel(cartRepository)
        val adminOrderViewModel = AdminOrderViewModel(orderRepository)
        val orderTrackingViewModel = OrderTrackingViewModel(orderRepository)
        val analyticsViewModel = AnalyticsViewModel(db)
        val reportViewModel = ReportViewModel(db.analyticsDao())

        enableEdgeToEdge()
        setContent {
            val currentTheme by com.example.data.ThemeManager.currentTheme.collectAsState()
            MyApplicationTheme(appTheme = currentTheme) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(
                            onTimeout = {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            viewModel = storefrontViewModel,
                            onProductClick = { productId ->
                                navController.navigate("detail/$productId")
                            },
                            onCartClick = {
                                navController.navigate("cart")
                            },
                            onTrackOrderClick = {
                                navController.navigate("track-order")
                            },
                            onAdminClick = {
                                navController.navigate("admin/login")
                            }
                        )
                    }

                    composable("admin/login") {
                        AdminLoginScreen(
                            onBackClick = { navController.popBackStack() },
                            onLoginSuccess = {
                                navController.navigate("admin/orders") {
                                    popUpTo("home")
                                }
                            }
                        )
                    }

                    composable(
                        route = "detail/{productId}",
                        arguments = listOf(navArgument("productId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                        ProductDetailScreen(
                            productId = productId,
                            productRepository = productRepository,
                            cartRepository = cartRepository,
                            onBackClick = { navController.popBackStack() },
                            onCartClick = { navController.navigate("cart") },
                            onCheckoutClick = { navController.navigate("checkout") }
                        )
                    }

                    composable("cart") {
                        CartScreen(
                            viewModel = cartViewModel,
                            onBackClick = { navController.popBackStack() },
                            onCheckoutClick = { navController.navigate("checkout") },
                            onContinueShoppingClick = { navController.popBackStack() }
                        )
                    }

                    composable("checkout") {
                        CheckoutScreen(
                            cartViewModel = cartViewModel,
                            orderRepository = orderRepository,
                            onBackClick = { navController.popBackStack() },
                            onOrderPlaced = { orderId ->
                                navController.navigate("track-order?orderId=$orderId") {
                                    popUpTo("home") { inclusive = false }
                                }
                            }
                        )
                    }

                    composable(
                        route = "track-order?orderId={orderId}",
                        arguments = listOf(
                            navArgument("orderId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId")
                        OrderTrackingScreen(
                            viewModel = orderTrackingViewModel,
                            initialOrderId = orderId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("admin/orders") {
                        AdminOrderScreen(
                            viewModel = adminOrderViewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateDashboard = { navController.navigate("admin/dashboard") },
                            onNavigateReports = { navController.navigate("admin/reports") }
                        )
                    }

                    composable("admin/dashboard") {
                        AdminDashboardScreen(
                            viewModel = analyticsViewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateProducts = { navController.navigate("admin/products") },
                            onNavigateOrders = { navController.navigate("admin/orders") },
                            onNavigateStaff = { navController.navigate("admin/staff") },
                            onNavigateReports = { navController.navigate("admin/reports") }
                        )
                    }

                    composable("admin/products") {
                        AdminProductManagementScreen(
                            productRepository = productRepository,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("admin/staff") {
                        AdminStaffManagementScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("admin/reports") {
                        AdminReportsScreen(
                            viewModel = reportViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
