package no.jergan.puertorico

/**
 * Configuration for application.
 *
 * @author <a href="mailto:oyvind@jergan.no">Oyvind Jergan</a>
 */
case class Configuration(val port: Int, bindAddress: String, players: List[String])
