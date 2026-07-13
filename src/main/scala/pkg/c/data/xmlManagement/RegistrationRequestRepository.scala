package pkg.c.data.xmlManagement

import pkg.c.data.generalStructures.RegistrationRequestStatus
import pkg.c.data.guiStructures.RegistrationRequest

import java.io.File
import java.time.LocalDateTime
import scala.xml.{Elem, Node, XML}

class RegistrationRequestRepository(
                                     private val filePath: String = "registration_requests.xml"
                                   ):

  // si
  private def emptyXml: Elem =
    <registrationRequests></registrationRequests>

  // si 
  private def loadXml(): Elem =
    val file = File(filePath)

    if file.exists() then
      XML.loadFile(file)
    else
      saveXml(emptyXml)
      emptyXml

  // no -> saveXML
  private def saveXml(xml: Elem): Unit =
    XML.save(filePath, xml, "UTF-8", xmlDecl = true)

  // no -> recordToElem
  private def toXml(request: RegistrationRequest): Node =
    <request>
      <id>{request.id}</id>
      <name>{request.name}</name>
      <surname>{request.surname}</surname>
      <email>{request.email}</email>
      <phone>{request.phone}</phone>
      <requestedRole>{request.requestedRole}</requestedRole>
      <requestedArea>{request.requestedArea}</requestedArea>
      <assignment>{request.assignment}</assignment>
      <requestDate>{request.requestDate.toString}</requestDate>
      <status>{request.status.toString}</status>
    </request>

  // si temp
  private def fromXml(node: Node): RegistrationRequest =
    RegistrationRequest(
      id = (node \ "id").text,
      name = (node \ "name").text,
      surname = (node \ "surname").text,
      email = (node \ "email").text,
      phone = (node \ "phone").text,
      requestedRole = (node \ "requestedRole").text,
      requestedArea = (node \ "requestedArea").text,
      assignment = (node \ "assignment").text,
      requestDate = LocalDateTime.parse((node \ "requestDate").text),
      status = RegistrationRequestStatus.valueOf((node \ "status").text)
    )

  // si temp
  private def saveAll(requests: List[RegistrationRequest]): Unit =
    val xml =
      <registrationRequests>
        {requests.map(toXml)}
      </registrationRequests>

    saveXml(xml)

  // no -> insertElemIntoXML
  def save(request: RegistrationRequest): RegistrationRequest =
    val requests = findAll()
    saveAll(requests :+ request)
    request

  // si 
  def findAll(): List[RegistrationRequest] =
    val xml = loadXml()
    (xml \ "request").toList.map(fromXml)

  // si
  def findById(id: String): Option[RegistrationRequest] =
    findAll().find(_.id == id)
    
  // no -> xml, ma logica
  def findPending(): List[RegistrationRequest] =
    findAll().filter(_.status == RegistrationRequestStatus.Pending)

  def update(
              updatedRequest: RegistrationRequest
            ): Either[String, RegistrationRequest] =
    val requests = findAll()

    if !requests.exists(_.id == updatedRequest.id) then
      Left("Richiesta di registrazione non trovata")
    else
      val updatedRequests =
        requests.map: request =>
          if request.id == updatedRequest.id then updatedRequest
          else request

      saveAll(updatedRequests)
      Right(updatedRequest)