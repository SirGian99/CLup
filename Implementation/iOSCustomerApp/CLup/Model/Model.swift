//
//  Model.swift
//  CLup
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele.
//  Copyright © 2021 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele. All rights reserved.
//

import Foundation
import UIKit

typealias HFID = String
typealias CUUID = UUID
typealias Duration = Int

struct Token: CustomStringConvertible {
    public var description: String {return hfid}
    let hfid: HFID
    let uuid: CUUID
}

struct Address: CustomStringConvertible {
    public var description: String {return "\(streetName), \(streetNumber) - \(city)"}
    let streetName: String
    let streetNumber: String
    let city: String
    let postalCode: String
    let country: String
}

struct Time: CustomStringConvertible, Hashable {
    public var description: String {return "\(hour):\(minute)"}
    let hour: String
    let minute: String
    
    init(hour: String, minute: String) {
        self.hour = hour
        self.minute = minute
    }
    
    init(time: String) {
        self.hour = String(time[time.startIndex..<time.firstIndex(of: ":")!])
        self.minute = String(time[time.index(after: time.firstIndex(of: ":")!)..<time.endIndex])
    }
}

struct CTimeInterval {
    let startingDateTime: Date
    let duration: Duration
    //var date: DateComponents {return Calendar.current.dateComponents([.day, .month, .year], from: startingDateTime)}
    //var start: DateComponents {return Calendar.current.dateComponents([.day, .minute], from: startingDateTime)}
}

struct DayInterval: Hashable {
    let day: Int
    let start: Time
    let end: Time
}

struct WorkingHours {
    var wh: [Int:[DayInterval]]
    var sunday: [DayInterval] {return wh[1]!}
    var monday: [DayInterval] {return wh[2]!}
    var tuesday: [DayInterval] {return wh[3]!}
    var wednesday: [DayInterval] {return wh[4]!}
    var thursday: [DayInterval] {return wh[5]!}
    var friday: [DayInterval] {return wh[6]!}
    var saturday: [DayInterval] {return wh[7]!}
    
    init(test: Bool = false) {
        wh = [:]
        wh[1] = []; wh[2] = []; wh[3] = []; wh[4] = []; wh[5] = []; wh[6] = []; wh[7] = []
        if test {
            wh[1] = [DayInterval(day: 0, start: Time(hour: "10", minute: "00"), end: Time(hour: "11", minute: "00")), DayInterval(day: 1, start: Time(hour: "17", minute: "00"), end: Time(hour: "18", minute: "00"))]
            wh[2] = [DayInterval(day: 1, start: Time(hour: "10", minute: "00"), end: Time(hour: "11", minute: "00")), DayInterval(day: 1, start: Time(hour: "17", minute: "00"), end: Time(hour: "18", minute: "00"))]
            wh[3] = [DayInterval(day: 2, start: Time(hour: "09", minute: "00"), end: Time(hour: "11", minute: "00")), DayInterval(day: 1, start: Time(hour: "17", minute: "00"), end: Time(hour: "19", minute: "00"))]
            wh[4] = [DayInterval(day: 3, start: Time(hour: "10", minute: "00"), end: Time(hour: "11", minute: "00")), DayInterval(day: 1, start: Time(hour: "17", minute: "00"), end: Time(hour: "18", minute: "00"))]
        }
    }
}

var wh = WorkingHours(test: true)

enum VRState: Int {
    case pending = 0
    case ready = 1
    case fulfilled = 2
    case completed = 3
}

class Chain {
    let name: String
    let description: String
    let image: UIImage
    var stores: [String:Store] = [:] //Manipolato dall'init degli store
    
    init(name: String, description: String, image: UIImage) {
        self.name = name
        self.description = description
        self.image = image
    }
    
    func addStore(_ store: Store) {
        stores[store.id] = store
    }
    
    init() {
        self.name = "name\(Int.random(in: 1..<100))"
        self.description = "description"
        self.image = UIImage(imageLiteralResourceName: "MissingImg")
    }
}

class Section {
    var id: String
    var name: String
    
    init(id: String, name: String) {
        self.id = id
        self.name = name
    }
}

class Store {
    let id: String
    let name: String
    private let _description: String?
    private let _image: UIImage?
    let address: Address
    let currentOccupancy: Int
    let workingHours: WorkingHours
    let estimatedQueueDisposalTime: Duration
    let sections: [Section]
    let chain: Chain?
    
    var description: String {
        _description ?? chain?.description ?? "Description not available"
    }
    
    var image: UIImage {
        _image ?? chain?.image ?? UIImage(named: "MissingImg")!
    }
    
    init(id: String, name: String, description: String?, image: UIImage?, address: Address, currentOccupancy: Int, workingHours: WorkingHours, estimatedQueueDisposalTime: Duration, sections: [Section], chain: Chain?) {
        self.id = id
        self.name = name
        self._description = description
        self._image = image
        self.address = address
        self.currentOccupancy = currentOccupancy
        self.workingHours = workingHours
        self.estimatedQueueDisposalTime = estimatedQueueDisposalTime
        self.sections = sections
        self.chain = chain
        chain?.addStore(self)
    }
    
    init(chain: Chain? = nil) {
        self.id = "\(Int.random(in: 1..<100))"
        self.name = "Store\(Int.random(in: 1..<100))"
        self._description = "description"
        self._image = nil
        self.address = Address(streetName: "via \(Int.random(in: 1..<100))", streetNumber: "\(Int.random(in: 1..<100))", city: "Prova", postalCode: "01234", country: "Italy")
        self.currentOccupancy = 10
        self.workingHours = wh
        self.estimatedQueueDisposalTime = 600
        self.chain = chain
        self.sections = [
            Section(id: "\(Int.random(in: 1..<10))", name: "Sezione\(Int.random(in: 1..<10))"),
            Section(id: "\(Int.random(in: 1..<10))", name: "Sezione\(Int.random(in: 1..<10))"),
            Section(id: "\(Int.random(in: 1..<10))", name: "Sezione\(Int.random(in: 1..<10))")
        ]
        chain?.addStore(self)
    }
}

protocol VisitRequest: ObservableObject, CustomStringConvertible {
    var numberOfPeople: Int { get }
    var store: Store { get }
    var visitToken: Token { get }
    var state: VRState { get }
}


class LineUpRequest: VisitRequest {
    let numberOfPeople: Int
    let visitToken: Token
    let state: VRState
    let store: Store
    let ete: Date?
    
    public var description: String {return "LUR with token \(visitToken)"}
    
    init(numberOfPeople: Int, visitToken: Token, state: VRState, ete: Date?, store: Store) {
        self.numberOfPeople = numberOfPeople
        self.visitToken = visitToken
        self.state = state
        self.ete = ete
        self.store = store
    }
    
    init(store: Store) {
        self.numberOfPeople = 3
        self.visitToken = Token(hfid: "L\(Int.random(in: 1..<100))", uuid: CUUID())
        self.state = .pending
        self.ete = Date(timeIntervalSinceNow: 6000)
        self.store = store
    }
}

class BookingRequest: VisitRequest {
    let numberOfPeople: Int
    let visitToken: Token
    let state: VRState
    let store: Store
    
    let desiredTimeInterval: CTimeInterval
    let sections: [Section]
    
    public var description: String {return "BR with token \(visitToken)"}
    
    init(numberOfPeople: Int, visitToken: Token, state: VRState, desiredTimeInterval: CTimeInterval, sections: [Section], store: Store) {
        self.numberOfPeople = numberOfPeople
        self.visitToken = visitToken
        self.state = state
        self.desiredTimeInterval = desiredTimeInterval
        self.sections = sections
        self.store = store
    }
    
    init(store: Store) {
        self.numberOfPeople = 3
        self.visitToken = Token(hfid: "B\(Int.random(in: 1..<100))", uuid: CUUID())
        self.state = .pending
        self.desiredTimeInterval = CTimeInterval(startingDateTime: Date(timeIntervalSinceNow: 60000), duration: 600)
        self.sections = store.sections
        self.store = store
    }
    
    func listOfSections() -> String {
        if sections.first == nil {
            return "None"
        }
        var x = sections.first!.name
        for sect in sections {
            x = x + ", " + sect.name
        }
        return x
    }
}
