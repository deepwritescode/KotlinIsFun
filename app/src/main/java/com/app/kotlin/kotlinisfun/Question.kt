package com.app.kotlin.kotlinisfun

import java.io.Serializable
import java.util.*

/**
 * Created by deep on 5/31/18.
 */
class Question : Serializable {

    var id: String = ""
        internal set

    var creator: String = ""
        internal set

    var points: Long = 0
        internal set

    var question: String = ""
        internal set

    var answer: String = ""
        internal set

    var createdAt: Date? = null
        internal set
}