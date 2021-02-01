//
//  Repository.swift
//  CLup
//
//  Created by Vincenzo Riccio on 30/01/2021.
//

import Foundation

let chain1 = Chain()
let chain2 = Chain()
let chain3 = Chain()
let chain4 = Chain()
let chain5 = Chain()
let chain6 = Chain()
let store1 = Store()
let store2 = Store()
let store3 = Store()
let store4 = Store(chain: chain4)
let store5 = Store(chain: chain5)
let store6 = Store(chain: chain6)
let lur1 = LineUpRequest(store: store4)
let br1 = BookingRequest(store: store5)

class Repository: ObservableObject {
    static let singleton = Repository()
    @Published var stores: [String:Store] = ["1":store1, "2":store2, "3":store3, "4":store4, "5":store5, "6":store6]
    @Published var chains: [String:Chain] = ["1":chain1, "2":chain2, "3":chain3, "4":chain4, "5":chain5, "6":chain6]
    @Published var lur: LineUpRequest? = lur1
    @Published var brs: [String:BookingRequest] = [br1.visitToken.uuid.uuidString:br1]

    private init(){}
    
    func storesArray() -> [Store] {
        return Array(stores.values)
    }
    
    func chainsArray() -> [Chain] {
        return Array(chains.values)
    }
    
    func brsArray() -> [BookingRequest] {
        return Array(brs.values).sorted { (br1, br2) -> Bool in
            return br1.desiredTimeInterval.startingDateTime < br2.desiredTimeInterval.startingDateTime
        }
    }
    
}
