package pkg.a.gui

import scalafx.beans.property.StringProperty

class DocumentRow(
                   protocollo: String,
                   oggetto: String,
                   mittente: String,
                   categoria: String,
                   stato: String,
                   data: String
                 ):
  val protocolloProperty = StringProperty(protocollo)
  val oggettoProperty = StringProperty(oggetto)
  val mittenteProperty = StringProperty(mittente)
  val categoriaProperty = StringProperty(categoria)
  val statoProperty = StringProperty(stato)
  val dataProperty = StringProperty(data)