package pkg.b.logic

case class Registration(
                    private var id: String = "",
                    private var surname: String = "",
                    private var name: String = "",
                    private var email: String = "",
                    private var phone: String = "",
                    private var role: String = "",
                    private var area: String = "",
                    private var assignment: String = "",
                    private var date: String = "",
                    private var state: String = "",
                    private var motivation: String = "",
                    private var processedBy: String = "",
                    private var processedDate: String = "",
                    private var assignedUsername: String = ""
                  ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setSurname(value: String): Unit = surname = value
  def setName(value: String): Unit = name = value
  def setEmail(value: String): Unit = email = value
  def setPhone(value: String): Unit = phone = value
  def setRole(value: String): Unit = role = value
  def setArea(value: String): Unit = area = value
  def setAssignment(value: String): Unit = assignment = value
  def setDate(value: String): Unit = date = value
  def setState(value: String): Unit = state = value
  def setMotivation(value: String): Unit = motivation = value
  def setProcessedBy(value: String): Unit = processedBy = value
  def setProcessedDate(value: String): Unit = processedDate = value
  def setAssignedUsername(value: String): Unit = assignedUsername = value

  def getId: String = id
  def getSurname: String = surname
  def getName: String = name
  def getEmail: String = email
  def getPhone: String = phone
  def getRole: String = role
  def getArea: String = area
  def getAssignment: String = assignment
  def getDate: String = date
  def getState: String = state
  def getMotivation: String = motivation
  def getProcessedBy: String = processedBy
  def getProcessedDate: String = processedDate
  def getAssignedUsername: String = assignedUsername

  override def xmlFile = "registrations.xml"
