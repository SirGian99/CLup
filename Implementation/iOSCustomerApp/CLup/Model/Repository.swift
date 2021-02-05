//
//  Repository.swift
//  CLup
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele.
//  Copyright © 2021 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele. All rights reserved.
//


import Foundation

class Repository: ObservableObject {
    static let singleton = Repository()
    @Published var stores: [String:Store] = [:]
    @Published var chains: [String:Chain] = [:]
    @Published var lurs: [String:LineUpRequest] = [:]
    @Published var brs: [String:BookingRequest] = [:]

    private init(){}
    
    func storesArray() -> [Store] {
        return Array(stores.values)
    }
    
    func chainsArray() -> [Chain] {
        return Array(chains.values)
    }
    
    func emptyStoreChain() {
        stores = [:]
        chains = [:]
    }

}
