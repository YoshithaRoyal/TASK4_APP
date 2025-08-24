package com.example.task4// <- use your actual package name here

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.task4.billing.BillingClientManager
import com.example.task4.billing.PurchaseStore

class MainActivity : ComponentActivity() {
    private lateinit var billing: BillingClientManager
    private lateinit var store: PurchaseStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        store = PurchaseStore(this)
        billing = BillingClientManager(this, store).also { it.start() }

        // Reactive states
        var isPremium by mutableStateOf(store.isPremium)
        var coins by mutableStateOf(store.coins)

        // Update UI when purchases change
        billing.onEntitlementsChanged = { premium, coinsNow ->
            isPremium = premium
            coins = coinsNow
        }

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(if (isPremium) "✅ Premium: Unlocked" else "🔒 Premium: Locked")
                    Text("💰 Coins: $coins")

                    Button(onClick = {
                        // Dummy login action (replace with Firebase Auth later)
                        Toast.makeText(this@MainActivity, "Login clicked", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Login (dummy)")
                    }

                    Button(onClick = {
                        billing.buy(this@MainActivity, BillingClientManager.PRODUCT_PREMIUM)
                    }) { Text("Buy Premium (non-consumable)") }

                    Button(onClick = {
                        billing.buy(this@MainActivity, BillingClientManager.PRODUCT_COINS_100)
                    }) { Text("Buy 100 Coins (consumable)") }
                }


}
}
}

override fun onDestroy() {
    super.onDestroy()
    billing.destroy()
}
}
