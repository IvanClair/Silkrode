package personal.ivan.silkrode.extension

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import personal.ivan.silkrode.R

/**
 * Enable or disable loading [ProgressBar]
 */
infix fun ProgressBar.switchLoadingProcess(enable: Boolean) {
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