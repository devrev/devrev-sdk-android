package ai.devrev.sdk.sample.viewmodel

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.showSupport
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SupportChatViewModel(): ViewModel() {

    fun createSupportConversation(context: Context) {
        DevRev.createSupportConversation(context)
    }

    fun showSupport(context: Context) {
        DevRev.showSupport(context)
    }

}