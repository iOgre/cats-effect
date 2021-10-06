package com.rockthejvm.part1recap.part2effects

import scala.concurrent.Future
import com.rockthejvm.part1recap.part2effects.Effects.MyIO
import cats.syntax.flatMap
import com.rockthejvm.part1recap.part2effects.Effects

object Effects {

  def combine(a: Int, b: Int) = a + b
  val five = combine(2, 3)
  val fivev2 = 2 + 3
  val fivev3 = 5

  // referential transparency = can replace expression with its value
  // as many times as we want without changing behavior

  //side effects
  //example: print to the console
  val printSmth: Unit = println("Cats effect")
  val printSmthV2: Unit = () //not the same

  //example: change a variable
  var anInt = 0
  val changingVar: Unit = (anInt += 1)
  val changingVarV2: Unit = () //not the same

  //side effects are inevitable for useful programs

  //effect
  /*
  Effect types
   Properties:
       - type signature describes the kind of calculation that will be performed
       - type signature describes the VALUE that will be calculated
       - when side effects are needed, effect construction is separate from effect execution
   */

  /*
  example: Option
    - describes a possibly absent value
    - computes a value of type A if it exists
    - side effect are not needed
   */
  val anOption: Option[Int] = Option(42)

  /*
    example: Future
        - describes async computation
        - computes a value of type A if it's successful
        - side effect required (allocation/scheduling a thread), execution not separated from constr
   */
  import scala.concurrent.ExecutionContext.Implicits.global
  val aFuture: Future[Int] = Future(42)

  /*
    example MyIO data type from monads lesson
        - describes any computation that might produce side effects
        - calculates a value of type A, if it successful
        - side effects are required for the evaluation of () => A
            - YES, the creation of MyIO does NOT produce the side effects on construction
   */

  val anIO: MyIO[Int] = MyIO(() => {
    println("I'm writing something ")
    42
  })
  case class MyIO[A](unsafeRun: () => A) {
    def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsafeRun()))
    def flatMap[B](f: A => MyIO[B]): MyIO[B] = MyIO(() => f(unsafeRun()).unsafeRun())
  }

  def main(args: Array[String]): Unit = {
    anIO.unsafeRun()
  }
}

/*
    *Pure functional program = big expression computing a value
        - referential transparency = can replace an expression with its value without changing behavior

    *Expressions performing side effects are not replaceable
        -   i.e. break referential transparency

    *Effects = data type which
        - embodies a computational concept (e.g. side effect, absense of value)
        - referentially transparent

    *Effect properties
        - it describes what kind of computation it will perform
        - the type signature describes the value it will calculate
        - it separates effect description from effect execution
            (when externally visible side effects are produced)

 */
