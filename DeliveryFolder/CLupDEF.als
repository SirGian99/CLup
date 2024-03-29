sig Date {}
sig TimeInterval {
  start : Int,
  end   : Int
} {
  start >= 0 and
  start < end
}
sig Token {} {
  Token = Visit_Request.visit_token
}
sig ID {} {
  ID = Manager.id
}

sig Manager {
  id     : ID,
  stores : some Store
}

sig Chain {
  stores : some Store
}

sig Store {
  parent_chain      : Chain,
  current_occupancy : Int,
  maximum_occupancy : Int,
  working_hours     : some TimeInterval,
  sections          : some Product_Section,
  visit_requests	: set Visit_Request,
  queue             : Queue,
  managers          : some Manager
} {
  current_occupancy >= 0 and
  current_occupancy <= maximum_occupancy
}

sig Queue {
  store                   : Store,
  length                  : Int,
  estimated_disposal_time : Int,
  requests                : set LineUp_Request
} {
  estimated_disposal_time >= 0 and
  length = #requests
}

sig Product_Section {
  store             : Store,
  current_occupancy : Int,
  maximum_occupancy : Int
} {
  current_occupancy >= 0 and
  maximum_occupancy > 0 and
  current_occupancy <= maximum_occupancy
}

abstract sig Visit_Request {
  store            : Store,
  number_of_people : Int,
  date_of_creation : Date,
  time_of_creation : Int,
  visit_token      : Token,
  customer         : Customer,
  visit			   : lone Visit
} {
  number_of_people > 0 and
  time_of_creation >= 0
}

sig LineUp_Request extends Visit_Request {
}

sig Booking_Request extends Visit_Request {
  desired_date     : Date,
  desired_interval : TimeInterval,
  product_sections : set Product_Section
}

sig Visit {
  request	    : Visit_Request,
  starting_time	: Int,
  ending_time	: lone Int 
}{
  starting_time >= ending_time
}

abstract sig Customer {
  lineUp_requests : set LineUp_Request
}

sig App_Customer extends Customer {
  booking_requests : set Booking_Request
}

sig Proxy_Customer extends Customer {
} {
  #lineUp_requests = 1
}




---------MAPPINGS--------
--A visit request is associated with a visit iff that visit is associated with that request
fact mapping_VR_Visit {
  all vr : Visit_Request, v : Visit | vr.visit = v iff v.request = vr
}

--A visit request refers to a certain store and that store has the considered visit request
--in the associated set of visit requests
fact mapping_VR_Store {
  all vr : Visit_Request, s : Store | vr.store = s iff (vr in s.visit_requests)
}

--A line-up request is in a store queue iff 
fact mapping_LUR_Queue {
  all lur: LineUp_Request, q: Queue | lur in q.requests iff (lur.store = q.store and lur.visit = none)
}

--A line-up request refers to a certain customer and that customer has the considered 
--request in the associated set of lineup requests
fact mapping_C_LUR {
  all c: Customer, lur: LineUp_Request | lur in c.lineUp_requests iff lur.customer = c
}

--A booking request refers to a certain app-customer and that app-customer has the 
--considered request in the associated set of booking requests
fact mapping_AC_BR {
  all ac: App_Customer, br: Booking_Request | br in ac.booking_requests iff br.customer = ac
}

--A store refers to a certain chain and that chain has the considered 
--store in the associated set of stores
fact mapping_Store_Chain {
  all chain: Chain, store: Store | store in chain.stores iff store.parent_chain = chain
}

--A queue refers to a certain store and that store has the considered queue as queue
fact mapping_Store_Queue {
  all q: Queue, s: Store | q.store = s iff s.queue = q
}

--A manager refers to certain stores and those store have the considered 
--manager in the associated set of managers
fact mapping_Store_Manager {
  all m: Manager, store: Store | m in store.managers iff store in m.stores
}

--A product section refers to a certain store and that store has the considered 
--section in the associated set of product sections
fact mapping_Store_Sections {
  all ps: Product_Section, s: Store | ps in s.sections iff s = ps.store
}

--A booking request refers to a certain store and to certain product section.
--Those sections must be sections of the store the request refers to
fact mapping_StoreSections_BookingSections {
  all ps: Product_Section, b: Booking_Request | ps in b.product_sections iff ps.store=b.store 
}






--------FUNCTIONS--------
fun inProgressVisitRequests[s: Store]: set Visit_Request {
  {vr: Visit_Request | vr.store = s and vr.visit != none }
}

fun storeOverlappingBooking[s: Store, t: TimeInterval, d: Date]: set Booking_Request {{
  b: Booking_Request |
    b in s.visit_requests and b.desired_date = d and
    ((b.desired_interval.start <= t.start and b.desired_interval.end >= t.start) or
    (b.desired_interval.start >= t.start and b.desired_interval.start <= t.end))
}}







----------FACTS----------
--A line-up request can be associated to its visit only if the visit starts the same day of
--the line-up request and after the lining up
fact visit_after_LineUp {
  all v: Visit, lur: LineUp_Request | 
    v.request = lur iff v.starting_time >= lur.time_of_creation	 
}

--A booking request can be associated to its visit only if the visit starts in the desidered date and
--after the desidered starting time
fact visit_after_Booking {
  all v: Visit, b: Booking_Request | 
    v.request = b iff v.starting_time >= b.desired_interval.start 	 
}

--A customer can line up for only a store at a time
fact one_LineUp_AtATime {
  all c: Customer | #{lur: LineUp_Request | lur in c.lineUp_requests and lur.visit=none}<=1
}

--A customer cannot visit more than one store at the same time
fact no_MoreThanOne_ActiveVisit {
	(all c: Proxy_Customer | #{vr: Visit_Request| vr in c.lineUp_requests and vr.visit != none and
	 vr.visit.ending_time = none}<=1)
  and
	(all c: App_Customer | #{vr: Visit_Request| vr.visit != none and
	 vr.visit.ending_time = none and (vr in c.lineUp_requests or vr in c.booking_requests)}<=1)
}

--A customer cannot book two overlapping time intervals among all the stores
fact no_OverlappingBookings_ForCustomers {
  all b1: Booking_Request, b2: Booking_Request |
  (
    b1!=b2 and b1.customer = b2.customer and b1.desired_date = b2.desired_date implies (
      (b1.desired_interval.end < b2.desired_interval.start)
      or
      (b1.desired_interval.start > b2.desired_interval.end)
    )
  )
}

--It is not possible to book a visit if the booking would make the store exceed
--its maximum occupancy
fact no_TooMany_OverlappingBookings {
  all s: Store, t: TimeInterval, d: Date |
    (sum b: storeOverlappingBooking[s, t, d] | b.number_of_people) <= s.maximum_occupancy
}

--It is not possible to book a visit which does not fits in the store working hours
fact no_Bookings_WhenClose {
  all br: Booking_Request | #{t: TimeInterval | t in br.store.working_hours 
    and (br.desired_interval.start >= t.start and br.desired_interval.end <= t.end)}>=1
}

--It is not possible to book a visit that starts before the current queue disposal time
fact no_Booking_Before_Queue{
  all b: Booking_Request | b.date_of_creation = b.desired_date implies 
	b.desired_interval.start > (b.time_of_creation + b.store.queue.estimated_disposal_time)
}

--It is not possible to line up when the store is closed
fact no_LineUps_WhenClose {
  all lu: LineUp_Request | #{t: TimeInterval | t in lu.store.working_hours 
    and lu.time_of_creation >= t.start and lu.time_of_creation <= t.end}>=1
}

--The current store occupancy is the number of people in the store at that moment
fact storeOccupancy {
  all s: Store | s.current_occupancy = (sum x: inProgressVisitRequests[s] | x.number_of_people)
}






-----UNICITY CONSTRAINTS------
--Each visit token is unique
fact uniqueToken {
  all t: Token, vr1: Visit_Request, vr2: Visit_Request | 
    t in Visit_Request.visit_token and
    (vr1.visit_token = t and vr2.visit_token = t implies vr1 = vr2)
}

--Each manager has a unique id
fact uniqueID {
  all id1 : ID, m1 : Manager, m2 : Manager | 
    id1 in Manager.id and
    (m1.id = id1 and m2.id = id1 implies m1 = m2)
}

--A product section cannot be shared between stores. Each store has its own product sections
fact uniqueProductSection {
  all ps : Product_Section, s1 : Store, s2 : Store | 
    ps in Store.sections and
    ps in s1.sections and ps in s2.sections implies s1 = s2
}





pred show {
#Chain = 2 
#Store = 3
#Visit >= 1
#Proxy_Customer >= 3 
#App_Customer >= 3
#Queue.requests >= 3
}
run show for 8

