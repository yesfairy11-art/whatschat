package com.example

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.navigation.WhatsChatNavGraph
import com.example.ui.WhatsChatViewModel
import com.example.ui.theme.WhatsChatTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
    )
    setContent {
      WhatsChatTheme {
        val viewModel: WhatsChatViewModel = viewModel()
        val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(toastMessage) {
          val msg = toastMessage
          if (!msg.isNullOrBlank()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
          }
        }

        WhatsChatNavGraph(viewModel = viewModel)
      }
    }
  }
}
