package no.jergan.puertorico.model

import scala.collection.immutable.HashMap

case class World (players: List[Player]) {

  def inputs(): List[List[Input]] = {
    List(
      List(Input(0, "In00"), Input(1, "In01")),
      List(Input(2, "In10"), Input(3, "In11")),
    )
  }

  def next(input: Input): World = {
    println(input.index)
    this.copy()
  }
}

case class Input(index: Int, name: String)

object World {
  def initial(players: List[String]): World = {
    val initialValues: Map[Int, Doubloons] = Map(
      (3, Doubloons(2)),
      (4, Doubloons(3)),
      (5, Doubloons(4))
    )
    val initialValue = initialValues.getOrElse(players.size, Doubloons(0))
    new World(players.map(name => Player(name, VictoryPoints(0), initialValue)))
  }
}

case class Player(name: String, victoryPoints: VictoryPoints, doubloons: Doubloons)

case class Doubloons(value: Int)

case class VictoryPoints(value: Int)
