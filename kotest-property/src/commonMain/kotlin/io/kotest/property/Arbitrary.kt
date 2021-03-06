package io.kotest.property

import io.kotest.property.arbitrary.*
import kotlin.random.Random

/**
 * An [Arbitrary] is a type of [Gen] which generates two types of values: [edgecases] and [samples].
 *
 * Edge cases are values that are a common source of bugs. For example, a function using ints is
 * more likely to fail for common edge cases like zero, minus 1, positive 1, [Int.MAX_VALUE] and [Int.MIN_VALUE]
 * rather than random values like 965489. Therefore, edge cases can be included in sequences
 * generated by an arbitrary.
 *
 * Not all arbitraries will utilize edge cases. For example, if you define an integer generator
 * using a subset of the number space - say from 100 to 250,000 - then no edge cases are provided.
 *
 * Samples are chosen randomly from the sample space and are used to give a greater breadth to
 * the test cases. For example, in the case of a function using integers, these random values
 * could be from across the entire integer number line, or could be limited to a subset of ints
 * such as natural numbers or even numbers.
 */
interface Arbitrary<T> : Gen<T> {

   /**
    * Returns the values that are considered common edge case for the type.
    *
    * For example, for Strings this may include the empty string, a string with white space,
    * a string with unicode, and a string with non-printable characters.
    *
    * The result can be empty if for type T there are no common edge cases.
    *
    * @return the common edge cases for type T.
    */
   fun edgecases(): Iterable<T>

   /**
    * Returns a sequence of random sample values to be used for testing.
    *
    * @param random the [Random] instance to be used for generating values. This random instance is
    * seeded using the seed provided to the test framework so that tests can be deterministically re-run.
    * Implementations should honour the random provider whenever possible.
    *
    * @return the random test values as instances of [PropertyInput].
    */
   fun samples(random: Random): Sequence<PropertyInput<T>>

   override fun generate(random: Random): Sequence<PropertyInput<T>> =
      edgecases().map { PropertyInput(it) }.asSequence() + samples(random)

   companion object
}

fun <T, U> Arbitrary<T>.map(f: (T) -> U): Arbitrary<U> = object : Arbitrary<U> {
   override fun edgecases(): Iterable<U> = this@map.edgecases().map(f)
   override fun samples(random: Random): Sequence<PropertyInput<U>> =
      this@map.samples(random).map { it.map(f) }
}

fun <T> Arbitrary<T>.filter(predicate: (T) -> Boolean): Arbitrary<T> = object : Arbitrary<T> {
   override fun edgecases(): Iterable<T> = this@filter.edgecases().filter(predicate)
   override fun samples(random: Random): Sequence<PropertyInput<T>> =
      this@filter.samples(random).filter { predicate(it.value) }
}

/**
 * Returns a new [Arbitrary] where the edge cases of the receiver are replaced with the edge
 * cases given as input to this function. The samples are unchanged.
 */
fun <T> Arbitrary<T>.setEdgeCases(vararg edgecases: T): Arbitrary<T> = setEdgeCases(edgecases.asList())

fun <T> Arbitrary<T>.setEdgeCases(edgecases: Iterable<T>): Arbitrary<T> = object : Arbitrary<T> {
   override fun edgecases(): Iterable<T> = edgecases
   override fun samples(random: Random): Sequence<PropertyInput<T>> = this@setEdgeCases.samples(random)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Arbitrary.Companion.default(iterations: Int): Arbitrary<T> {
   val classname = T::class.simpleName ?: "<unknown>"
   return forClassName(classname, iterations) as Arbitrary<T>
}

fun Arbitrary.Companion.forClassName(className: String, iterations: Int): Arbitrary<*> {
   return when (className) {
      "java.lang.Integer", "kotlin.Int", "Int" -> Arbitrary.int(iterations)
      "java.lang.Long", "kotlin.Long", "Long" -> Arbitrary.long(iterations)
      "java.lang.Float", "kotlin.Float", "Float" -> Arbitrary.float(iterations)
      "java.lang.Double", "kotlin.Double", "Double" -> Arbitrary.double(iterations)
      else -> throw IllegalArgumentException("Cannot infer generator for $className; specify generators explicitly")
   }
}
