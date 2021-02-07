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
    
    //In ordine!
    let storeIDs = [
        "44af7f16-64ac-11eb-a3e0-dca632747890",
        "44af9545-64ac-11eb-a3e0-dca632747890",
        "a8224c0b-6552-11eb-a3e0-dca632747890",
        "b9ab1420-6481-11eb-a3e0-dca632747890",
        "bbab1410-6481-11eb-a3e0-dca632747890"
    ]
    let storeNames = [
        "Rubattino",
        "Lambrate",
        "StoreDiTest",
        "RST Groceries",
        "FilledStore"
    ]
    
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
