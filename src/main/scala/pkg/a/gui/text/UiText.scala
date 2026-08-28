package pkg.a.gui.text

// Punto d'accesso unico ai testi dell'interfaccia: il contenuto vero e proprio è diviso per
// dominio (CommonText, IdentityText, DocumentText, AppText) per tenere ogni file leggibile;
// gli export mantengono invariati tutti i riferimenti esistenti a "UiText.<Oggetto>".
object UiText:
  export CommonText.*
  export IdentityText.*
  export DocumentText.*
  export AppText.*
