package com.github.couchtracker.server.model.common.list

import com.github.couchtracker.server.model.api.users.lists.ApiListItem
import com.github.couchtracker.server.model.api.users.lists.ApiListItemGroupValue
import com.github.couchtracker.server.util.list.ListSorter
import com.github.couchtracker.server.util.list.SimpleListSorter
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ListSorterType {

    sealed class DirectionalSorterType : ListSorterType() {
        abstract val direction: ListSortDirection
    }

    abstract fun getSorter(): ListSorter

    // TYPE-INDEPENDENT SORTERS
    @Serializable
    @SerialName("manual")
    data class Manual(override val direction: ListSortDirection) : DirectionalSorterType() {

        override fun getSorter() = object : SimpleListSorter<Int>(direction) {
            override fun getComparable(index: Int, item: ApiListItem) = index
        }
    }

    @Serializable
    @SerialName("random")
    data class Random(val seed: Int? = null) : ListSorterType() {

        override fun getSorter() = object : SimpleListSorter<Long>(ListSortDirection.ASC) {
            override fun getComparable(index: Int, item: ApiListItem) = when (seed) {
                null -> kotlin.random.Random
                else -> kotlin.random.Random(seed)
            }.nextLong()
        }
    }

    // COMMON SORTERS
    @Serializable
    @SerialName("alphabetical")
    data class Alphabetical(override val direction: ListSortDirection) : DirectionalSorterType() {

        override fun getSorter() = object : SimpleListSorter<String>(direction) {
            override fun getComparable(index: Int, item: ApiListItem) = when (item) {
                is ApiListItem.Show -> item.item.name
            }

            override fun getGroup(comparable: String) = ApiListItemGroupValue.Initial(comparable)
        }
    }

    @Serializable
    @SerialName("add_date")
    data class AddDate(override val direction: ListSortDirection) : DirectionalSorterType() {

        override fun getSorter() = object : SimpleListSorter<Instant>(direction) {
            override fun getComparable(index: Int, item: ApiListItem) = item.added
            override fun getGroup(comparable: Instant) = ApiListItemGroupValue.Date(comparable)
        }
    }

    @Serializable
    @SerialName("popularity")
    data class Popularity(override val direction: ListSortDirection) : DirectionalSorterType() {

        override fun getSorter() = object : SimpleListSorter<Double>(direction) {
            override fun getComparable(index: Int, item: ApiListItem): Double? {
                TODO("To be implemented when popularity is available in model")
            }
        }
    }

    @Serializable
    @SerialName("rating")
    data class Rating(override val direction: ListSortDirection) : DirectionalSorterType() {

        override fun getSorter() = object : SimpleListSorter<Double>(direction) {
            override fun getComparable(index: Int, item: ApiListItem) = when (item) {
                is ApiListItem.Show -> item.item.ratings.avg()
            }
            override fun getGroup(comparable: Double) = ApiListItemGroupValue.Rating(comparable)
        }
    }

    @Serializable
    @SerialName("personal_rating")
    data class PersonalRating(override val direction: ListSortDirection) : DirectionalSorterType() {

        override fun getSorter() = object : SimpleListSorter<Double>(direction) {
            override fun getComparable(index: Int, item: ApiListItem) = when (item) {
                is ApiListItem.Show -> TODO("To be implemented when personal rating is implemented")
            }
            override fun getGroup(comparable: Double) = ApiListItemGroupValue.Rating(comparable)
        }
    }

    // SHOW SPECIFIC SORTERS
    @Serializable
    @SerialName("last_watched_episode")
    data class LastWatchedEpisode(override val direction: ListSortDirection) : DirectionalSorterType() {

        override fun getSorter() = object : SimpleListSorter<Instant>(direction) {
            override fun handledTypes() = listOf(ApiListItem.Show::class)
            override fun getComparable(index: Int, item: ApiListItem): Instant? {
                TODO("To be implemented when viewings are implemented")
            }
            override fun getGroup(comparable: Instant) = ApiListItemGroupValue.Date(comparable)
        }
    }
}
