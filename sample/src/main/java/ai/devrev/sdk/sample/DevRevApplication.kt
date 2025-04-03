/*
 * Copyright (c) 2022 DevRev Inc. All rights reserved.
 */

package ai.devrev.sdk.sample

import ai.devrev.sdk.DevRev
import android.app.Application
import com.google.firebase.FirebaseApp

class DevRevApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        DevRev.configure(
            context = this,
            appId = "APP_ID_HERE",
        )

        DevRev.setShouldDismissModalsOnOpenLink(false)
    }
}
