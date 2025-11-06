package ai.devrev.sdk.sample.viewmodel

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.addSessionProperties
import ai.devrev.sdk.endTimer
import ai.devrev.sdk.isRecording
import ai.devrev.sdk.pauseRecording
import ai.devrev.sdk.processAllOnDemandSessions
import ai.devrev.sdk.resumeAllMonitoring
import ai.devrev.sdk.resumeRecording
import ai.devrev.sdk.startRecording
import ai.devrev.sdk.startTimer
import ai.devrev.sdk.stopAllMonitoring
import ai.devrev.sdk.stopRecording
import ai.devrev.sdk.trackScreenName
import android.content.Context
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

    fun addSessionProperties(properties: HashMap<String, Any>) {
        DevRev.addSessionProperties(properties)
    }

    fun startTimer(name: String, properties: HashMap<String, String>) {
        DevRev.startTimer(name, properties)
    }

    fun endTimer(name: String, properties: HashMap<String, String>) {
        DevRev.endTimer(name, properties)
    }
}
