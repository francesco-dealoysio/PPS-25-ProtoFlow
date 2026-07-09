package pkg.c.data.guiStructures

import pkg.c.data.generalStructures.RegistrationRequestStatus

import java.time.LocalDateTime
import java.util.UUID

case class RegistrationRequest(
                                id: String = UUID.randomUUID().toString,
                                name: String = "",
                                surname: String = "",
                                email: String = "",
                                phone: String = "",
                                requestedRole: String = "",
                                requestedArea: String = "",
                                assignment: String = "",
                                requestDate: LocalDateTime = LocalDateTime.now(),
                                status: RegistrationRequestStatus = RegistrationRequestStatus.Pending
                              )
