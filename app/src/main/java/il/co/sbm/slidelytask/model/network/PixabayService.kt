package il.co.sbm.slidelytask.model.network

import io.reactivex.Observable
import retrofit2.http.GET

interface PixabayService {
    companion object Factory {
        const val BASE_URL = "https://pixabay.com/api/"
    }

    @GET("?key=11287892-fd0eb0862a15f32f981063923&image_type=photo&pretty=true")
    fun getTopPhotos(): Observable<PixabayPhotoResponse>
}