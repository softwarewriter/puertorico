package no.jergan.puertorico.model

case class World ()

object World {
  def initial(players: List[String]): World = {
    new World()
  }
}

case class Player(name: String)

case class Doubloons(value: Int)

case class VictoryPoints(value: Int)
