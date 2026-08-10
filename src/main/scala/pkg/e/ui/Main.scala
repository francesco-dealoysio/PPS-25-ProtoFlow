package pkg.e.ui

import pkg.e.ui.traits.GUI
import pkg.b.logic.Account
import pkg.e.ui.operations.Login

object Main extends GUI:

  val parentMask: GUI = this
  val user = Account("", "Guess")
  override def start(): Unit =
    Login(user, this).start()

