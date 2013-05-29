package nl.gideondk.vlok.server

import akka.actor._
import math.BigInt

class VlokGenerator(mac: Long) extends Actor {
  private val signShift = 64
  private val macShift = 16
  private val sequenceBits = 16
  private val timestampShift = 64
  private val sequenceMask = -1L ^ (-1L << sequenceBits)

  var sequence: Long = 0
  var lastTimestamp = -1L

  def tilNextMillis(lastTimestamp: Long): Long = {
    var timestamp = timeGen
    while (timestamp <= lastTimestamp) {
      timestamp = timeGen
    }
    timestamp
  }

  def timeGen = System.currentTimeMillis()

  def nextId: BigInt = {
    var timestamp = timeGen

    if (timestamp < lastTimestamp) {
      throw new Exception("Clock is moving backwards!")
    }

    if (lastTimestamp == timestamp) {
      sequence = (sequence + 1) & sequenceMask
      if (sequence == 0) {
        timestamp = tilNextMillis(lastTimestamp)
      }
    } else {
      sequence = 0
    }

    lastTimestamp = timestamp

    (BigInt(timestamp) << timestampShift) + ((mac << macShift) | sequence)

  }

  def receive = {
    case GenerateID ⇒ sender ! nextId
  }
}

case object GenerateID