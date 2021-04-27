package com.autoencoder.glasdemoapp.shared.utils.extensions

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.models.DemoActivityItem
import com.autoencoder.glasdemoapp.models.FeatureRequirement
import com.autoencoder.glasdemoapp.models.Service
import org.jetbrains.anko.sdk27.coroutines.onClick

fun Fragment.toast(message: String) {
    context?.let { context ->
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

fun Fragment.toast(@StringRes messageId: Int) {
    context?.let { context ->
        Toast.makeText(context, messageId, Toast.LENGTH_LONG).show()
    }
}

fun Activity?.toast(message: String) {
    this?.let {
        Toast.makeText(it, message, Toast.LENGTH_LONG).show()
    }
}

fun Activity?.toast(@StringRes messageId: Int) {
    this?.let {
        Toast.makeText(it, messageId, Toast.LENGTH_LONG).show()
    }
}

@SuppressWarnings("LongParameterList")
fun Fragment.showDialog(
    title: String = "",
    message: String = "",
    buttonMessage: String = "",
    onButtonClick: () -> Unit = {},
    @DrawableRes icon: Int? = null
): AlertDialog? {
    this.context?.let {
        var dialog: AlertDialog? = null
        val dialogLayout: View = layoutInflater.inflate(R.layout.info_dialog, null)
        val builder = AlertDialog.Builder(it)
        builder.setView(dialogLayout)
        builder.setCancelable(true)
        with(dialogLayout.findViewById<ImageView>(R.id.dialog_icon)) {
            icon?.let { icon ->
                setImageResource(icon)
            } ?: run {
                visibility = View.GONE
            }
        }
        dialogLayout.findViewById<TextView>(R.id.dialog_title).text = title
        dialogLayout.findViewById<TextView>(R.id.dialog_description).text = message
        with(dialogLayout.findViewById<TextView>(R.id.dismiss_button)) {
            if (buttonMessage.isNotEmpty())
                text = buttonMessage
            setOnClickListener {
                onButtonClick()
                dialog?.dismiss()
            }
        }
        dialog = builder.show()
        return dialog
    }
    return null
}

fun Fragment.showCriteriaDialog(
    feature: DemoActivityItem
): AlertDialog? {
    this.context?.let { context ->
        var dialog: AlertDialog? = null
        val dialogLayout: View = layoutInflater.inflate(R.layout.info_dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogLayout)
        builder.setCancelable(true)
        dialogLayout.findViewById<ImageView>(R.id.dialog_icon)
            .setImageResource(feature.service.drawable)
        val title = context.getString(feature.service.title)
        dialogLayout.findViewById<TextView>(R.id.dialog_title).text =
            context.getString(R.string.dialog_title, title)
        dialogLayout.findViewById<TextView>(R.id.dialog_description).text = context.getString(
            R.string.dialog_description,
            title,
            context.getString(feature.service.criteria),
            context.getString(R.string.percentage, feature.percentage)
        )
        with(dialogLayout.findViewById<TextView>(R.id.dismiss_button)) {
            setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialog = builder.show()
        return dialog
    }
    return null
}

fun Fragment.showTurnOnDialog(
    feature: DemoActivityItem,
    requirement: FeatureRequirement,
    onButtonClick: (AlertDialog?) -> Unit
): AlertDialog? {
    this.context?.let { context ->
        var dialog: AlertDialog? = null
        val dialogLayout: View = layoutInflater.inflate(R.layout.info_dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogLayout)
        builder.setCancelable(true)
        dialogLayout.findViewById<ImageView>(R.id.dialog_icon)
            .setImageResource(feature.service.drawable)
        val requirementName = context.getString(requirement.typeName)
        dialogLayout.findViewById<TextView>(R.id.dialog_title).text =
            context.getString(R.string.feature_turn_on, requirementName)
        dialogLayout.findViewById<TextView>(R.id.dialog_description).text =
            context.getString(requirement.description, context.getString(feature.service.title))
        dialogLayout.findViewById<TextView>(R.id.turn_on_button).apply {
            visibility = View.VISIBLE
            text = context.getString(R.string.turn_on_feature, requirementName)
            onClick {
                onButtonClick(dialog)
                dialog?.dismiss()
            }
        }
        with(dialogLayout.findViewById<TextView>(R.id.dismiss_button)) {
            setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialog = builder.show()
        return dialog
    }
    return null
}