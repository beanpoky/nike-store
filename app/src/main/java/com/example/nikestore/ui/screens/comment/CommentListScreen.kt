package com.example.nikestore.ui.screens.comment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nikestore.R
import com.example.nikestore.ui.screens.NavigationDestination
import com.example.nikestore.ui.screens.common.ErrorScreen
import com.example.nikestore.ui.screens.common.LoadingScreen
import com.example.nikestore.ui.screens.common.NetworkDataUiState
import com.example.nikestore.ui.screens.common.NikeTopAppBar
import com.example.nikestore.ui.screens.product.detail.CommentItem
import com.example.nikestore.ui.theme.NikeTheme
import org.koin.androidx.compose.koinViewModel

object CommentListDestination : NavigationDestination {
    override val route = "COMMENT_LIST"
    override val titleRes = R.string.user_comments
    const val PRODUCT_ID_ARG = "product_id"
    val routeWithArg = "$route/{$PRODUCT_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentListScreen(
    onBack: () -> Unit,
    commentListViewModel: CommentListViewModel = koinViewModel()
) {
    val commentsUiState = commentListViewModel.networkDataUiState

    Scaffold(
        topBar = {
            NikeTopAppBar(
                title = stringResource(CommentListDestination.titleRes),
                onBack = onBack
            )
        },
        containerColor = NikeTheme.colors.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (commentsUiState) {
                is NetworkDataUiState.Loading -> LoadingScreen()
                is NetworkDataUiState.Error -> ErrorScreen(retryAction = commentListViewModel::getComments)
                is NetworkDataUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 16.dp)
                    ) {
                        items(commentsUiState.data) { comment ->
                            CommentItem(comment)
                        }
                    }
                }
            }
        }
    }
}