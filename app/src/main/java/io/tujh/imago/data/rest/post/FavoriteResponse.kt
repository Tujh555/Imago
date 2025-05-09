package io.tujh.imago.data.rest.post

import com.google.gson.annotations.SerializedName

class FavoriteResponse(
    @SerializedName("in_favorite")
    val inFavorite: Boolean
)