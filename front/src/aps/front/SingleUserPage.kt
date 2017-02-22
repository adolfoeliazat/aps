package aps.front

import into.kommon.*

class SingleUserPage {
    object urlQuery : URLQueryParamsMarker {
        val id by LongURLParam()
    }

    suspend fun load(): PageLoadingError? {
        imf("527a8001-bae3-450f-aaa1-56ddc2d46865")
    }
}

