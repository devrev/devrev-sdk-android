package ai.devrev.sdk.sample

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.model.UserIdentification
import ai.devrev.sdk.showSupport
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val identifyButton: Button get() = findViewById(R.id.identify_button)
    private val openWidgetButton: Button get() = findViewById(R.id.open_widget_button)
    private val userIdentificationInput: EditText get() = findViewById(R.id.user_identification_input)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        identifyButton.setOnClickListener {
            if (userIdentificationInput.text.isNotBlank()) {
                DevRev.identify(
                    UserIdentification(userId = userIdentificationInput.text.toString())
                )
            }
        }

        openWidgetButton.setOnClickListener {
            DevRev.showSupport(this)
        }
    }
}
