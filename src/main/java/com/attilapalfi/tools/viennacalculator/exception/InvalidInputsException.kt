package com.attilapalfi.tools.viennacalculator.exception

/**
 * Created by palfi on 2016-03-06.
 */
class InvalidInputsException : Exception {
    constructor() : super()

    constructor(p0: String?) : super(p0)

    constructor(p0: String?, p1: Throwable?) : super(p0, p1)

    constructor(p0: Throwable?) : super(p0)

    constructor(p0: String?, p1: Throwable?, p2: Boolean, p3: Boolean) : super(p0, p1, p2, p3)
}