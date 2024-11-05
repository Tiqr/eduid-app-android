package nl.eduid.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.util.DebugLogger
import kotlinx.coroutines.Dispatchers
import java.nio.ByteBuffer

@Composable
fun SvgImage(svgString: String, modifier: Modifier = Modifier) {
    val model: ImageRequest = ImageRequest.Builder(context = LocalContext.current)
        .data(ByteBuffer.wrap(svgString.toByteArray()))
        .decoderFactory(SvgDecoder.Factory())
        .decoderDispatcher(Dispatchers.IO)
        .build()
    val imageLoader = LocalContext.current.imageLoader.newBuilder().logger(DebugLogger()).build()
    AsyncImage(
        model = model, imageLoader = imageLoader, contentDescription = null, modifier = modifier
    )
}