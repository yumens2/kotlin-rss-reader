package study

import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

data class BlogPostUpg(val subject: String, val url: String, val date: Date)

val dateFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
val outputFormatter = SimpleDateFormat("yyyy-MM-dd")

val rssUrls = listOf(
    "https://techblog.woowahan.com/feed",
    "https://tech.kakao.com/blog/feed",
    "https://helloworld.kurly.com/feed",
    "https://rss.app/feeds/Pueu2lECid2IOtEO.xml"
)

suspend fun loadRssFeed(feedUrl: String): List<BlogPostUpg> = withContext(Dispatchers.IO) {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val xml = builder.parse(URL(feedUrl).openStream())
    val channel = xml.getElementsByTagName("channel").item(0) as Element
    val items = channel.getElementsByTagName("item")

    val rssResults = mutableListOf<BlogPostUpg>()
    for (i in 0 until items.length) {
        val item = items.item(i) as Element
        val subject = item.getElementsByTagName("title").item(0).textContent
        val url = item.getElementsByTagName("link").item(0).textContent
        val dateStr = item.getElementsByTagName("pubDate").item(0)?.textContent ?: ""
        val date = try { dateFormatter.parse(dateStr) } catch (e: Exception) { Date(0) }

        rssResults += BlogPostUpg(subject, url, date)
    }
    rssResults
}

suspend fun fetchAllRssFeeds(rssUrls: List<String>): List<BlogPostUpg> = coroutineScope {
    val deferredList = rssUrls.map { url -> async { loadRssFeed(url) } }
    deferredList.awaitAll().flatten()
}

// RSS 모니터링용 함수
fun CoroutineScope.launchRssMonitor(
    rssUrls: List<String>,
    previousFeedsRef: MutableList<BlogPostUpg>
) = launch(Dispatchers.IO) {
    while (isActive) {
        val currentFeeds = fetchAllRssFeeds(rssUrls)
            .sortedByDescending { it.date }
            .take(10)

        val newFeeds = currentFeeds.filterNot { newItem ->
            previousFeedsRef.any { it.subject == newItem.subject && it.url == newItem.url }
        }

        if (previousFeedsRef.isNotEmpty() && newFeeds.isNotEmpty()) {
            println("\n 새로운 글이 등록되었습니다!")
            newFeeds.forEach {
                println("[NEW] ${it.subject} (${outputFormatter.format(it.date)}) - ${it.url}")
            }
        }

        // 리스트 내부 값만 갱신
        previousFeedsRef.clear()
        previousFeedsRef.addAll(currentFeeds)

        delay(30.seconds)
    }
}

// 검색 프롬프트 기능
fun CoroutineScope.launchSearchPrompt(
    previousFeedsRef: List<BlogPostUpg>
) = launch {
    while (isActive) {
        print("\n검색어를 입력하세요 (없으면 전체 출력): ")
        val keyword = readlnOrNull()?.trim().orEmpty()

        val filtered = if (keyword.isEmpty()) previousFeedsRef else previousFeedsRef.filter {
            it.subject.contains(keyword, ignoreCase = true)
        }

        println()
        filtered.forEachIndexed { index, post ->
            println("[${index + 1}] ${post.subject} (${outputFormatter.format(post.date)}) - ${post.url}")
        }
    }
}

// 실행 메인 함수
fun monitorBlogFeed() = runBlocking {
    val previousFeeds: MutableList<BlogPostUpg> = mutableListOf()

    launchRssMonitor(rssUrls, previousFeeds)
    launchSearchPrompt(previousFeeds)
}

// 시작점
fun main() {
    println("A)================================================")
    println(Date(0))
    println("==================================================")

    monitorBlogFeed()

    println("B)================================================")
    println(Date(0))
    println("==================================================")
}