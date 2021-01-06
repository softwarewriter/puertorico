package no.jergan.puertorico.model

import scala.collection.mutable.ListBuffer

class Frames(frames: ListBuffer[Frame]) {

  def this(initial: World) = {
    this(new ListBuffer().addOne(Frame(None, None, initial)))
  }

  def add(frame: Frame): Unit = {
    frames.addOne(frame)
  }

  def current: Frame = {
    frames.last
  }

  def toList: List[Frame] = {
    this.frames.toList
  }

}

case class Frame(move: Option[Move], player: Option[Player], world: World)

case class World (players: List[Player],
                  playerIndex: Int,
                  bank: Bank
                 ) {

  def player(): Player = {
    players(playerIndex)
  }

  def moves(): List[(String, List[Move])] = {
    List(
      ("M0", List(Move("M00"), Move("M01"))),
      ("M1", List(Move("M10"), Move("M11")))
    )
  }

  def next(move: Move): World = {
    this.copy(playerIndex = (playerIndex + 1) % players.size)
  }
}

object World {
  def initial(players: List[String]): World = {
    val cards = List(
      Card.empty(Role.Builder),
      Card.empty(Role.Captain),
      Card.empty(Role.Craftsman),
      Card.empty(Role.Major),
      Card.empty(Role.Settler),
      Card.empty(Role.Trader)
    )
    val initialValues: Map[Int, (Doubloons, VictoryPoints, List[Card])] = Map(
      (3, (Doubloons(2), VictoryPoints(75), cards)),
      (4, (Doubloons(3), VictoryPoints(100), cards ++ List(Card.empty(Role.Prospector1)))),
      (5, (Doubloons(4), VictoryPoints(122), cards ++ List(Card.empty(Role.Prospector1), Card.empty(Role.Prospector2))))
    )
    val initialValue = initialValues.getOrElse(players.size, (Doubloons(0), VictoryPoints(0), List[Card]()))
    new World(
      players.map(name => Player(name, VictoryPoints(0), initialValue._1)),
      0,
      Bank(initialValue._2, initialValue._3)
    )
  }
}

case class Move(name: String)

case class Player(name: String, victoryPoints: VictoryPoints, doubloons: Doubloons)

case class Bank(victoryPoints: VictoryPoints, cards: List[Card])

case class Doubloons(value: Int)

case class VictoryPoints(value: Int)

case class Card(role: Role, doubloons: Doubloons, player: Option[Player])

object Card {
  def empty(role: Role): Card = Card(role, Doubloons(0), None)
}

sealed trait Role

object Role {
  case object Builder extends Role
  case object Captain extends Role
  case object Craftsman extends Role
  case object Major extends Role
  case object Prospector1 extends Role
  case object Prospector2 extends Role
  case object Settler extends Role
  case object Trader extends Role
}
