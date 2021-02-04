//
//  Repository.swift
//  CLup
//
//  Created by Vincenzo Riccio on 30/01/2021.
//

import Foundation

class Repository: ObservableObject {
    static let singleton = Repository()
    @Published var stores: [String:Store] = [:]
    @Published var chains: [String:Chain] = [:]
    @Published var lur: LineUpRequest? = nil
    @Published var brs: [String:BookingRequest] = [:]

    private init(){}
    
    func storesArray() -> [Store] {
        return Array(stores.values)
    }
    
    func chainsArray() -> [Chain] {
        return Array(chains.values)
    }

}
