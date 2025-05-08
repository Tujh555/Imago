package io.tujh.imago.presentation.screens.post.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import io.tujh.imago.domain.paging.paginator.LoadState
import io.tujh.imago.presentation.components.ShortPost
import io.tujh.imago.presentation.components.postShape

private val shimmerHeights = (200..400 step 20).map(Int::dp).shuffled()

fun randomShimmerHeight() = shimmerHeights.random()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreenContent(state: PostListScreen.State, onAction: (PostListScreen.Action) -> Unit) {
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        isRefreshing = state.isRefreshing,
        onRefresh = { onAction(PostListScreen.Action.Refresh) },
    ) {
        if (state.isEmpty) {
            Text(
                text = "There will be something interesting here in the future.",
                color = Color.White,
                fontSize = 24.sp
            )
        } else {
            val shimmer = rememberShimmer(ShimmerBounds.View)
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                state = state.gridState,
                columns = StaggeredGridCells.Adaptive(180.dp),
                reverseLayout = false,
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.posts, key = { it.id }, contentType = { "post" }) { post ->
                    ShortPost(
                        modifier = Modifier.fillMaxWidth(),
                        post = post,
                        shimmer = shimmer
                    )
                }

                if (state.loadState.isLoading()) {
                    items(30, contentType = { "shimmer" }) {
                        PostShimmer(Modifier, shimmer, randomShimmerHeight())
                    }
                }
            }
        }
    }
}

@Composable
fun PostShimmer(
    modifier: Modifier = Modifier,
    shimmer: Shimmer = rememberShimmer(ShimmerBounds.View),
    height: Dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(postShape)
            .shimmer(shimmer)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}

fun LoadState.isLoading() = this == LoadState.Loading || this == LoadState.Initial

@Composable
@Preview
private fun ShimmerPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .systemBarsPadding()
                .fillMaxWidth()
                .shimmer()
        ) {
            Box(modifier = Modifier
                .size(200.dp)
                .background(MaterialTheme.colorScheme.outline))
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}