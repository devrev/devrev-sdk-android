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
            appId = "DvRvStPZG9uOmNvcmU6ZHZydi11cy0xOmRldm8vM2ZBSEVDOnBsdWdfc2V0dGluZy8xX198fF9fMjAyNC0wNy0yOSAwOTozMjoxNC4xNjU1Mjc4NTggKzAwMDAgVVRDxlxendsDvRv",
        )

        DevRev.setShouldDismissModalsOnOpenLink(false)
    }
}
