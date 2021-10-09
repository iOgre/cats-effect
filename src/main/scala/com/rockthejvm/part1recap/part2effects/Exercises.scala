package com.rockthejvm.part1recap.part2effects
import Effects.MyIO
import scala.io.StdIn.readLine

object Exercises {

  /*
        Exercises
        1. an IO which returns the current time of the system
        2. an IO which measures the duration of computation
        3. IO which print something to the console
        4. IO which reads line (string) from std input
   */

  //1
  val clock: MyIO[Long] = MyIO(() => System.currentTimeMillis)

  //2
  def measure[A](comp: MyIO[A]): MyIO[Long] = for {
    start <- clock
    _ <- comp
    endTime <- clock
  } yield endTime - start

  /*
    clock.flatMap(start => comp.flatMap(_ => clock.map(endTime => endTime - start)))
    clock.map(endTime => endTime - start) = MyIO(() => clock.unsafeRun() - start)

    clock.map(endTime => endTime - start) = MyIO(() => System.currentTimeMillis() - start)

    =>   clock.flatMap(start => comp.flatMap(_ => MyIO(() => System.currentTimeMillis() - start)))
        
          comp.flatMap(lambda) = MyIO(() => lambda(comp.unsafeRun()))
                               = MyIO(() => lambda(___COMP___).unsafeRun())
                               = MyIO(() => MyIO(() => System.currentTimeMillis() - start).unsafeRun())
                               = MyIO( () => MyIO( () => System.currentTimeMillis() - start)).unsafeRun())
                               = MyIO(() => System.currentTimeMillis_after_computation() - start)

    =>  clock.flatMap(start => MyIO(() => System.currentTimeMillis_after_computation() - start))
    =   MyIO(() => lambda(System.currentTimeMillis).unsafeRun())                            
  */              
 
  //3
  def printIO[A](what: A): MyIO[Unit] = MyIO(() => println(what))

  //4
  val inputLine: MyIO[String] = MyIO(() => readLine)

  def computation: MyIO[String] = MyIO(() => {
    println("working")
    Thread.sleep(1000)
    "hello"
  })

  def main(args: Array[String]): Unit = {
    val process = for {
      _ <- printIO("Enter something")
      str <- inputLine
      _ <- printIO("And another")
      str2 <- inputLine
      _ <- printIO(s"Entered: $str and $str2")
      tm <- measure(computation)
      _ <- printIO(s"Execution tooks $tm")
    } yield ()

     process.unsafeRun()
    
  }
}
