package spree

import com.sksamuel.elastic4s.Indexable

import java.util.Date

sealed trait Gender
case object Female extends Gender
case object Male   extends Gender

sealed trait MartialStatus
case object Unmarried extends MartialStatus
case object Married   extends MartialStatus

final case class Employee(
    firstName: String,
    lastName: String,
    designation: String,
    salary: Int,
    dateOfJoining: Date,
    address: String,
    gender: Gender,
    age: Int,
    martialStatus: MartialStatus,
    interests: Seq[String]
)
