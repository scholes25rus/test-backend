package mobi.sevenwinds.app.author

import io.restassured.RestAssured
import mobi.sevenwinds.common.ServerTest
import mobi.sevenwinds.common.jsonBody
import mobi.sevenwinds.common.toResponse
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.Seconds
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthorApiKtTest : ServerTest() {

    @BeforeEach
    internal fun setup() {
        transaction {
            AuthorTable.deleteAll()
        }
    }

    @Test
    fun testAuthorCreation() {
        val authorName = "Александр Сергеевич Пушкин"

        RestAssured.given()
            .jsonBody(AuthorCreateRequest(authorName))
            .post("/author/add")
            .toResponse<AuthorRecord>().let { response ->
                println("${response.fullname} / ${response.id} / ${response.createdDate}")

                assertTrue(response.id > 0)
                assertEquals(authorName, response.fullname)

                val creationDateTime = DateTime.parse(response.createdDate)
                val difference = Seconds.secondsBetween(creationDateTime, DateTime.now()).seconds
                assertTrue(difference == 0)
            }
    }

}
