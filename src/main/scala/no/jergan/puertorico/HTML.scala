package no.jergan.puertorico

import no.jergan.puertorico.model.{Bank, Frame, Move, Player, World}
import scalatags.Text.all.{h1, html, _}

object HTML {

  case class Input(index: Int, move: Move)

  val inputName = "input"

  def move(world: World, indexAsString: String): Option[Move] = {
    inputs(world.moves()).flatMap(a => a._2)
      .find(_.index == Integer.parseInt(indexAsString))
      .map(input => input.move)
  }

  def toHtml(frames: List[Frame]): ConcreteHtmlTag[String] = {
    val world = frames.head.world
    html(
      head(),
      body(
        h1(s"Inputs for ${world.player.name}"),
        inputsToHtml(world),
        h1("Players"),
        playersToHtml(world.players),
        h1("Bank"),
        bankToHtml(world.bank),
        h1("History"),
        framesToHtml(frames)
    ))
  }

  def inputsToHtml(world: World): ConcreteHtmlTag[String] = {
    form(`id`:="inputform")(`method`:="post")(
      table(
        inputs(world.moves())
          .map(row => tr(td(row._1), row._2
            .map(input => td(button(`name`:=inputName)(`type`:="submit")(`value`:=input.index)(input.move.name)))))
      )
    )
  }

  def playersToHtml(players: List[Player]): ConcreteHtmlTag[String] = {
    table(
      tr(th("Name"), th("Victory points"), th("Doubloons")),
      players.map(p => tr(td(p.name), td(p.victoryPoints.value), td(p.doubloons.value)))
    )
  }

  def bankToHtml(bank: Bank): ConcreteHtmlTag[String] = {
    table(
      tr(th("Victory points")),
      tr(td(bank.victoryPoints.value))
    )
  }

  def framesToHtml(frames: List[Frame]): ConcreteHtmlTag[String] = {
    table(
      tr(th("Iteration"), th("Player"), th("Move")),
      frames.zipWithIndex.reverse.map(e => {
        val player = e._1.player.map(p => p.name).getOrElse("")
        val move = e._1.move.map(m => m.name).getOrElse("")
        tr(td(e._2), td(player), td(move))
      })
    )
  }

  private def inputs(moves: List[(String, List[Move])]): List[(String, List[Input])] = {
    val indexes = LazyList.iterate(0)(_ + 1).iterator
    moves.map(r => (r._1, r._2.map(e => Input(indexes.next(), e))))
  }

}
