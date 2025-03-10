package ai.devrev.sdk.sample.viewmodel

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.isMonitoringEnabled
import ai.devrev.sdk.isRecording
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var title: MutableLiveData<String> = MutableLiveData("DevRev SDK")

    private var _isUserIdentified = MutableLiveData<Boolean>()
    val isUserIdentified: LiveData<Boolean> get() = _isUserIdentified

    private var _isMonitoringEnabled = MutableLiveData<Boolean>()
    val isMonitoringEnabled: LiveData<Boolean> get() = _isMonitoringEnabled

    private var _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean> get() = _isRecording

    init {
        _isUserIdentified.value = DevRev.isUserIdentified
        _isRecording.value = DevRev.isRecording
        _isMonitoringEnabled.value = DevRev.isMonitoringEnabled
    }

    fun resetTitle() {
        title.value = "DevRev SDK"
    }

    fun changeTitle(newTitle: String) {
        title.value = newTitle
    }

}

