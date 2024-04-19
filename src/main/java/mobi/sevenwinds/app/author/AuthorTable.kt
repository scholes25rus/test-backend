package mobi.sevenwinds.app.author

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object AuthorTable : IntIdTable("author") {
    val fullname = text("fullname")
    val createdDate = datetime("created_date")
}

class AuthorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthorEntity>(AuthorTable)

    var fullname by AuthorTable.fullname
    var createdDate by AuthorTable.createdDate

    fun toResponse() = AuthorRecord(this.id.value,this.fullname, this.createdDate.toString())
}
