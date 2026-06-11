package com.example

import android.app.Application
import com.example.data.SicBoDatabase
import com.example.data.SicBoRepository

class SicBoApplication : Application() {
    val database by lazy { SicBoDatabase.getDatabase(this) }
    val repository by lazy { SicBoRepository(database.sicBoDao()) }
}
