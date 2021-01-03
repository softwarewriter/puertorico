package no.jergan.puertorico

import no.jergan.puertorico.model.{Bank, Input, Player, World}
import scalatags.Text.all.{h1, html, _}

object HTML {

  val inputName = "input"

  def toHtml(worlds: List[World]): ConcreteHtmlTag[String] = {
    val world = worlds.head
    html(
      head(),
      body(
        h1("Inputs"),
        inputsToHtml(world.inputs()),
        h1("Players"),
        playersToHtml(world.players),
        h1("Bank"),
        bankToHtml(world.bank),
        h1("History"),
        worldsToHtml(worlds)
    ))
  }

  def inputsToHtml(inputs: List[List[Input]]): ConcreteHtmlTag[String] = {
    form(`id`:="inputform")(`method`:="post")(
      table(
        inputs.map(row => tr(row.map(input => td(button(`name`:=inputName)(`type`:="submit")(`value`:=input.index)(input.name)))))
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

  def worldsToHtml(worlds: List[World]): ConcreteHtmlTag[String] = {
    table(
      tr(th("Iteration"), th("Players")),
      worlds.zipWithIndex.reverse.map(e => tr(td(e._2), td(e._1.players.size)))
    )
  }
}
