/*
 * Copyright (c) 2022 DevRev Inc. All rights reserved.
 */

package ai.devrev.sdk.sample

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.model.FeatureConfiguration
import ai.devrev.sdk.model.SupportWidgetTheme
import android.app.Application
import com.google.firebase.FirebaseApp

class DevRevApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        DevRev.configure(
            context = this,
            appId = "APP_ID_HERE",
            featureConfiguration = FeatureConfiguration(
                enableFrameCapture = true,          //  screenshot capture enabled (default)
                autoStartRecording = true,          // Auto-start recording (default)
                prefersDialogMode = false,          // Use activity mode (default)
                supportWidgetTheme = SupportWidgetTheme(prefersSystemTheme = true),
            )
        )

        DevRev.setShouldDismissModalsOnOpenLink(false)
    }
}
