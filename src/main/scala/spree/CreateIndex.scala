package spree
import com.sksamuel.elastic4s.ElasticDate
import com.sksamuel.elastic4s.ElasticDsl._

import java.time.LocalDate
import java.util.Date
import java.util.concurrent.TimeUnit

object CreateIndex extends App {

  import com.sksamuel.elastic4s.requests.mappings._
  def setupCitiesIndex = esClient.execute {
    createIndex("trips")
      .mapping(properties(
        dateField("started"),
        dateField("ended"),
        keywordField("country"),
        keywordField("cities"),
        intField("photosTaken"),
        floatField("distanceCovered"),
        textField("description")
      ))
      .shards(3)
      .replicas(2)
  }

  def indexSingle() = esClient.execute {
    indexInto("trips").fields(
      "country"         -> "Italy",
      "started"         -> ElasticDate(LocalDate.of(2020, 2, 15)),
      "ended"           -> ElasticDate.fromTimestamp(1582224835L),
      "cities"          -> "Venice",
      "photosTaken"     -> 2173,
      "distanceCovered" -> 83.4,
      "description"     -> "The name of the city, deriving from Latin forms Venetia and Venetiae, is most likely taken from \"Venetia et Histria\", the Roman name of Regio X of Roman Italy, but applied to the coastal part of the region that remained under Roman Empire outside of Gothic, Lombard, and Frankish control."
    )
  }

  def indexMultiple() = esClient.execute {
    bulk(
      indexInto("trips").fields(
        "country"         -> "Italy",
        "started"         -> ElasticDate(LocalDate.of(2020, 6, 20)),
        "ended"           -> ElasticDate(LocalDate.of(2019, 6, 30)),
        "cities"          -> Seq("Florence", "Sienna", "San Gimignano", "Volterra"),
        "photosTaken"     -> 9001,
        "distanceCovered" -> 583.4,
        "description"     -> "Tuscany is the second most popular Italian region for travellers in Italy.[7] The main tourist spots are Florence, Pisa, Castiglione della Pescaia, Grosseto and Siena.[8] The town of Castiglione della Pescaia is the most visited seaside destination in the region,[8] with seaside tourism accounting for approximately 40% of tourist arrivals. ",
        "scootersUsed"    -> 2
      ),
      indexInto("trips").fields(
        "country"         -> "Switzerland",
        "started"         -> ElasticDate(LocalDate.of(2020, 8, 30)),
        "ended"           -> ElasticDate(LocalDate.of(2020, 9, 7)),
        "cities"          -> Seq("Zurich", "Lausanne", "Geneva"),
        "photosTaken"     -> 510,
        "distanceCovered" -> 250.4,
        "triedFoundee"    -> true,
        "description"     -> "The Romans built a military camp, which they called Lousanna, at the site of a Celtic settlement, near the lake where Vidy and Ouchy are situated; on the hill above was a fort called Lausodunon or Lousodunon (The \"-y\" suffix is common to many place names of Roman origin in the region (e.g.) Prilly, Pully, Lutry, etc.).[8] By the 2nd century AD, it was known as vikanor[um] Lousonnensium and in 280 as lacu Lausonio. By 400, it was civitas Lausanna, and in 990 it was mentioned as Losanna.[8]"
      )
    )
  }

  def searchForItalianTrips() =
    esClient
      .execute {
        search("trips")
          .query {
            boolQuery()
              .must(
                termQuery("country", "Italy"),
                rangeQuery("started").gt(ElasticDate("now-2y/y"))
              )
              .should(matchQuery("description", "Italian"))
          }
      }
      .map { res =>
        res.map { resp =>
          println(s"""
           |Search result #1
           |hits: ${resp.hits.total.value}
           |took: ${resp.took}ms
           |maxScore ${resp.maxScore}
           |""".stripMargin)
          resp.hits.hits.zipWithIndex.foreach {
            case (hit, idx) =>
              println(s"$idx#")
              println(hit.fields.mkString("\n"))
          }

        }

      }

  val res = for {
    index <- setupCitiesIndex
    _ = index.fold(println("Failed to crate index")) { resp =>
      println(s"index created: ${resp.acknowledged}")
    }
    indexSingle <- indexSingle()
    tripId = indexSingle.fold(
      _ => { println("failed to index single"); "" },
      resp => {
        println(s"""
           |indexed: ${resp.result}
           |id:      ${resp.id}
           |shard:   ${resp.shards}
           |version: ${resp.version}""".stripMargin)
        resp.id
      }
    )
    indexMultiple <- indexMultiple()
    _ = indexMultiple.map { resp =>
      println(s"""bulk result: 
         |errors: ${resp.errors}
         |took:   ${resp.took}ms""".stripMargin)
      resp.successes.foreach { item =>
        println(s"""
           |result: ${item.result}
           |id: ${item.id}
           |itemId: ${item.itemId}""".stripMargin)
      }
    }
    _ <- searchForItalianTrips()
  } yield tripId

  println(res.await)

  esClient.close()
}
