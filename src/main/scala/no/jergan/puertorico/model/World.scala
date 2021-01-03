package no.jergan.puertorico.model

import scala.collection.immutable.HashMap

case class World (players: List[Player],
                  bank: Bank) {

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
