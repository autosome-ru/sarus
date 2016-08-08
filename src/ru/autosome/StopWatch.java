package ru.autosome;

/**
 * Created with IntelliJ IDEA.
 * User: nastia
 * Date: 18.03.14
 * Time: 23:34
 * To change this template use File | Settings | File Templates.
 */
public class StopWatch {

  private final long start;

  StopWatch() {
    this.start = System.currentTimeMillis();
  }

  double elapsedTime() {
    long now = System.currentTimeMillis();
    return (now - start);
  }

}
