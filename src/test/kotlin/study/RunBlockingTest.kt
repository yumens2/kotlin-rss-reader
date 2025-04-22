package study

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.*

class BlogFeedMonitorTest {

 private val format = SimpleDateFormat("yyyy-MM-dd")

 @Test
 fun `detects new blog posts correctly`() {
  // 이전 글 목록
  val previous = listOf(
   BlogPostUpg("글1", "https://example.com/1", format.parse("2024-04-01")),
   BlogPostUpg("글2", "https://example.com/2", format.parse("2024-04-02"))
  )

  // 현재 글 목록 (글2는 동일, 글3은 신규)
  val current = listOf(
   BlogPostUpg("글2", "https://example.com/2", format.parse("2024-04-02")),
   BlogPostUpg("글3", "https://example.com/3", format.parse("2024-04-03"))
  )

  monitorBlogFeed()

  print("끝")
  print(previous)
  print(current)

//  val result = detectNewFeeds(previous, current)

//  result.size shouldBe 1
//  result[0].subject shouldBe "글3"
 }

 @Test
 fun `returns empty list when no new posts exist`() {
  val previous = listOf(
   BlogPostUpg("글1", "https://example.com/1", format.parse("2024-04-01"))
  )
  val current = listOf(
   BlogPostUpg("글1", "https://example.com/1", format.parse("2024-04-01"))
  )

  monitorBlogFeed()

  print("끝")
  print(previous)
  print(current)
//  detectNewFeeds(previous, current).shouldBe(emptyList())
 }
}
