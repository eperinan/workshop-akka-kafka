package com.workshop.routes

import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete

trait SimpleRoutes {
  lazy val simpleRoutes =
    path("welcome-workshop-akka-kafka") {
      get {
        complete("Welcome to workshop-akka-kafka")
      }
    }
}
