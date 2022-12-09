package com.reg.homeworks

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.task.homework_4.data.local.preferences.PreferencesManager

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }
    private val preferencesManager by lazy {
        PreferencesManager(
            this.getSharedPreferences(
                "chat.preferences",
                Context.MODE_PRIVATE
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defineStartDestinationDependingOnTheAuthenticationProcess()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.authFragment,
                R.id.chatsFragment

            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }


    private fun defineStartDestinationDependingOnTheAuthenticationProcess() {
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

            when (preferencesManager.isAuthenticated) {
                true ->
                    navGraph.setStartDestination(R.id.authFragment)
                false ->
                    navGraph.setStartDestination(R.id.authFragment)
            }
            navController.graph = navGraph
        }
    }
