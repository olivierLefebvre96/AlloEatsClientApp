package fr.esgi.alloeatsclientapp.business.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.models.google.details.Result
import fr.esgi.alloeatsclientapp.utils.Google
import java.util.*


class RestaurantAdapter(context: Context, private val googleRestaurants: ArrayList<Result>) : BaseAdapter() {
    override fun getItem(position: Int) = googleRestaurants[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = googleRestaurants.size

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, savedView: View?, parent: ViewGroup?): View {
        var view = savedView
        val holder: ViewHolder

        if (view == null) {
            view = inflater.inflate(R.layout.restaurant_row, parent, false)
            holder = ViewHolder(view)
            view!!.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        return setHolderView(holder, getItem(position), view)
    }

    /**
     * Populate the data into the template view using the data object
     */
    private fun setHolderView(holder: ViewHolder, restaurant: Result, view: View): View {
        val sb = StringBuilder()

        holder.name.text = sb.append("Name: ").append(restaurant.name)
        sb.setLength(0)
        holder.isOpen.text = sb.append("Is open: ").append(toReadableOpenNow(restaurant.openingHours?.openNow))
        sb.setLength(0)
        holder.rating.text = sb.append("Rating: ").append(toReadableRating(restaurant))
        sb.setLength(0)
        holder.address.text = sb.append("Address: ").append(restaurant.vicinity)
        sb.setLength(0)

        if (restaurant.photos?.get(0)?.photoReference != null) {
            val maxWidth = 256
            val maxHeight = 256
            val photoReference = restaurant.photos?.get(0)?.photoReference
            val googleApiKey = Google.GOOGLE_BROWSER_API_KEY
            val photoUri = "https://maps.googleapis.com/maps/api/place/photo?" +
                    "maxwidth=$maxWidth&maxheight=$maxHeight" +
                    "&photoreference=$photoReference&key=$googleApiKey"

            Picasso.with(view.context)
                    .load(photoUri)
                    .placeholder(R.drawable.default_restaurant_icon)
                    .error(R.drawable.default_restaurant_icon)
                    .resize(256, 256)
                    .centerCrop()
                    .into(holder.mainPhoto)
        }

        return view
    }

    private fun toReadableRating(googleRestaurant: Result) : String{
        return if(googleRestaurant.rating == null) "No rating" else googleRestaurant.rating.toString()
    }

    private fun toReadableOpenNow(isOpenNow: Boolean?): String{
        if(isOpenNow == null) return "Unknown"
        if(isOpenNow) { return "Yes" }
        return "No"
    }

    internal class ViewHolder(view: View) {
        @BindView(R.id.name)
        lateinit var name: TextView

        @BindView(R.id.isOpen)
        lateinit var isOpen: TextView

        @BindView(R.id.rating)
        lateinit var rating: TextView

        @BindView(R.id.address)
        lateinit var address: TextView

        @BindView(R.id.mainPhoto)
        lateinit var mainPhoto: ImageView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
