package nl.eduid.ui

import android.content.Context
import android.graphics.drawable.PictureDrawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.util.DebugLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer


suspend fun svgStringToDrawable(context: Context, svgString: String, imageLoader: ImageLoader): PictureDrawable? {
    return withContext(Dispatchers.IO) {
        val request = ImageRequest.Builder(context)
            .data(svgString.toByteArray())
            .decoderFactory(SvgDecoder.Factory())
            .build()
        val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
        result as? PictureDrawable
    }
}

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