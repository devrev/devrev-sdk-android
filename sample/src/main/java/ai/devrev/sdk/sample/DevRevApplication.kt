/*
 * Copyright (c) 2022 DevRev Inc. All rights reserved.
 */

package ai.devrev.sdk.sample

import ai.devrev.sdk.DevRev
import android.app.Application

class DevRevApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DevRev.configure(
            context = this,
            appId = TODO("Add AppID"),
            secret = TODO("Add secret"),
            organizationSlug = TODO("Add organization slug")
        )
    }
}
