package com.nikhil.gaugeright.presentation.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nikhil.gaugeright.R

@Composable
fun AnimatedLine(
    boxSize: Dp = 100.dp,
    animationDuration: Int = 2000) 
{
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val heightAnimation by infiniteTransition.animateValue(
        initialValue = 0.dp,
        targetValue = boxSize,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = animationDuration
                0.dp at 0 // ms
                boxSize at animationDuration / 2
                0.dp at animationDuration with FastOutSlowInEasing
            }
            // Use the default RepeatMode.Restart to start from 0.dp after each iteration
        ), label = ""
    )
    Box(
        modifier = Modifier
            .size(boxSize),
    ) {
//        Image(
//            painter = painterResource(id = R.drawable.ic_launcher_background),
//            contentDescription = "start scanning",
//            modifier = Modifier.size(boxSize),
//        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(heightAnimation))
            Divider(
                thickness = 4.dp, color = Color.Cyan,
                modifier = Modifier.width(boxSize - 6.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Prev() {
    AnimatedLine()
}