//package study
//
////git commit --no-verify -m "강제 커밋"
//
//import java.net.URL
//import java.text.SimpleDateFormat
//import java.util.*
//import javax.xml.parsers.DocumentBuilderFactory
//import org.w3c.dom.Element
//import kotlinx.coroutines.*
//import kotlin.time.Duration.Companion.seconds
//
//// RSS 데이터를 담을 클래스 (제목, 링크, 날짜)
//data class BlogPostUpg(val subject: String, val url: String, val date: Date)
//
//// RSS URL로부터 데이터를 읽어오는 함수
//// RSS XML 파일을 열어 파싱하여 BlogPostUpg 객체 리스트로 반환함
//fun loadRssFeed(feedUrl: String): List<BlogPostUpg> {
//    val factory = DocumentBuilderFactory.newInstance() // XML 파서 팩토리 생성
//    val builder = factory.newDocumentBuilder() // 파서 생성
//    val xml = builder.parse(URL(feedUrl).openStream()) // URL의 RSS XML 읽기
//    val channel = xml.getElementsByTagName("channel").item(0) as Element
//    val items = channel.getElementsByTagName("item") // <item> 태그 목록 수집
//
//    val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH) // 날짜 포맷
//    val rssResults = mutableListOf<BlogPostUpg>() // 결과 저장 리스트
//
//    for (i in 0 until items.length) { // 각 item 반복
//        val item = items.item(i) as Element
//        val subject = item.getElementsByTagName("title").item(0).textContent
//        val url = item.getElementsByTagName("link").item(0).textContent
//        val dateStr = item.getElementsByTagName("pubDate").item(0)?.textContent ?: ""
//        val date = try { formatter.parse(dateStr) } catch (e: Exception) { Date(0) }
//        rssResults += BlogPostUpg(subject, url, date) // 객체 추가
//    }
//    return rssResults
//}
//
//// 실행 함수: 주기적 RSS 확인 + 키워드 필터 UI
//fun monitorBlogFeed() = runBlocking {
//    // 수집 대상 RSS URL 목록 정의
//    val rssUrls = listOf(
//        "https://techblog.woowahan.com/feed",
//        "https://tech.kakao.com/blog/feed",
//        "https://helloworld.kurly.com/feed",
//        "https://rss.app/feeds/Pueu2lECid2IOtEO.xml"
//    )
//
//    val formatter = SimpleDateFormat("yyyy-MM-dd") // 출력용 날짜 포맷
//    var previousFeeds: List<BlogPostUpg> = emptyList() // 이전 상태 저장용 리스트
//
//    // 10분마다 새 RSS 수집 및 알림
//    launch(Dispatchers.IO) {
//        while (isActive) {
//            val currentFeeds = rssUrls.flatMap { loadRssFeed(it) } // 모든 피드 합치기
//                .sortedByDescending { it.date } // 최신순 정렬
//                .take(10) // 상위 10개만 유지
//
//            // 기존에 없던 글만 필터링
//            val newFeeds = currentFeeds.filterNot { newItem ->
//                previousFeeds.any { oldItem -> oldItem.subject == newItem.subject && oldItem.url == newItem.url }
//            }
//
//            // 새 글이 있으면 출력
//            if (previousFeeds.isNotEmpty() && newFeeds.isEmpty()) {
//                println("\n새로운 글이 등록되었습니다!")
//                newFeeds.forEach { post ->
//                    println("[NEW] ${post.subject} (${formatter.format(post.date)}) - ${post.url}")
//                }
//            }
//
//            previousFeeds = currentFeeds // 상태 갱신
//            delay(1.seconds) // 5초 대기 후 반복
//        }
//    }
//
//    // 사용자 입력을 통한 필터 기능 실행
//    launch {
////        while (isActive) {
//            print("\n검색어를 입력하세요 (없으면 전체 출력): ")
//            val keyword = readlnOrNull()?.trim().orEmpty() // 사용자 키워드 입력 받기
//
//            // 검색어 필터 적용
//            val filtered = if (keyword.isEmpty()) previousFeeds else previousFeeds.filter {
//                it.subject.contains(keyword, ignoreCase = true)
//            }
//
//            // 필터된 결과 출력
//            println()
//            filtered.forEachIndexed { index, post ->
//                println("[${index + 1}] ${post.subject} (${formatter.format(post.date)}) - ${post.url}")
//            }
//        }
////    }
//}
//
//// 메인 함수: 프로그램 실행 시작 지점
//fun main() {
//    monitorBlogFeed() // 전체 실행 함수 호출
//}