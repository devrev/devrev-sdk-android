package ai.devrev.sdk.sample

import ai.devrev.sdk.sample.viewmodel.SessionAnalyticsViewModel
import ai.devrev.sdk.sample.viewmodel.SharedViewModel
import ai.devrev.sdk.sample.viewmodel.SupportChatViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.viewModels

class SupportChatFragment : Fragment() {

    private val viewModel: SupportChatViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_support_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportChatButton: Button = view.findViewById(R.id.support_chat_button)
        val supportViewButton: Button = view.findViewById(R.id.support_view_button)
        val isUserIdentifiedCheckbox: CheckBox = view.findViewById(R.id.is_user_identified)

        sharedViewModel.isUserIdentified.observe(viewLifecycleOwner) { isUserIdentified ->
            isUserIdentifiedCheckbox.isChecked = isUserIdentified
        }

        supportChatButton.setOnClickListener {
            viewModel.createSupportConversation(requireContext())
        }

        supportViewButton.setOnClickListener {
            viewModel.showSupport(requireContext())
        }
    }
}