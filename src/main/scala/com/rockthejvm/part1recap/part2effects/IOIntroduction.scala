package com.rockthejvm.part1recap.part2effects

import cats.effect.IO
import scala.io.StdIn
import cats.effect.unsafe.implicits.global
object IOIntroduction {

  val ourFirstIO: IO[Int] = IO.pure(42)

  //val bad: IO[Unit] = IO.pure(println("bad thing"))
  //    val quiteBad: IO[() => Unit] = IO.pure(() => println("bad thing"))

  val aDelayedIO: IO[Int] = IO.delay {
    println("producing an integer")
    54
  }
  val aDelayedIOv2: IO[Int] = IO { //apply ==  delay
    println("I am producing an integer too")
    54
  }

  // map, flatMap
  val improvedMeaningOfLife = ourFirstIO.map(_ * 2)

  val printedMeaningOfLife = ourFirstIO.flatMap(m => IO.delay(println(m)))

  val putStr: String => IO[Unit] = str => IO(println(str))
  def smallProgram(): IO[Unit] = for {
    _ <- putStr("hello")
    line1 <- IO(StdIn.readLine())
    line2 <- IO(StdIn.readLine())
    _ <- IO(println(line1 + line2))

  } yield ()

  //mapN - combine IO effects as tuples
  import cats.syntax.apply._
  val combinedMOL = (ourFirstIO, improvedMeaningOfLife).mapN(_ + _)

  def smallProgramV2 = (IO(StdIn.readLine()), IO(StdIn.readLine())).mapN(_ + _).map(println)

  /*
    Excercises:
   */

  //1 - sequence two IOs and take result of LAST one (first must be succeed)
  def sequenceTakeLast[A, B](ioa: IO[A], iob: IO[B]): IO[B] = ioa.flatMap(_ => iob)

  def sequenceTakeLast2[A, B](ioa: IO[A], iob: IO[B]) = ioa *> iob //andThen operator

  def sequenceTakeLast3[A, B](ioa: IO[A], iob: IO[B]) = ioa >> iob //andThen operator with by name call

  //2 - sequence two IOs and take result of FIRST one (last must be succeed)
  def sequenceTakeFirst[A, B](ioa: IO[A], iob: IO[B]): IO[A] = ioa.flatMap(a => iob.map(_ => a))

  def sequenceTakeFirst2[A, B](ioa: IO[A], iob: IO[B]): IO[A] = ioa <* iob
  //3 - repeat IO effect forever

  def forever[A](io: IO[A]): IO[A] = io.flatMap(_ => forever(io))

  def forever_v2[A](ioa: IO[A]): IO[A] = ioa >> forever_v2(ioa)

  def forever_v3[A](ioa: IO[A]): IO[A] = ioa *> forever_v3(ioa) //EAGER, will crash even without .unsafeRunSync

  def forever_v4[A](ioa: IO[A]): IO[A] = ioa.foreverM //with tail recursion

  // 4 convert an IO effect to a different type

  def convert[A, B](ioa: IO[A], value: B): IO[B] = ioa.map(_ => value)

  def convert_v2[A, B](ioa: IO[A], value: B): IO[B] = ioa.as(value)

  // 5 - discard value in IO

  def asUnit[A](ioa: IO[A]) = ioa.map(_ => ())

  def asUnit_v2[A](ioa: IO[A]) = ioa.as(()) // discourage, don't use in code

  def asUnit_v3[A](ioa: IO[A]) = ioa.void //preferrable toUnit way

  //6 fix stack recursion
  def sum(n: Int): Int = if (n <= 0) 0
  else n + sum(n - 1)

  def sumIO(n: Int): IO[Int] = {
    def accum(ion: IO[Int], ioacc: IO[Int]): IO[Int] = {
      ion.flatMap {
        case 0   => ioacc
        case gtz => accum(IO.pure(gtz - 1), (ion, ioacc).mapN(_ + _))
      }
    }
    accum(IO.pure(n), IO.pure(0))
  }

  def sumIO_v1(n:Int): IO[Int] = 
    if(n <= 0) IO(0)
    else for {
      lastNumber <- IO(n)
      previousSum <- sumIO_v1(n - 1)
    } yield previousSum + lastNumber





  // 7 (hard) - write a fibonacci IO that does not crash on recursion
  def inner(n: Int): Long = {
    n match {
      case 0     => 0
      case 1     => 1
      case 2     => 1
      case value => inner(value - 1) + inner(value - 2)
    }
  }

  def fibobacci(n: Int): IO[BigInt] =  
    if(n <= 2) IO(1)
    for {
      last <- IO(fibobacci(n - 1)).flatMap(x => x)
      prev <- IO(fibobacci(n - 2)).flatMap(x => x)
    } yield last + prev

  def main(args: Array[String]): Unit = {

    println(fibobacci(6).unsafeRunSync())
  //  (1 to 10).foreach(i => println(fibobacci(i).unsafeRunSync()))

    // forever_v2(IO {
    //   println("forever")
    //   Thread.sleep(1000)
    // }) //.unsafeRunSync()

    //println("go og ")
    //println(inner(50))

    //println(fibobacci(66).unsafeRunSync())

    //val aaaa = 999999
    //val aaaa = 10

    //val attempt = sumIO(aaaa).unsafeRunSync()
    // println(s"answer: $attempt")
//    println(s"AAA ${sum(aaaa)}")

  /*   val ioa1 = IO(println("tf first-1"))
    val iob1 = IO {
      println("tf last - 1")
      42
    }

    val first = sequenceTakeFirst(ioa1, iob1)

    val last = sequenceTakeLast(
      IO {
        println("tl first-2")
      },
      IO {
        println("tl last - 2")
        42
      }
    )

    val another = sequenceTakeLast2(
      IO {
        println("tl2 first-2")
      },
      IO {
        println("tl2 last - 2")
        42
      }
    )

    val data = forever(IO {
      println(System.currentTimeMillis)
      Thread.sleep(500)
      60
    }) */

    //println(data)
    //val another = sequenceTakeLast2(IO(5 / 4), IO(7)).unsafeRunSync()
    //println(first)
    //println(another.unsafeRunSync())
    //println(another)

    // "end of the world"
    //println(aDelayedIO.unsafeRunSync())
    //smallProgram().unsafeRunSync()
    //smallProgramV2.unsafeRunSync()
  }

}
