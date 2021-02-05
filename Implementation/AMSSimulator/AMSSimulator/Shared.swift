//
//  Shared.swift
//  AMSSimulator
//
//  Created by Vincenzo Riccio on 04/02/2021.
//

import Foundation
import SwiftUI

class Shared {
    static let instance = Shared()
    private init(){}
    
    let storeID = "44af9545-64ac-11eb-a3e0-dca632747890"
    var numberOfPeople: Int? = nil
    var token: String? = nil
    
    func cache(token: String, numOfP: Int) {
        self.token = token
        numberOfPeople = numOfP
    }
    func emptyCache() {
        self.token = nil
        numberOfPeople = nil
    }
}

let defAlert = Alert(title: Text("Oh no!"), message: Text("Something went wrong"), dismissButton: .default(Text("Got it!")))
let okPopup = Alert(title: Text("Perfect!"), message: Text("Everything went fine"), dismissButton: .default(Text("Got it!")))
