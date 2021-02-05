//
//  ContentView.swift
//  AMSSimulator
//
//  Created by Vincenzo Riccio on 04/02/2021.
//

import SwiftUI

let screenH = UIScreen.main.bounds.height
let screenW = UIScreen.main.bounds.width
let qrframe = screenW < screenH/1.5 ? screenW : screenH/1.5

enum ButtonPressed {
    case access
    case exit
    case none
}

struct ContentView: View {
    let si = Shared.instance
    @State var token: String? = nil
    @State var showOkPopup: Bool = false
    @State var showAlert: Bool = false
    @State var previousButtonPressed: ButtonPressed = .none
    
    func reset() {
        previousButtonPressed = .none
        token = nil
        si.emptyCache()
    }
    
    var body: some View {
        VStack {
            Text("AMS Simulator\nStoreID: \(si.storeID)").font(.title2)
            if token == nil {
                CodeScannerView(codeTypes: [.qr], scanMode: .continuous, scanInterval: 4.0) {res in
                    self.token = res.rawBarcode
                }.frame(width: qrframe, height: qrframe)
            } else {
                ZStack {
                    Rectangle().foregroundColor(.gray).blur(radius: 5).frame(width: qrframe, height: qrframe)
                    Text("Scanned token: \(token!)").foregroundColor(.white)
                }
            }
            HStack {
                Button(action: {
                    Server.controller.requestAccess(token: token!) { error in
                        if error != nil {self.reset(); self.showAlert = true}
                        else {self.previousButtonPressed = .access; self.showOkPopup = true}
                    }
                }) {
                    Text("ReqAccess")
                }.disabled(token == nil || previousButtonPressed != .none)
                Spacer()
                Button(action: {
                    Server.controller.confirmAccess() { error in
                        if error != nil {self.showAlert = true}
                        else {self.showOkPopup = true}
                        self.reset()
                    }
                }) {
                    Text("ConfAccess")
                }.disabled(token == nil || previousButtonPressed != .access)
            }.padding()
            HStack {
                Button(action: {
                    Server.controller.requestExit(token: token!) { error in
                        if error != nil {self.reset(); self.showAlert = true}
                        else {self.previousButtonPressed = .exit; self.showOkPopup = true}
                    }
                }) {
                    Text("ReqExit")
                }.disabled(token == nil || previousButtonPressed != .none)
                Spacer()
                Button(action: {
                    Server.controller.confirmExit() { error in
                        if error != nil {self.showAlert = true}
                        else {self.showOkPopup = true}
                        self.reset()
                    }
                }) {
                    Text("ConfExit")
                }.disabled(token == nil || previousButtonPressed != .exit)
            }.padding()
            Button(action: {
                self.reset()
            }) {
                Text("Reset")
            }
        }.alert(isPresented: $showAlert){defAlert}.alert(isPresented: $showOkPopup){okPopup}
    }
}

