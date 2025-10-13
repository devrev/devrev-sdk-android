package ai.devrev.sdk.sample.viewmodel

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.model.Identity
import ai.devrev.sdk.sample.utils.DeviceInfo
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IdentificationViewModel(): ViewModel() {

    fun identifyUnverifiedUser(userId: String) {
        DevRev.identifyUnverifiedUser(Identity(userId = userId))
    }

    fun identifyVerifiedUser(userId: String, sessionToken: String) {
        DevRev.identifyVerifiedUser(userId = userId, sessionToken = sessionToken)
    }

    fun updateUser(identity: Identity) {
        DevRev.updateUser(identity = identity)
    }

    fun logout(context: Context) {
        val deviceId = DeviceInfo.getDeviceId(context)
        DevRev.logout(context, deviceId)
    }

}