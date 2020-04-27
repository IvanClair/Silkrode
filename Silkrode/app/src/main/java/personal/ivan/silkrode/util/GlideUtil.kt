package personal.ivan.silkrode.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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
        url: String?,
        completeListener: (() -> Unit)? = null
    ) {
        Glide.with(imageView)
            .load(url)
            .apply(mRequestOptions)
            .transition(mTransitionOptions)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ) = false

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    completeListener?.invoke()
                    return false
                }
            })
            .into(imageView)
    }
}