package no.jergan.puertorico.model

case class World (players: List[Player],
                  bank: Bank) {

  def player(): Player = {
    players.head
  }

  def moves(): List[(String, List[Move])] = {
    List(
      ("M0", List(Move("M00"), Move("M01"))),
      ("M1", List(Move("M10"), Move("M11")))
    )
  }

  def next(move: Move): World = {
    println(move.name)
    this.copy()
  }
}

case class Move(name: String)

object World {
  def initial(players: List[String]): World = {
    val initialValues: Map[Int, (Doubloons, VictoryPoints)] = Map(
      (3, (Doubloons(2), VictoryPoints(75))),
      (4, (Doubloons(3), VictoryPoints(100))),
      (5, (Doubloons(4), VictoryPoints(122)))
    )
    val initialValue = initialValues.getOrElse(players.size, (Doubloons(0), VictoryPoints(0)))
    new World(players.map(name => Player(name, VictoryPoints(0), initialValue._1)),
      Bank(initialValue._2))
  }
}

case class Player(name: String, victoryPoints: VictoryPoints, doubloons: Doubloons)

case class Bank(victoryPoints: VictoryPoints)

case class Doubloons(value: Int)

case class VictoryPoints(value: Int)
