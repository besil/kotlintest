package com.sksamuel.kotest.extensions

import io.kotest.core.config.Project
import io.kotest.core.spec.description
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.extensions.ProjectListener
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.style.WordSpec
import io.kotest.shouldBe

object SpecExtensionNumbers {

  var before = 0
  var after = 0

  val ext = object : SpecExtension {
    override suspend fun intercept(spec: SpecConfiguration, process: suspend () -> Unit) {
      if (spec::class.description().name == "com.sksamuel.kotest.extensions.SpecExtensionTest") {
        before++
        process()
        println("AFTER SPEK")
        after++
      } else {
        process()
      }
    }
  }
}

object SpecSetup {
  fun setup() {
     Project.registerExtension(SpecExtensionNumbers.ext)
  }
}

class SpecExtensionTest : WordSpec() {

  init {

    SpecSetup.setup()

     Project.registerProjectListener(object : ProjectListener {
      override fun afterProject() {
        SpecExtensionNumbers.after shouldBe 1
      }
    })

    "SpecExtensions" should {
      "be activated by registration with ProjectExtensions" {
        SpecExtensionNumbers.before shouldBe 1
        SpecExtensionNumbers.after shouldBe 0
      }
      "only be fired once per spec class" {
        // the intercepts should not have fired again
        SpecExtensionNumbers.before shouldBe 1
        SpecExtensionNumbers.after shouldBe 0
      }
    }
  }
}
