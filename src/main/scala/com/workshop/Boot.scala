package com.workshop

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import com.workshop.routes.{ BaseRoutes, SimpleRoutes }
import scala.concurrent.Await
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

object Boot extends Directives with SimpleRoutes {
  def main(args: Array[String]) {

    val config = ConfigFactory.load()

    implicit val system = ActorSystem("workshop-akka-kafka")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val routes = BaseRoutes.baseRoutes ~ simpleRoutes
    val bindingFuture = Http().bindAndHandle(routes, config.getString("workshop-akka-kafka.hostname"), config.getInt("workshop-akka-kafka.port"))

    Await.ready(system.whenTerminated, Duration.Inf)

  }

}
