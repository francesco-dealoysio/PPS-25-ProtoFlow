package pkg.a.gui.structures

import pkg.c.data.generalStructures.RegistrationRequestStatus
import java.time.LocalDateTime

case class RegistrationRequest(
                                id: String,
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
