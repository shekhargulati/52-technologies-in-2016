package medium

object domainObjects {

  case class User(id: String, username: String, name: String, url: String, imageUrl: String)

  case class PostRequest(title: String, contentFormat: String, content: String, tags: Array[String] = Array(), canonicalUrl: Option[String] = None, publishStatus: String = "public", license: String = "all-rights-reserved")

  case class Post(id: String, publicationId: Option[String] = None, title: String, authorId: String, tags: Array[String], url: String, canonicalUrl: String, publishStatus: String, publishedAt: Long, license: String, licenseUrl: String)

}
