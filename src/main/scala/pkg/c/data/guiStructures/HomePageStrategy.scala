package pkg.c.data.guiStructures

trait HomePageStrategy:
  def role: Role
  def pageTitle: String
  def roleDescription: String
  def menuItems: Seq[MenuItem]

  def createConfig(applicationTitle: String): HomePageConfig =
    HomePageConfig(
      applicationTitle = applicationTitle,
      pageTitle = pageTitle,
      role = role,
      roleDescription = roleDescription,
      menuItems = menuItems
    )