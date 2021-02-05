//
//  TabController.swift
//  CLup
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele.
//  Copyright © 2021 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele. All rights reserved.
//


import SwiftUI

struct TabViewController: View {
    let tab1 = FirstTab()
    let tab2 = SecondTab()
    @State var selectedIndex = 0
    @State var showAlert = false
    
    var body: some View {
        VStack(spacing: 0) {
            if selectedIndex == 0 {
                tab1
            } else if selectedIndex == 1 {
                tab2.onAppear{Repository.singleton.emptyStoreChain()}
            }
            Spacer()
            VStack(spacing: 0) {
                SizedDivider(height: 4, width: UIScreen.main.bounds.width)
                HStack {
                    HStack {
                        Spacer()
                        VStack(spacing: 2) {
                            Image(systemName: "bag").resizable().scaledToFit()
                            Text("Stores").font(.caption)
                        }.tintIf(selectedIndex == 0, .blueLabel, .gray)
                        .onTapGesture {self.selectedIndex = 0}
                        Spacer()
                    }
                    HStack {
                        Spacer()
                        VStack(spacing: 2) {
                            Image(systemName: "list.bullet").resizable().scaledToFit()
                            Text("Your requests").font(.caption)
                        }.tintIf(selectedIndex == 1, .blueLabel, .gray)
                        .onTapGesture {self.selectedIndex = 1}
                        Spacer()
                    }
                }
                SizedDivider(height: 22, width: UIScreen.main.bounds.width)
            }.frame(width: UIScreen.main.bounds.width, height: 75)
        }.edgesIgnoringSafeArea(.bottom).alert(isPresented: $showAlert) {defAlert}
    }
}
