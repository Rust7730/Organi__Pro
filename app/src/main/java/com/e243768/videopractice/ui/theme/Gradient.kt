import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random
private data class Star(
    val center: Offset,
    val radius: Float,
    val alpha: Float
)


@Composable
fun GradientWithStarsBackground(
    modifier: Modifier = Modifier,
    starCount: Int = 200
) {
    val topColor = Color(0xFF4E4A6D)
    val bottomColor = Color(0xFF210535)
    val starColor = Color(0xFFF0F0D8)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(topColor,bottomColor, bottomColor)
                )
            )
    ) {
        val stars = remember(starCount) {
            List(starCount) {
                Star(

                    center = Offset(
                        x = Random.nextFloat(),
                        y = Random.nextFloat()
                    ),
                    radius = Random.nextFloat() * 2f + 0.5f,
                    alpha = Random.nextFloat() * 0.7f + 0.3f
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {

            stars.forEach { star ->
                drawCircle(
                    color = starColor.copy(alpha = star.alpha),

                    center = Offset(
                        x = star.center.x * size.width,
                        y = star.center.y * size.height
                    ),
                    radius = star.radius
                )
            }
        }
    }
}
@Composable
fun gradientLigthly (){
    val topColor = Color(0xFF7A5EFF)
    val bottomColor = Color(0xFF210535)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(topColor, bottomColor)
                )
            )
    ){

    }
}