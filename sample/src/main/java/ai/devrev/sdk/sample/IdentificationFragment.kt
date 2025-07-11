package ai.devrev.sdk.sample

import ai.devrev.sdk.model.Identity
import ai.devrev.sdk.model.UserInfo
import ai.devrev.sdk.sample.viewmodel.IdentificationViewModel
import ai.devrev.sdk.sample.viewmodel.SharedViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels

class IdentificationFragment : Fragment() {

    private val viewModel: IdentificationViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_identify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val identifyUnverifiedUserButton: Button = view.findViewById(R.id.unverified_user_button)
        val identifyVerifiedUserButton: Button = view.findViewById(R.id.verified_user_button)
        val isUserIdentifiedCheckbox: CheckBox = view.findViewById(R.id.is_user_identified)
        val logOutButton: Button = view.findViewById(R.id.logout_button)
        val sessionTokenText: EditText = view.findViewById(R.id.session_token_text)
        val unverifiedUserIdText: EditText = view.findViewById(R.id.user_id_text)
        val updateUserButton: Button = view.findViewById(R.id.update_user_button)
        val updateUserEmail: EditText = view.findViewById(R.id.update_user_email)
        val verifiedUserIdText: EditText = view.findViewById(R.id.verified_user_id_text)

        var userId : String = ""

        sharedViewModel.isUserIdentified.observe(viewLifecycleOwner) { isUserIdentified ->
            isUserIdentifiedCheckbox.isChecked = isUserIdentified
        }

        identifyUnverifiedUserButton.setOnClickListener {
            if(unverifiedUserIdText.text.isNotBlank()) {
                try {
                    userId = unverifiedUserIdText.text.toString()
                    viewModel.identifyUnverifiedUser(userId = userId)
                    alertDialogBox(getString(R.string.successful), getString(R.string.identification_successful))
                } catch (e: Exception) {
                    alertDialogBox(getString(R.string.unsuccessful), getString(R.string.identification_unsuccessful))
                }
            }
        }

        identifyVerifiedUserButton.setOnClickListener {
            try {
                if (verifiedUserIdText.text.isNotBlank() and sessionTokenText.text.isNotBlank()) {
                    userId = verifiedUserIdText.text.toString()
                    viewModel.identifyVerifiedUser(userId, sessionTokenText.text.toString())
                    alertDialogBox(getString(R.string.successful), getString(R.string.identification_successful))
                }
            }catch (e: Exception) {
                alertDialogBox(getString(R.string.unsuccessful), getString(R.string.identification_unsuccessful))
            }
        }

        logOutButton.setOnClickListener {
            viewModel.logout(requireContext())
        }

        updateUserButton.setOnClickListener {
            try {
                var userEmail: String? = null
                if (updateUserEmail.text.isNotBlank()) {
                    userEmail = updateUserEmail.text.toString()
                }
                if (userId.isNotEmpty()) {
                    viewModel.updateUser(Identity(userId = userId, userInfo = UserInfo(userEmail)))
                }
                alertDialogBox(getString(R.string.successful), getString(R.string.update_user_successful))
            } catch (e: Exception) {
                alertDialogBox(getString(R.string.unsuccessful), getString(R.string.update_user_unsuccessful))
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