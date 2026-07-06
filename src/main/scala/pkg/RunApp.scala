package pkg

import pkg.b.logic.*
import pkg.a.gui.Login

// Entry point to run the application
object RunApp {
  def main(args: Array[String]): Unit = {
    init
    Login.main(args)
  }
}
