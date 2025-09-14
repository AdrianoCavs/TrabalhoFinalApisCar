package com.cavstecnologia.trabalhofinalapiscar

import android.app.Application
import com.cavstecnologia.trabalhofinalapiscar.database.DatabaseBuilder

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        init();
    }

    private fun init(){
        DatabaseBuilder.getInstance(this);
    }
}