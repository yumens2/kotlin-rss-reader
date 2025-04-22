package study

import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

// RSS 데이터 매핑 클래스
data class BlogPost22(val title: String, val link: String, val pubDate: Date)

// RSS 데이터 파싱 함수
fun fetchRssFrom2(url: String): List<BlogPost2> {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val xml = builder.parse(URL(url).openStream())
    val channel = xml.getElementsByTagName("channel").item(0) as Element
    val items = channel.getElementsByTagName("item")

    val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    val posts = mutableListOf<BlogPost2>()

    for (i in 0 until items.length) {
        val item = items.item(i) as Element
        val title = item.getElementsByTagName("title").item(0).textContent
        val link = item.getElementsByTagName("link").item(0).textContent
        val pubDateStr = item.getElementsByTagName("pubDate").item(0)?.textContent ?: ""
        val pubDate = try { formatter.parse(pubDateStr) } catch (e: Exception) { Date(0) }
        posts += BlogPost2(title, link, pubDate)
    }
    return posts
}

// 실행 함수: 검색 UI + 출력 UI 구현
fun main() {
    val rssFeeds = listOf(
        "https://techblog.woowahan.com/feed",
         "https://tech.kakao.com/blog/feed",
        "https://helloworld.kurly.com/feed"
    )

    val allPosts = rssFeeds.flatMap { fetchRssFrom2(it) }
        .sortedByDescending { it.pubDate } // 날짜 내림차순 정렬
        .take(10) // ✅ [x] 상위 10개 출력

    val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    while (true) {
        // 검색어 입력 UI
        print("\n검색어를 입력하세요 (없으면 전체 출력): ")
        val keyword = ""

        //  키워드 포함 필터링
        val filtered = if (keyword.isEmpty()) allPosts else allPosts.filter {
            it.title.contains(keyword, ignoreCase = true)
        }

        //  출력 UI 구현
        println()
        filtered.forEachIndexed { index, post ->
            println("[${index + 1}] ${post.title} (${dateFormat.format(post.pubDate)}) - ${post.link}")
        }
    }
}
