package org.wisconsinbigdata2018.demo.fraudmodel.impl

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.wisconsinbigdata2018.demo.fraudmodel.api.{FraudmodelService, LogRecord, ModelSummary}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import ml.combust.bundle.BundleFile
import ml.combust.mleap.runtime.MleapSupport._
import resource._
import ml.combust.mleap.runtime.frame.{DefaultLeapFrame, Row}
import ml.combust.mleap.core.types._

import scala.concurrent.Future

/**
  * Implementation of the FraudmodelService.
  */
class FraudmodelServiceImpl extends FraudmodelService {
  val modelPath: String = "jar:file:/Users/e5543574/Downloads/fraud-model-json.zip"

  override def analyze() = ServiceCall { (logRecord: LogRecord) =>
    println("*** PROCESSING REQUEST TO 'analyze' API ***")
    println(logRecord)

    // Load the model from disk.
    val zipBundleM = (for(bundle <- managed(BundleFile(modelPath))) yield {
      bundle.loadMleapBundle().get
    }).opt.get

    // Define the schema of the expected input.
    val mleapSchema = ml.combust.mleap.core.types.StructType(
      StructField("RegisteredDevice", ScalarType.Int),
      StructField("Flow", ScalarType.String),
      StructField("Action", ScalarType.String),
      StructField("Amount", ScalarType.Double)
    ).get

    // Create a leap frame to hold the input record.
    val data = Seq(
      Row(logRecord.RegisteredDevice, logRecord.Flow, logRecord.Action, logRecord.Amount)
    )
    val mleapFrame = DefaultLeapFrame(mleapSchema, data)

    // Generate predictions for this record, and print the full results to the log.
    val results = zipBundleM.root.transform(mleapFrame).get
    results.show()

    // Return the parsed response to the caller.
    val answer: Integer = results.select("prediction").get.collect().head.getDouble(0).toInt

    Future.successful(answer)
  }

  override def metadata() = ServiceCall { _ =>
    println("*** PROCESSING REQUEST TO 'metadata' API ***")

    // Load the model from disk.
    val zipBundleM = (for(bundle <- managed(BundleFile(modelPath))) yield {
      bundle.loadMleapBundle().get
    }).opt.get

    val updatedTimestamp = LocalDateTime.parse(zipBundleM.info.timestamp)
    val formattedTimestamp = updatedTimestamp.format(DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a"))

    val answer = ModelSummary(zipBundleM.info.name, formattedTimestamp, zipBundleM.info.version)
    Future.successful(answer)
  }
}
