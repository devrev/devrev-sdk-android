package ai.devrev.sdk.sample.viewmodel

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.isRecording
import ai.devrev.sdk.pauseRecording
import ai.devrev.sdk.processAllOnDemandSessions
import ai.devrev.sdk.resumeAllMonitoring
import ai.devrev.sdk.resumeRecording
import ai.devrev.sdk.startRecording
import ai.devrev.sdk.stopAllMonitoring
import ai.devrev.sdk.stopRecording
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SessionAnalyticsViewModel(): ViewModel() {

    fun startRecording(context: Context) {
        DevRev.startRecording(context)
        if(!DevRev.isRecording)
            throw Exception("Start recording failed")
    }

    fun stopRecording() {
        DevRev.stopRecording()
        if(DevRev.isRecording)
            throw Exception("Stop recording failed")
    }

    fun pauseRecording() {
        DevRev.pauseRecording()
    }

    fun resumeRecording() {
        DevRev.resumeRecording()
    }

    fun processAllOnDemandSessions() {
        DevRev.processAllOnDemandSessions()
    }

    fun stopAllMonitoring() {
        DevRev.stopAllMonitoring()
    }

    fun resumeAllMonitoring() {
        DevRev.resumeAllMonitoring()
    }
}