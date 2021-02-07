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
        VStack(alignment: .center) {
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
                        if error != nil {print(error!); self.reset(); self.showAlert = true}
                        else {print("Good!"); self.previousButtonPressed = .access; self.showOkPopup = true}
                    }
                }) {
                    Text("ReqAccess")
                }.disabled(token == nil || previousButtonPressed != .none)
                Spacer()
                Button(action: {
                    Server.controller.confirmAccess() { error in
                        if error != nil {print(error!); self.showAlert = true}
                        else {print("Good!"); self.showOkPopup = true}
                        self.reset()
                    }
                }) {
                    Text("ConfAccess")
                }.disabled(token == nil || previousButtonPressed != .access)
            }.padding().alert(isPresented: $showAlert){defAlert}
            HStack {
                Button(action: {
                    Server.controller.requestExit(token: token!) { error in
                        if error != nil {print(error!); self.reset(); self.showAlert = true}
                        else {print("Good!"); self.previousButtonPressed = .exit; self.showOkPopup = true}
                    }
                }) {
                    Text("ReqExit")
                }.disabled(token == nil || previousButtonPressed != .none)
                Spacer()
                Button(action: {
                    Server.controller.confirmExit() { error in
                        if error != nil {print(error!); self.showAlert = true}
                        else {print("Good!"); self.showOkPopup = true}
                        self.reset()
                    }
                }) {
                    Text("ConfExit")
                }.disabled(token == nil || previousButtonPressed != .exit)
            }.padding().alert(isPresented: $showOkPopup){okPopup}
            Button(action: {
                self.reset()
            }) {
                Text("Reset")
            }
        }
    }
}

