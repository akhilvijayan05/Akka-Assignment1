import java.io.File

import akka.actor.Status.Success
import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.io.Source
import akka.pattern.pipe

/**
  * Created by knoldus on 18/3/17.
  */

class ParentTask extends Actor {

  val childActor = context.actorOf(Props[ChildTask])
  import scala.concurrent.ExecutionContext.Implicits.global
  var wordCount=0

  override def receive = {

    case file:String => {

      val src = Source.fromFile(file)

      src.getLines.toList.map(line=> {

        implicit val timeout = Timeout(1000 seconds)
        val f:Future[Int]=(childActor ask line).mapTo[Int]
        Thread.sleep(100)
        f.foreach(word=>wordCount+=word)

      })

      println(s"Total word count is : $wordCount")

    }
  }
}

class ChildTask extends Actor {

  override def receive = {

    case line:String => {

      val word = line.split(" ").length
      sender() ! word

    }
  }
}

object WordCount extends App {

  val file="/home/knoldus/Akka_Assign1/src/main/scala/File.txt"
  val system = ActorSystem("ForwardPattern")
  val props = Props[ParentTask]
  val ref = system.actorOf(props)

  ref ! file

}
