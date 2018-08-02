package org.wisconsinbigdata2018.demo.fraudmodel.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

/**
  * The fraud-model service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the FraudmodelService.
  */
trait FraudmodelService extends Service {

  /**
    * Example: curl http://localhost:9000/api/fraud/analyze
    */
  def analyze(): ServiceCall[LogRecord, Int]

  /**
    * Example: curl http://localhost:9000/api/fraud
    */
  def metadata(): ServiceCall[NotUsed, ModelSummary]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("fraud-model")
      .withCalls(
        pathCall("/api/fraud/analyze", analyze _),
        pathCall("/api/fraud", metadata)
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

case class LogRecord
(
  RegisteredDevice: Int,
  Flow: String,
  Action: String,
  Amount: Double
)

object LogRecord {
  implicit val format: Format[LogRecord] = Json.format
}

case class ModelSummary
(
  Name: String,
  Updated: String,
  Version: String
)

object ModelSummary {
  implicit val format: Format[ModelSummary] = Json.format
}