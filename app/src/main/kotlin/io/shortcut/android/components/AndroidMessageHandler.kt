package io.shortcut.android.components

import android.widget.Toast
import io.shortcut.android.KmmApplication
import io.shortcut.redux.middlewares.MessageHandler

class AndroidMessageHandler : MessageHandler {

    override fun handle(message: String?) {
        if (message != null) Toast.makeText(KmmApplication.app, message, Toast.LENGTH_LONG).show()
    }
}
