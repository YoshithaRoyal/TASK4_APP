package com.example.task4.billing


    import android.content.Context
    import androidx.core.content.edit

    class PurchaseStore(context: Context) {
        private val prefs = context.getSharedPreferences("purchases", Context.MODE_PRIVATE)

        var isPremium: Boolean
            get() = prefs.getBoolean("isPremium", false)
            set(value) = prefs.edit { putBoolean("isPremium", value) }

        var coins: Int
            get() = prefs.getInt("coins", 0)
            set(value) = prefs.edit { putInt("coins", value) }
    }

