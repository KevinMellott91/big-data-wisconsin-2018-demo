package org.wisconsinbigdata2018.demo.fraudmodel.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import org.wisconsinbigdata2018.demo.fraudmodel.api.{FraudmodelService, LogRecord}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.softwaremill.macwire._

class FraudmodelLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new FraudmodelApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new FraudmodelApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[FraudmodelService])
}

abstract class FraudmodelApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[FraudmodelService](wire[FraudmodelServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = FraudModelSerializerRegistry
}


object FraudModelSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[Double],
    JsonSerializer[Int],
    JsonSerializer[LogRecord]
  )
}