//
//  CLupApp.swift
//  CLup
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele.
//  Copyright © 2021 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele. All rights reserved.
//


import SwiftUI

@main
struct CLupApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @Environment(\.scenePhase) private var scenePhase
    @AppStorage("deviceToken") var devToken: String = ""
    @State var showAlert = false
    
    var body: some Scene {
        WindowGroup {
            TabViewController().accentColor(getColor(.blueLabel)).alert(isPresented: $showAlert) {defAlert}
        }
        .onChange(of: scenePhase) { newScenePhase in
            if newScenePhase == .active {
                if self.devToken != "" {
                    SI.controller.getMyRequests() { error in
                        if error != nil {print(error!); self.showAlert = true}
                    }
                }
            }
        }
        .onChange(of: scenePhase) { newScenePhase in
            if newScenePhase == .background {
                Repository.singleton.reset()
            }
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    @AppStorage("deviceToken") var devToken: String = ""
    override init() {
        super.init()
        if devToken == "" {
            let temp = UUID().uuidString.lowercased()
            print("Registro con \(temp)")
            SI.controller.register(token: temp) { error in
                if error != nil {fatalError(error!)}
                print("REGISTRAZIONE OK\nappID: \(temp)")
                self.devToken = temp
            }
        } else {
            print("appID: \(devToken)")
        }
    }
}
