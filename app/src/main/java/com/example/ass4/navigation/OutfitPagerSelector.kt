package com.example.ass4.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ass4.R
import com.example.ass4.database.Cloth
import com.example.ass4.screens.SelectGridItem
import com.google.accompanist.pager.*

// 数据类更清晰地表示单个 outfit 区块
data class OutfitBox(
    val iconRes: Int,
    val label: String,
    val list: List<Cloth>,
    val selected: Cloth?,
    val onSelect: () -> Unit,
    val onClear: () -> Unit
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OutfitPagerSelector(
    cap: Pair<List<Cloth>, Cloth?>,
    top: Pair<List<Cloth>, Cloth?>,
    bottom: Pair<List<Cloth>, Cloth?>,
    shoes: Pair<List<Cloth>, Cloth?>,
    onCapSelect: () -> Unit, onCapClear: () -> Unit,
    onTopSelect: () -> Unit, onTopClear: () -> Unit,
    onBottomSelect: () -> Unit, onBottomClear: () -> Unit,
    onShoesSelect: () -> Unit, onShoesClear: () -> Unit
) {
    val pages = listOf(
        listOf(
            OutfitBox(R.drawable.baseball_cap, "Cap", cap.first, cap.second, onCapSelect, onCapClear),
            OutfitBox(R.drawable.shirt, "Top", top.first, top.second, onTopSelect, onTopClear)
        ),
        listOf(
            OutfitBox(R.drawable.pants, "Bottom", bottom.first, bottom.second, onBottomSelect, onBottomClear),
            OutfitBox(R.drawable.shoes, "Shoes", shoes.first, shoes.second, onShoesSelect, onShoesClear)
        )
    )
    val pagerState = rememberPagerState()

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) { page ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                pages[page].forEach { box ->
                    SelectGridItem(
                        iconRes = box.iconRes,
                        label = box.label,
                        selectedItem = box.selected,
                        onSelect = box.onSelect,
                        onClear = box.onClear
                    )
                }
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
            activeColor = Color(0xFF2E7D32)
        )
    }
}