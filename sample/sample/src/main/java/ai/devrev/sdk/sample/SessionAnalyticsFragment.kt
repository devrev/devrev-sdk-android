package ai.devrev.sdk.sample

import ai.devrev.sdk.sample.viewmodel.SessionAnalyticsViewModel
import ai.devrev.sdk.sample.viewmodel.SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class SessionAnalyticsFragment : Fragment() {

    private val viewModel: SessionAnalyticsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_session_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.trackScreen("Session Analytics")

        sharedViewModel.setInScreenTransitioning(false)

        val isRecordingCheckbox: CheckBox = view.findViewById(R.id.is_session_recorded)
        val isMonitoringEnabledCheckBox: CheckBox = view.findViewById(R.id.is_monitoring_enabled)
        val startRecordingButton: Button  = view.findViewById(R.id.start_recording_button)
        val stopRecordingButton: Button  = view.findViewById(R.id.stop_recording_button)
        val pauseRecordingButton: Button  = view.findViewById(R.id.pause_recording_button)
        val resumeRecordingButton: Button  = view.findViewById(R.id.resume_recording_button)
        val processAllDemandsSessions: Button  = view.findViewById(R.id.process_all_on_demand_session_button)
        val stopAllMonitoring: Button  = view.findViewById(R.id.stop_all_monitoring_button)
        val resumeAllMonitoring: Button  = view.findViewById(R.id.resume_all_monitoring_button)
        val startTimerWithPropertiesButton: Button = view.findViewById(R.id.start_timer_with_properties)
        val endTimerWithPropertiesButton: Button = view.findViewById(R.id.end_timer_with_properties)
        val unmaskedField: EditText = view.findViewById(R.id.manual_unmasked_item)

        unmaskedField.tag = "devrev-unmask"

        val openWebViewButton: Button = view.findViewById(R.id.open_webview_button)
        openWebViewButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, WebViewFragment())
                .addToBackStack(this::class.java.name)
                .commit()
        }

        val openComposeViewListButton: Button = view.findViewById(R.id.open_compose_view_list_button)
        openComposeViewListButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, ComposeViewListFragment())
                .addToBackStack(this::class.java.name)
                .commit()
        }

        val openRecyclerViewListButton: Button = view.findViewById(R.id.open_recyclerview_list_button)
        openRecyclerViewListButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, RecyclerViewListFragment())
                .addToBackStack(this::class.java.name)
                .commit()
        }

        val properties = HashMap<String, Any>()
        properties["state"] = "active"
        viewModel.addSessionProperties(properties)

        sharedViewModel.isMonitoringEnabled.observe(viewLifecycleOwner) { isMonitoringEnabled ->
            isMonitoringEnabledCheckBox.isChecked = isMonitoringEnabled
        }

        sharedViewModel.isRecording.observe(viewLifecycleOwner) { isRecording ->
            isRecordingCheckbox.isChecked = isRecording
        }

        startRecordingButton.setOnClickListener {
            try {
                viewModel.startRecording(requireContext())
                alertDialogBox(getString(R.string.recording_started), getString(R.string.started))
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.recording_start_failed), getString(R.string.start_error))
            }
        }

        stopRecordingButton.setOnClickListener {
            try {
                viewModel.stopRecording()
                alertDialogBox(getString(R.string.recording_stopped), getString(R.string.stopped))
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.recording_stop_failed), getString(R.string.stop_error))
            }
        }

        pauseRecordingButton.setOnClickListener {
            try {
                viewModel.pauseRecording()
                alertDialogBox(getString(R.string.recording_paused), getString(R.string.paused))
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.recording_pause_failed), getString(R.string.pause_error))
            }
        }

        resumeRecordingButton.setOnClickListener {
            try {
                viewModel.resumeRecording()
                alertDialogBox(getString(R.string.recording_resumed), getString(R.string.resumed))
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.recording_resume_failed), getString(R.string.resume_error))
            }
        }

        processAllDemandsSessions.setOnClickListener {
            try {
                viewModel.processAllOnDemandSessions()
                alertDialogBox(getString(R.string.on_demand_session), getString(R.string.on_demand) )
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.on_demand_session_failed), getString(R.string.process_error) )
            }
        }

        stopAllMonitoring.setOnClickListener {
            try {
                viewModel.stopAllMonitoring()
                alertDialogBox(getString(R.string.monitoring_stopped), getString(R.string.monitoring_stopped_success))
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.monitoring_stop_failed), getString(R.string.monitoring_stop_error))
            }
        }

        resumeAllMonitoring.setOnClickListener {
            try {
                viewModel.resumeAllMonitoring()
                alertDialogBox(getString(R.string.monitoring_resumed), getString(R.string.monitoring_resume_success) )
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.monitoring_resume_failed), getString(R.string.monitoring_resume_error) )
            }
        }

        startTimerWithPropertiesButton.setOnClickListener {
            try {
                val userProperties = HashMap<String, String>()
                userProperties["key1"] = "value1"
                userProperties["key2"] = "value2"
                viewModel.startTimer("session", userProperties)
                alertDialogBox(getString(R.string.start_timer_success), getString(R.string.timer_started))
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.start_timer_error),getString(R.string.timer_not_started))
            }
        }

        endTimerWithPropertiesButton.setOnClickListener {
            try {
                val userProperties = HashMap<String, String>()
                userProperties["key3"] = "value3"
                userProperties["key4"] = "value4"
                viewModel.endTimer("session", userProperties)
                alertDialogBox(getString(R.string.end_timer_success), getString(R.string.timer_ended))
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.end_timer_error),getString(R.string.timer_not_ended))
            }
        }
    }

    private fun alertDialogBox(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
