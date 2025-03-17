package com.cmc.aqualis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AddWaterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "100ml d'eau ajoutés !", Toast.LENGTH_SHORT).show()
    }
}
