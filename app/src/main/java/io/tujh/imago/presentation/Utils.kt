package io.tujh.imago.presentation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

fun LazyListState.lastVisibleItemIndex() = snapshotFlow { layoutInfo.visibleItemsInfo }
    .mapNotNull { it.lastOrNull()?.index }
    .distinctUntilChanged()

fun LazyStaggeredGridState.lastVisibleItemIndex() = snapshotFlow { layoutInfo.visibleItemsInfo }
    .mapNotNull { it.lastOrNull()?.index }
    .distinctUntilChanged()