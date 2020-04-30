@file:JvmName("UiExtension")

package personal.ivan.silkrode.extension

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import personal.ivan.silkrode.R

/**
 * Enable or disable a view
 */
infix fun View.enableOrDisable(enable: Boolean) {
    visibility = if (enable) View.VISIBLE else View.GONE
}

/**
 * API error alert
 */
fun Context.showApiErrorAlert() {
    MaterialAlertDialogBuilder(this)
        .setTitle(R.string.alert_title)
        .setMessage(R.string.alert_message)
        .setPositiveButton(R.string.label_ok, null)
        .show()
}

/**
 * Change tint color for audio controls
 */
infix fun ImageView.setTintForPlayerStatus(enabled: Boolean) {
    setColorFilter(
        ContextCompat.getColor(
            context,
            if (enabled) R.color.colorPrimaryDark else android.R.color.darker_gray
        )
    )
}