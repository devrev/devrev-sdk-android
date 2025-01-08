package ai.devrev.sdk.sample

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.model.Identity
import ai.devrev.sdk.model.UserInfo
import ai.devrev.sdk.showSupport
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val identifyButton: Button get() = findViewById(R.id.identify_button)
    private val openWidgetButton: Button get() = findViewById(R.id.open_widget_button)
    private val userIdentificationInput: EditText get() = findViewById(R.id.user_identification_input)
    private val createConversationButton: Button get() = findViewById(R.id.create_conversation_button)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DevRev.setInAppLinkHandler {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(it)
            startActivity(intent)
        }

        identifyButton.setOnClickListener {
            if (userIdentificationInput.text.isNotBlank()) {
                DevRev.identifyUnverifiedUser(Identity(userId = userIdentificationInput.text.toString()))
            }
        }

        openWidgetButton.setOnClickListener {
            DevRev.showSupport(this)
        }

        createConversationButton.setOnClickListener {
            DevRev.createSupportConversation(context = this)
        }
    }
}
