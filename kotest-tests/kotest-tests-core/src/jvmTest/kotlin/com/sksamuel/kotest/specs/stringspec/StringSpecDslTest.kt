package com.sksamuel.kotest.specs.stringspec

import io.kotest.shouldThrow
import io.kotest.core.spec.style.StringSpec

class StringSpecDslTest : StringSpec() {
  init {
    "allowed" {
      shouldThrow<Exception> {
        "this should throw at runtime" {

        }
      }
    }
  }
}
