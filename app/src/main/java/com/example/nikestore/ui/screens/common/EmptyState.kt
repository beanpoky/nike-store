package com.example.nikestore.ui.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nikestore.R
import com.example.nikestore.data.EmptyState
import com.example.nikestore.ui.theme.NikeTheme

@Composable
fun EmptyState(
    emptyState: EmptyState,
    emptyStateAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
            .background(NikeTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(emptyState.imageResId),
            contentDescription = stringResource(emptyState.messageResId)
        )
        Text(
            text = stringResource(emptyState.messageResId),
            modifier = Modifier.padding(top = 36.dp, bottom = 8.dp),
            color = NikeTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        if (emptyState.mustShowCallToActionButton) {
            Button(
                onClick = emptyStateAction,
                shape = MaterialTheme.shapes.extraSmall,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NikeTheme.colors.button,
                    contentColor = NikeTheme.colors.buttonContent
                )
            ) {
                Text(stringResource(R.string.login_screen_title))
            }
        }
    }
}