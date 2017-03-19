import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory

/**
  * Created by knoldus on 19/3/17.
  */

class BookMyShowApp extends Actor{

  var status=false

  override def receive = {

    case bookSeat if(bookSeat.equals("Booking Seat 1"))=>{
      if(status==false)
        {
          status=true
          println(s"Thanks for $bookSeat !!")
        }
      else {
        //status=false
        println("Sorry this seat is already booked !!")
      }

    }

    case cancelSeat if(cancelSeat.equals("Canceling Seat 1"))=>{

      if(status==true) {
        status = false
        println(s"$cancelSeat Successful")
      }
      else
        println("Already Canceled !!")
    }
  }
}

object BookMyShow extends App{

  val config = ConfigFactory.parseString(
    """
      |akka.actor.deployment {
      | /poolRouter {
      |   router = balancing-pool
      |   nr-of-instances = 5
      | }
      |}
    """.stripMargin
  )

  val system = ActorSystem("RouterSystem", config)
  val router = system.actorOf(FromConfig.props(Props[BookMyShowApp]), "poolRouter")

  router ! "Booking Seat 1"
  Thread.sleep(1000)
  router ! "Booking Seat 1"
  Thread.sleep(1000)
  router ! "Canceling Seat 1"
  Thread.sleep(1000)
  router ! "Canceling Seat 1"
  Thread.sleep(1000)
  router ! "Booking Seat 1"

}
