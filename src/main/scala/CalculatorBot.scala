import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import io.scalac.slack.MessageEventBus
import io.scalac.slack.bots.AbstractBot
import io.scalac.slack.common.{Command, OutboundMessage}

class CalculatorBot(override val bus: MessageEventBus) extends AbstractBot {

  override def help(channel: String): OutboundMessage =
    OutboundMessage(
      channel,
      s"$name will help you to solve difficult math problems \\n" +
        "Usage: $calc {operation} {arguments separated by space}"
    )

  val possibleOperations = Map(
    "+" -> ((x: Double, y: Double) => x + y),
    "-" -> ((x: Double, y: Double) => x - y),
    "*" -> ((x: Double, y: Double) => x * y),
    "/" -> ((x: Double, y: Double) => x / y)
  )

  override def act: Receive = {
    case Command("calc", operation :: args, message) if args.length >= 1 =>
      val op = possibleOperations.get(operation)

      val response = op.map(f => {
        val result = args.map(_.toDouble).reduceLeft(f(_, _))
        OutboundMessage(message.channel, s"Results is: $result")
      }).getOrElse(OutboundMessage(message.channel, s"No operation $operation"))

      publish(response)

    case Command("calc", _, message) =>
      publish(OutboundMessage(message.channel, s"No arguments specified!"))

    case Command("http", _, msg) =>
      import context.dispatcher
      implicit val mat = ActorMaterializer(None, None)(context)
      Http(context.system).singleRequest(HttpRequest(uri = "https://api.github.com/users/agaro1121"))
        .foreach { resp =>
      Unmarshal(resp.entity).to[String].foreach{
        s =>
          publish(OutboundMessage(msg.channel, """```"""+s.replace("\"", "\\\"")+"""```"""))}
          /*resp.entity.dataBytes
            .runFold(ByteString.empty)(_ ++ _)
            .map { bs =>
              val s = bs.utf8String
              publish(OutboundMessage(msg.channel, """```"""+s.replace("\"", "\\\"")+"""```"""))
            }*/
        }
  }
}
