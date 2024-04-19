package mobi.sevenwinds.app.author

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route

fun NormalOpenAPIRoute.author() {
    route("/author") {
        route("/add").post<Unit, AuthorRecord, AuthorCreateRequest>(info("Добавить нового автора")) { param, body ->
            respond(AuthorService.addAuthor(body))
        }


    }
}

data class AuthorRecord(
    val id: Int,
    val fullname: String,
    val createdDate: String,
)

data class AuthorCreateRequest(
    val fullname: String
)
