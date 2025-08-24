package com.example.task4.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*

class BillingClientManager(
    private val context: Context,
    private val store: PurchaseStore
) : PurchasesUpdatedListener {

    private var billingClient: BillingClient

    var onEntitlementsChanged: ((Boolean, Int) -> Unit)? = null

    companion object {
        const val PRODUCT_PREMIUM = "premium"
        const val PRODUCT_COINS_100 = "coins_100"
    }

    init {
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(this)
            .build()
    }

    fun start() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.w("Billing", "Service disconnected. Will retry.")
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("Billing", "Setup finished. Querying purchases...")
                    queryPurchases()
                }
            }
        })
    }

    fun buy(activity: Activity, productId: String) {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val productDetails = productDetailsList[0]

                val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()

                val flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(listOf(productDetailsParams))
                    .build()

                billingClient.launchBillingFlow(activity, flowParams)
            } else {
                Log.e("Billing", "Product not found: $productId")
            }
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("Billing", "Purchase canceled by user")
        } else {
            Log.w("Billing", "Purchase failed: ${result.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        when {
            purchase.products.contains(PRODUCT_PREMIUM) -> {
                store.isPremium = true
                onEntitlementsChanged?.invoke(true, store.coins)
            }
            purchase.products.contains(PRODUCT_COINS_100) -> {
                store.coins += 100
                onEntitlementsChanged?.invoke(store.isPremium, store.coins)
            }
        }

        if (!purchase.isAcknowledged) {
            val ackParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(ackParams) { result ->
                Log.d("Billing", "Purchase acknowledged: ${result.responseCode}")
            }
        }
    }

    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { _, purchases ->
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    fun destroy() {
        billingClient.endConnection()
    }
}


