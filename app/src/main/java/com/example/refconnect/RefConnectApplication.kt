package com.example.refconnect

import android.app.Application
import com.example.refconnect.data.local.AppDatabase
import com.example.refconnect.data.local.DatabaseSeeder
import com.example.refconnect.data.local.PreferencesManager
import com.example.refconnect.repository.RefConnectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RefConnectApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Lazy initialization ensures database is created only when needed
    val database by lazy { AppDatabase.getDatabase(this) }
    val preferencesManager by lazy { PreferencesManager(this) }
    val repository by lazy { RefConnectRepository(database, preferencesManager) }

    override fun onCreate() {
        super.onCreate()

        // Initialize database with seed data on first launch using IO dispatcher
        applicationScope.launch(Dispatchers.IO) {
            try {
                val seeder = DatabaseSeeder(database)
                seeder.seedDatabase()
            } catch (e: Exception) {
                // Log error but don't crash app during development
                e.printStackTrace()
            }
        }
    }
}