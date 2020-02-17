//package ink.rubi.bilibili.live
//
//import com.fasterxml.jackson.databind.DeserializationFeature
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import ink.rubi.bilibili.live.JsonSerializerType.*
//import io.ktor.client.HttpClientConfig
//import io.ktor.client.features.json.GsonSerializer
//import io.ktor.client.features.json.JacksonSerializer
//import io.ktor.client.features.json.JsonFeature
//import io.ktor.client.features.json.JsonSerializer
//import io.ktor.http.ContentType
//import io.ktor.http.content.TextContent
//import io.ktor.util.KtorExperimentalAPI
//import java.util.*
//
//enum class JsonSerializerType {
//    Jackson,
//    Gson,
//    KotlinX
//}
//
//object JsonFacade {
//    var type: JsonSerializerType? = null
//    val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)!!
//    va
//    val backend: Any? = null
//    val serializerBySpi: JsonSerializer? = null
//    fun write(data: Any): String {
//        return (serializerBySpi!!.write(data, ContentType.Application.Json) as TextContent).text
//    }
//
//    fun read() {
//        GsonSerializer()
//    }
//}
//
//
//@KtorExperimentalAPI
//inline fun HttpClientConfig<*>.autoInstallJsonSerializer(block: (type: JsonSerializerType) -> Unit) {
//    val serializerBySpi =
//        ServiceLoader.load(JsonSerializer::class.java).firstOrNull() ?: JacksonSerializer()
//    install(JsonFeature) {
//        serializer = serializerBySpi
//        acceptContentTypes = acceptContentTypes + ContentType("text", "json")
//    }
//    val type = when (serializerBySpi.javaClass.name) {
//        "io.ktor.client.features.json.JacksonSerializer" -> Jackson
//        "io.ktor.client.features.json.GsonSerializer" -> Gson
//        "io.ktor.client.features.json.serializer.KotlinxSerializer" -> KotlinX
//        else -> TODO("wrong json serializer type")
//    }
//    block(type)
//}
////
////interface JsonSerializerFacade<tree> {
////
////    fun <T> read(json: String): T
////
////    fun <T> write(t: T): String
////
////    fun readTree(json: String): tree
////}
////
////class JacksonJsonSerializer : JsonSerializerFacade<JsonNode?> {
////    private val back = jacksonObjectMapper()
////    override fun <T> read(json: String): T {
////        return back.readValue(json, object : TypeReference<T>() {})
////    }
////
////    override fun <T> write(t: T): String {
////        return back.writeValueAsString(t)
////    }
////
////    override fun readTree(json: String): JsonNode? {
////        return back.readTree(json)
////    }
////}
////
////class JsonSerializerDelegate(type: JsonSerializerType) {
////    val useJsonSerializer: JsonSerializerFacade? = when (type) {
////        Jackson -> Gson
////        -> GsonBuilder().registerTypeAdapterFactory(KotlinAdapterFactory()).create()
////        KotlinX ->
////        else -> TODO("wrong json serializer type")
////    }
////}
////
////class GsonSerializer {
////
////}
//
//
////jacksonObjectMapper()
