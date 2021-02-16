package spree

import com.sksamuel.elastic4s.ElasticProperties
import com.sksamuel.elastic4s.ElasticClient._
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.HttpClient
import com.sksamuel.elastic4s.http.JavaClient

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object `package` {
  val esClient: ElasticClient = ElasticClient {
    JavaClient {
      ElasticProperties(
        9200.to(9202).map("http://localhost:" + _).mkString(","))
    }
  }
  implicit val executionContext: ExecutionContextExecutor =
    ExecutionContext.global
}
