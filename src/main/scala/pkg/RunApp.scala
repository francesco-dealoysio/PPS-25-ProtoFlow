package pkg

import pkg.b.logic.*
import pkg.a.gui.Login
import pkg.c.data.FileSystem.{createDirectoryStructure, getCurrentDirectory}
import pkg.c.data.Properties.{createPropsFile, setPropsFileProperty}

// Entry point to run the application
object RunApp {
  def main(args: Array[String]): Unit = {
    init
    Login.main(args)
  }
}
