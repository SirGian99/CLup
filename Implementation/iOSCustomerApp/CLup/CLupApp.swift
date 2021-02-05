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
    @State var showAlert = false
    
    var body: some Scene {
        WindowGroup {
            TabViewController().accentColor(getColor(.blueLabel)).alert(isPresented: $showAlert) {defAlert}
        }.onChange(of: scenePhase) { newScenePhase in
            if scenePhase == .inactive && newScenePhase == .active {
                DB.controller.getMyRequests() { error in
                    if error != nil {print(error!); self.showAlert = true}
                }
            }
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    @AppStorage("deviceToken") var devToken: String = ""
    override init() {
        super.init()
//        UNUserNotificationCenter.current().delegate = self
//        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge, .provisional]) { granted, error in
//            guard error == nil else {return}
//            DispatchQueue.main.sync { UIApplication.shared.registerForRemoteNotifications() }
//        }
        if devToken == "" {
            let temp = UUID().uuidString
            print("Registro con \(temp)")
            DB.controller.register(token: temp) { error in
                if error != nil {fatalError(error!)}
                print("REGISTRAZIONE OK\nappID: \(temp)")
                self.devToken = temp
            }
        } else {
            print("appID: \(devToken)")
        }
    }
    
//    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
//        let tokenParts = deviceToken.map { data in return String(format: "%02.2hhx", data) }
//        devToken = tokenParts.joined()
//        print("Registered for remote notifications with deviceToken \(devToken)")
//    }
//
//    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
//        print("Failed to register for remote notifications: \(error.localizedDescription)")
//    }
}
