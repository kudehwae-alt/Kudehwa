package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.OrderEntity
import com.example.data.local.PaymentEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `test order tracking`() = runBlocking {
        val order = OrderEntity("KUD-123", "John Doe", "1234567890", 100.0, "PLACED")
        db.orderDao().createOrder(order, emptyList())
        val found = db.orderDao().getOrderByIdAndPhone("KUD-123", "1234567890")
        assertNotNull(found)
        assertEquals("John Doe", found?.customerName)
        Unit
    }

    @Test
    fun `test total sales`() = runBlocking {
        db.paymentDao().insertPayment(PaymentEntity("P1", "KUD-123", 50.0, "CARD", "PAID", null))
        db.paymentDao().insertPayment(PaymentEntity("P2", "KUD-124", 50.0, "CARD", "PAID", null))
        val total = db.analyticsDao().getTotalSales() ?: 0.0
        assertEquals(100.0, total, 0.0)
        Unit
    }
}
