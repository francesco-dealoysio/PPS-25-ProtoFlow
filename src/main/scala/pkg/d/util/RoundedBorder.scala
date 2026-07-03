package pkg.d.util

import java.awt.{Graphics, RenderingHints}
import javax.swing.border.AbstractBorder
import scala.swing.{Color, Graphics2D}

// Custom rounded border class
class RoundedBorder(radius: Int, borderColor: Color) extends AbstractBorder {
  override def paintBorder(c: java.awt.Component, g: Graphics, x: Int, y: Int, width: Int, height: Int): Unit = {
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setColor(borderColor)
    g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
  }
}
