
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseUrl("http://127.0.0.1:8000")
		.inferHtmlResources()
		.acceptHeader("image/webp,image/apng,image/*,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "none",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Sec-Fetch-Dest" -> "image",
		"Sec-Fetch-Mode" -> "no-cors",
		"Sec-Fetch-Site" -> "same-origin")



	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/startmenu/addPlayer?name=caro")
			.headers(headers_0)
			.resources(http("request_1")
			.get("/favicon.ico")
			.headers(headers_1)))
		.pause(1)
		.exec(http("request_2")
			.get("/startmenu/addPlayer?name=helen")
			.headers(headers_0)
			.resources(http("request_3")
			.get("/favicon.ico")
			.headers(headers_1)))
		.pause(1)
		.exec(http("request_4")
			.get("/ninja/flag?row=1&col=1")
			.headers(headers_0)
			.resources(http("request_5")
			.get("/favicon.ico")
			.headers(headers_1)))
		.pause(1)
		.exec(http("request_6")
			.get("/ninja/flag?row=4&col=4")
			.headers(headers_0)
			.resources(http("request_7")
			.get("/favicon.ico")
			.headers(headers_1)))
		.pause(1)
		.exec(http("request_8")
			.get("/ninja/relStore")
			.headers(headers_0)
			.resources(http("request_9")
			.get("/favicon.ico")
			.headers(headers_1)))
		.pause(1)
		.exec(http("request_10")
			.get("/ninja/walk?row=1&col=0&dir=down")
			.headers(headers_0)
			.resources(http("request_11")
			.get("/favicon.ico")
			.headers(headers_1)))
		.pause(1)
		.exec(http("request_12")
			.get("/ninja/next")
			.headers(headers_0)
			.resources(http("request_13")
			.get("/favicon.ico")
			.headers(headers_1)))
		.pause(1)
		.exec(http("request_14")
			.get("/ninja/relLoad")
			.headers(headers_0)
			.resources(http("request_15")
			.get("/favicon.ico")
			.headers(headers_1)))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}