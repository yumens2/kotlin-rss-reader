package study

//import org.jsoup.Jsoup
//import org.jsoup.parser.Parser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.Test

data class BlogPost2(val title: String, val link: String, val pubDate: Date)

fun fetchRss(url: String): List<BlogPost2> {
    val doc = Jsoup.connect(url).parser(Parser.xmlParser()).get()
    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

    return doc.select("item").map {
        val title = it.selectFirst("title")?.text().orEmpty()
        val link = it.selectFirst("link")?.text().orEmpty()
        val pubDateStr = it.selectFirst("pubDate")?.text().orEmpty()
        val pubDate = try { dateFormat.parse(pubDateStr) } catch (e: Exception) { Date(0) }
        BlogPost2(title, link, pubDate)
    }
}

@Test
fun main() {
    val rssUrl = "https://tech.kakaobank.com/feed"
    val posts = fetchRss(rssUrl).sortedByDescending { it.pubDate }.take(10)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    while (true) {
        print("\n검색어를 입력하세요 (없으면 전체 출력): ")
        val keyword = readlnOrNull()?.trim().orEmpty()

        val filtered = if (keyword.isEmpty()) posts else posts.filter {
            it.title.contains(keyword, ignoreCase = true)
        }

        println()
        filtered.forEachIndexed { index, post ->
            println("[${index + 1}] ${post.title} (${dateFormat.format(post.pubDate)}) - ${post.link}")
        }
    }
}
