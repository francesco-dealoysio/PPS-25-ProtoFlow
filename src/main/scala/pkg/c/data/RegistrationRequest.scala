package pkg.c.data

import java.time.LocalDateTime

case class RegistrationRequest(
                                id: String,
                                name: String,
                                surname: String,
                                email: String,
                                requestedRole: String,
                                requestedArea: String,
                                requestDate: LocalDateTime,
                                status: RegistrationRequestStatus
                              )