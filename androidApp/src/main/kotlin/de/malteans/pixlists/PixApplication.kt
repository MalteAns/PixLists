package de.malteans.pixlists

import android.app.Application
import de.malteans.pixlists.di.initKoin
import org.koin.android.ext.koin.androidContext

class PixApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin{
            androidContext(this@PixApplication)
        }
    }
}