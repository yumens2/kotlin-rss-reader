package study

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.*

class RunBlockingTest2 {

    private val rssList = listOf(
        "https://techblog.woowahan.com/feed",
        "https://tech.kakao.com/blog/feed",
        "https://helloworld.kurly.com/feed",
        "https://rss.app/feeds/Pueu2lECid2IOtEO.xml"
    )

    private val 날짜포맷터 = SimpleDateFormat("yyyy-MM-dd")

    @Test
    fun `RSS 피드를 불러오면 결과가 비어있지 않아야 한다`() = runTest {
        val 피드 = loadRssFeed(rssList[0])
        assertTrue(피드.isNotEmpty(), "피드는 비어있지 않아야 합니다")
        assertTrue(피드.first().subject.isNotBlank(), "제목은 비어있으면 안 됩니다")
        assertTrue(피드.first().url.startsWith("http"), "URL은 http로 시작해야 합니다")
    }

    @Test
    fun `모든 RSS를 병합하여 리스트로 반환해야 한다`() = runTest {
        val 병합된피드 = fetchAllRssFeeds(rssList)
        assertTrue(병합된피드.size > 5, "병합된 결과는 5개 이상이어야 합니다")
        val 중복제거 = 병합된피드.distinctBy { it.subject + it.url }
        assertEquals(중복제거.size, 병합된피드.size, "중복된 피드가 없어야 합니다")
    }

    @Test
    fun `최신 날짜순으로 정렬되어 있어야 한다`() = runTest {
        val 피드목록 = fetchAllRssFeeds(rssList)
        val 정렬된 = 피드목록.sortedByDescending { it.date }
        assertEquals(정렬된, 정렬된.sortedByDescending { it.date }, "날짜 기준으로 내림차순 정렬되어야 합니다")
    }

    @Test
    fun `날짜 파싱 실패시 기본 날짜는 Date0으로 대체되어야 한다`() = runTest {
        val 잘못된피드 = listOf(
            BlogPostUpg("잘못된 날짜", "http://example.com", Date(0))
        )
        assertEquals(0, 잘못된피드.first().date.time, "날짜가 없으면 Date(0)으로 대체되어야 합니다")
    }
}
