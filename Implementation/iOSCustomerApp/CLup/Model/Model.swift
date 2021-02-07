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
typealias CUUID = String
typealias Duration = Int //minuti

struct Token: CustomStringConvertible {
    public var description: String {return "\(uuid) - \(hfid)"}
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
        let firstIndex = time.firstIndex(of: ":")!
        hour = String(time[time.startIndex..<firstIndex])
        let secondPart = time[time.index(after: time.firstIndex(of: ":")!)..<time.endIndex]
        let secondIndex = secondPart.firstIndex(of: ":") ?? secondPart.endIndex
        minute = String(secondPart[secondPart.startIndex..<secondIndex])
    }
}

struct CTimeInterval {
    let startingDateTime: Date
    let duration: Duration
}

struct DayInterval: Hashable {
    let day: Int
    let start: Time
    let end: Time
}

struct WorkingHours {
    var wh: [Int:[DayInterval]]
    var monday: [DayInterval] {return wh[1]!}
    var tuesday: [DayInterval] {return wh[2]!}
    var wednesday: [DayInterval] {return wh[3]!}
    var thursday: [DayInterval] {return wh[4]!}
    var friday: [DayInterval] {return wh[5]!}
    var saturday: [DayInterval] {return wh[6]!}
    var sunday: [DayInterval] {return wh[7]!}
    
    init() {
        wh = [:]
        wh[1] = []; wh[2] = []; wh[3] = []; wh[4] = []; wh[5] = []; wh[6] = []; wh[7] = []
    }
}


enum VRState: Int {
    case pending = 0
    case ready = 1
    case fulfilled = 2
    case completed = 3
}

class Chain: ObservableObject {
    let name: String
    let description: String
    let image: UIImage
    @Published var stores: [String:Store] = [:] //Manipolato dall'init degli store
    
    init(name: String, description: String, image: UIImage) {
        self.name = name
        self.description = description
        self.image = image
    }
    
    func addStore(_ store: Store) {
        DispatchQueue.main.async { self.stores[store.id] = store }
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
    
    public var description: String {return "\(state) LUR with token \(visitToken) for store \(store.id). Ete = \(String(describing: ete))"}
    
    init(numberOfPeople: Int, visitToken: Token, state: VRState, ete: Date?, store: Store) {
        self.numberOfPeople = numberOfPeople
        self.visitToken = visitToken
        self.state = state
        self.ete = ete
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
    
    public var description: String {return "\(state) BR with token \(visitToken) for store \(store.id)"}
    
    init(numberOfPeople: Int, visitToken: Token, state: VRState, desiredTimeInterval: CTimeInterval, sections: [Section], store: Store) {
        self.numberOfPeople = numberOfPeople
        self.visitToken = visitToken
        self.state = state
        self.desiredTimeInterval = desiredTimeInterval
        self.sections = sections
        self.store = store
    }
    
    func listOfSections() -> String {
        if sections.first == nil {
            return "None"
        }
        var x = sections.first!.name
        for sect in sections.dropFirst() {
            x = x + ", " + sect.name
        }
        return x
    }
}
