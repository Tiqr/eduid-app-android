package nl.eduid.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.eduid.R
import nl.eduid.ui.theme.EduidAppAndroidTheme
import nl.eduid.ui.theme.MainSurfGreen

@Composable
fun HomeGreenButton(
    text: String, onClick: () -> Unit, @DrawableRes icon: Int, modifier: Modifier = Modifier
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable {
        onClick.invoke()
    }
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MainSurfGreen)
            .height(50.dp)
            .width(50.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
        )
    }

    Spacer(Modifier.height(12.dp))

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Normal),
    )
}



@Preview
@Composable
private fun Preview_HomeGreenButton() {
    EduidAppAndroidTheme {
        HomeGreenButton(text = "Personal Info", onClick = {}, icon = R.drawable.homepage_security_icon, modifier = Modifier)
    }
}