package com.example.test

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val add_task: ImageVector
  get() {
    if (_add_task != null) {
      return _add_task!!
    }
    _add_task =
      ImageVector.Builder(
          name = "add_task",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(12f, 22f)
            quadTo(9.93f, 22f, 8.1f, 21.21f)
            quadTo(6.28f, 20.43f, 4.93f, 19.08f)
            quadTo(3.58f, 17.73f, 2.79f, 15.9f)
            reflectiveQuadTo(2f, 12f)
            quadTo(2f, 9.92f, 2.79f, 8.1f)
            quadTo(3.58f, 6.27f, 4.93f, 4.93f)
            quadTo(6.28f, 3.57f, 8.1f, 2.79f)
            quadTo(9.93f, 2f, 12f, 2f)
            quadToRelative(1.63f, 0f, 3.08f, 0.47f)
            reflectiveQuadTo(17.75f, 3.8f)
            lineTo(16.3f, 5.27f)
            quadTo(15.35f, 4.67f, 14.28f, 4.34f)
            reflectiveQuadTo(12f, 4f)
            quadTo(8.68f, 4f, 6.34f, 6.34f)
            reflectiveQuadTo(4f, 12f)
            reflectiveQuadToRelative(2.34f, 5.66f)
            reflectiveQuadTo(12f, 20f)
            quadToRelative(0.8f, 0f, 1.55f, -0.15f)
            reflectiveQuadTo(15f, 19.43f)
            lineToRelative(1.5f, 1.52f)
            quadToRelative(-1.02f, 0.5f, -2.15f, 0.78f)
            reflectiveQuadTo(12f, 22f)
            close()
            moveToRelative(7f, -2f)
            verticalLineTo(17f)
            horizontalLineTo(16f)
            verticalLineTo(15f)
            horizontalLineToRelative(3f)
            verticalLineTo(12f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(2f)
            horizontalLineTo(21f)
            verticalLineToRelative(3f)
            horizontalLineTo(19f)
            close()
            moveTo(10.6f, 16.6f)
            lineTo(6.35f, 12.35f)
            lineToRelative(1.4f, -1.4f)
            lineTo(10.6f, 13.8f)
            lineTo(20.6f, 3.77f)
            lineTo(22f, 5.18f)
            lineTo(10.6f, 16.6f)
            close()
          }
        }
        .build()
    return _add_task!!
  }

private var _add_task: ImageVector? = null
