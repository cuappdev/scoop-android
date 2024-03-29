package com.cornellappdev.scoop.ui.components.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.google.accompanist.pager.ExperimentalPagerApi
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.scoop.R

@Composable
fun FooterImage(
    currIndex: Int,
    carIndex: Int,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        if (currIndex == carIndex) {
            Image(
                painterResource(R.drawable.ic_footer_car),
                contentDescription = "",
                contentScale = ContentScale.Crop,
            )
        } else {

            Image(
                painterResource(R.drawable.footer_green_dot),
                contentDescription = "",
                contentScale = ContentScale.Crop,
            )
        }
        Image(
            painterResource(R.drawable.loading_black_dot),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
        Image(
            painterResource(R.drawable.loading_black_dot),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
    }
}

/**
 * Footer based on page index.
 *
 * Requires: [maxIndex] >= 2
 */
@OptIn(ExperimentalPagerApi::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun MovingCarFooter(
    carIndex: Int, maxIndex: Int
) {

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(maxIndex) { index ->
            if (index <= maxIndex - 2) {
                FooterImage(currIndex = index, carIndex = carIndex)
            } else {
                Image(
                    painterResource(R.drawable.green_footer_location),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
