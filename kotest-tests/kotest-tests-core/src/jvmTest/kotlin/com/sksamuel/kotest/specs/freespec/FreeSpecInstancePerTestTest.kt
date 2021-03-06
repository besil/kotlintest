//package com.sksamuel.kotest.specs.freespec
//
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestResult
//import io.kotest.core.spec.SpecConfiguration
//import io.kotest.shouldBe
//import io.kotest.specs.FreeSpec
//
//class FreeSpecInstancePerTestTest : FreeSpec() {
//
//  companion object {
//    var string = ""
//  }
//
//  override fun isolationMode() = IsolationMode.InstancePerTest
//
//  override fun finalizeSpec(spec: SpecConfiguration, results: Map<TestCase, TestResult>) {
//    string shouldBe "a_ab_abccc_ad_ade_"
//  }
//
//  init {
//    "a" - {
//      string += "a"
//      "b" - {
//        string += "b"
//        // since we execute this test 3 times, and we are in instance per test,
//        // the whole test should be re-executed 3 times
//        "c".config(invocations = 3) {
//          string += "c"
//        }
//      }
//      "d" - {
//        string += "d"
//        "e" {
//          string += "e"
//        }
//      }
//      string += "_"
//    }
//  }
//}
