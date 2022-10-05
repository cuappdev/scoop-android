package com.cornellappdev.scoop.data.models

import com.squareup.moshi.Json

data class User(
    var id: Int? = null,
    var netid: String? = null,
    // Need to ask backend about first name and last name json labels
    @Json(name = "first_name") var firstName: String? = null,
    @Json(name = "last_name") var lastName: String? = null,
    @Json(name = "phone_number") var phoneNumber: String? = null,
    var grade: String? = null,
    @Json(name = "profile_pic_url") var profilePicUrl: String? = null,
    var pronouns: String? = null
)