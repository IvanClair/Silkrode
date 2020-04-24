package personal.ivan.silkrode.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import javax.inject.Inject

class GlideUtil @Inject constructor(
    private val mRequestOptions: RequestOptions,
    private val mTransitionOptions: DrawableTransitionOptions
) {

    /**
     * Load podcast cover
     */
    fun loadPodcastCover(
        imageView: ImageView,
        url: String?
    ) {
        Glide.with(imageView)
            .load(url)
            .apply(mRequestOptions)
            .transition(mTransitionOptions)
            .into(imageView)
    }
}