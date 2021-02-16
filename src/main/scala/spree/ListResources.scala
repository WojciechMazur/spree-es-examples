package spree
import com.sksamuel.elastic4s.ElasticDsl._

object ListResources extends App {

  esClient
    .execute(catNodes())
    .await
    .fold(
      f => println(s"Failed to get nodes: ${f.error}"),
      resp => {
        println("Got nodes:")
        resp.foreach { node =>
          println(s"Node ${node.name} runs on port ${node.port}")
        }
      }
    )

  esClient
    .execute(catIndices())
    .await
    .fold(
      f => println(s"Failed to get nodes: ${f.error}"),
      resp => {
        println("Got indices")
        resp
          .filterNot(_.index.startsWith("."))
          .zipWithIndex
          .foreach {
            case (idx, n) =>
              println(s"#$n. ${idx.index} - ${idx.health} - ${idx.status}")
          }
      }
    )

  esClient.close()
}
