package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    class ILikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "ILIKE")

    infix fun <T : String?> ExpressionWithColumnType<T>.ilike(pattern: String): Op<Boolean> =
        ILikeOp(this, QueryParameter(pattern, columnType))

    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val author = body.authorId?.let { id ->
                AuthorEntity.findById(id) ?: throw IllegalArgumentException(("Автор с id $id не найден"))
            }
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.author = author
            }

            return@transaction entity.toCreateResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query = BudgetTable.leftJoin(AuthorTable, { authorId }, { AuthorTable.id })
                .select { BudgetTable.year eq param.year }
                .apply {
                    if (param.authorName != null) {
                        andWhere { AuthorTable.fullname ilike "%${param.authorName}%" }
                    }
                }
                .orderBy(BudgetTable.month)
                .orderBy(BudgetTable.amount, SortOrder.DESC)
                .orderBy(BudgetTable.month)
                .orderBy(BudgetTable.amount, SortOrder.DESC)
            val total = query.count()

            val sumByType = BudgetEntity.wrapRows(query).map { it.toResponse() }.groupBy { it.type.name }
                .mapValues { it.value.sumOf { v -> v.amount } }

            val data = BudgetEntity.wrapRows(query.limit(param.limit, param.offset)).map { it.toResponse() }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = data
            )
        }
    }
}
