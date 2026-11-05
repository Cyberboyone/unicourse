package com.cyberboyone.lecturenotes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class AdSlottedRow<T> {
    data class Item<T>(val value: T) : AdSlottedRow<T>()
    class Ad<T> : AdSlottedRow<T>()
}

fun <T> withNativeAdsEvery4(items: List<T>): List<AdSlottedRow<T>> =
    buildList {
        items.forEachIndexed { index, item ->
            add(AdSlottedRow.Item(item))
            if ((index + 1) % 4 == 0) add(AdSlottedRow.Ad())
        }
    }

@Composable
fun <T> AdSlottedGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    cardContent: @Composable (T) -> Unit,
    adContent: @Composable () -> Unit
) {
    val rows = withNativeAdsEvery4(items)
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = rows,
            span = { row ->
                when (row) {
                    is AdSlottedRow.Item -> GridItemSpan(1)
                    is AdSlottedRow.Ad -> GridItemSpan(maxLineSpan)
                }
            }
        ) { row ->
            when (row) {
                is AdSlottedRow.Item -> cardContent(row.value)
                is AdSlottedRow.Ad -> adContent()
            }
        }
    }
}
