package com.github.couchtracker.server.db

import com.github.couchtracker.server.db.model.ShowDbo
import com.github.couchtracker.server.db.model.ShowOrderingDbo
import org.litote.kmongo.coroutine.*

fun CoroutineDatabase.shows() = this.getCollection<ShowDbo>("shows")
fun CoroutineDatabase.showOrderings() = this.getCollection<ShowOrderingDbo>("showOrderings")