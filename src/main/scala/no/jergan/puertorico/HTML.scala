package no.jergan.puertorico

import no.jergan.puertorico.model.{Input, Player, World}
import scalatags.Text.all.{html, _}

object HTML {

  val inputName = "input"

  def toHtml(world: World): ConcreteHtmlTag[String] = {
    html(
      head(),
      body(
        h1("Inputs"),
        inputsToHtml(world.inputs()),
        h1("Players"),
        playersToHtml(world.players)
    ))
  }

  /*
 <form action="/action_page.php" method="get" id="form1">
<label for="fname">First name:</label>
<input type="text" id="fname" name="fname"><br><br>
<label for="lname">Last name:</label>
<input type="text" id="lname" name="lname">
</form>

<button type="submit" form="form1" value="Submit">Submit</button>
   */

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
}
