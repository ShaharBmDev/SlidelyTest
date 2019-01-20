package il.co.sbm.slidelytask.model.network
import com.fasterxml.jackson.annotation.JsonProperty


data class PixabayPhotoResponse(
    @JsonProperty("hits")
    val pixabayPhotoObjects: List<PixabayPhotoObject>,
    @JsonProperty("total")
    val total: Int,
    @JsonProperty("totalHits")
    val totalHits: Int
)

