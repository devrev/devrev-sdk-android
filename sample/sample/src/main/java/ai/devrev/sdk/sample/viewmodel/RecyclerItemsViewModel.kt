package ai.devrev.sdk.sample.viewmodel

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.markSensitiveViews
import ai.devrev.sdk.unmarkSensitiveViews
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.view.View

class RecyclerItemsViewModel : ViewModel() {
    private val _items = MutableLiveData(List(100) { "Item #$it" })
    val items: LiveData<List<String>> = _items

    fun markSensitive(cardView: View) {
        DevRev.markSensitiveViews(listOf(cardView))
    }
    fun unmarkSensitive(cardView: View) {
        DevRev.unmarkSensitiveViews(listOf(cardView))
    }
}
