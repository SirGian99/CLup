//
//  MainTabs.swift
//  CLup
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele.
//  Copyright © 2021 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele. All rights reserved.
//


import SwiftUI

struct FirstTab: View {
    @ObservedObject var repo = Repository.singleton
    @State var searchText = ""
    @State var showAlert = false
    var body: some View {
        let appearance = UINavigationBarAppearance()
        appearance.configureWithTransparentBackground()
        appearance.backgroundColor = UIColor.systemBackground
        UINavigationBar.appearance().standardAppearance = appearance
        return NavigationView {
            VStack(alignment: .center, spacing: 0) {
                SizedDivider(height: 15)
                SearchBar(text: $searchText, onClick: {
                    SI.controller.getChainStore(city: self.searchText){ (chains, autstores, error) in
                        guard error == nil else {print(error!); self.showAlert = true; return}
                        DispatchQueue.main.async {
                            Repository.singleton.chains = chains!
                            Repository.singleton.stores = autstores!
                        }
                    }
                })
                if !Repository.singleton.chains.isEmpty || !Repository.singleton.stores.isEmpty {
                    SizedDivider(height: 10)
                    ScrollView(.vertical, showsIndicators: false) {
                        if !Repository.singleton.chains.isEmpty {
                            Text("Chains").font(.title2).bold()
                            ForEach(repo.chainsArray(), id: \.name) { chain in
                                ChainView(chain: chain, city: searchText).cornerRadius(10).padding()
                            }
                        }
                        if !Repository.singleton.stores.isEmpty {
                            Text("Local stores").font(.title2).bold()
                            ForEach(repo.storesArray(), id: \.name) { store in
                                StoreView(store: store).cornerRadius(10).padding()
                            }
                        }
                    }
                }
                Spacer()
            }
            .alert(isPresented: $showAlert){defAlert}
            .navigationBarHidden(true).navigationBarTitleDisplayMode(.inline)
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

struct SecondTab: View {
    @ObservedObject var repo = Repository.singleton
    @State var selectedLUR: LineUpRequest? = nil //SwiftUI iOS14 bug!
    @State var selectedBR: BookingRequest? = nil //SwiftUI iOS14 bug!
    @State var showLURModal = false
    @State var showBRModal = false
    var body: some View {
        let sselectedLUR = selectedLUR //SwiftUI iOS14 bug!
        let sselectedBR = selectedBR //SwiftUI iOS14 bug!
        return VStack(spacing: 0) {
            SizedDivider(height: 5)
            ScrollView(.vertical, showsIndicators: false) {
                ForEach(repo.lurs.keys.sorted(), id: \.self) { key in
                    SizedDivider(height: 5)
                    Button(action: {
                        selectedLUR = repo.lurs[key]!
                        print("Selected lur with uuid: \(key)")
                        showLURModal.toggle()
                    }){
                        RequestPreview(req: repo.lurs[key]!)
                            .sheet(isPresented: $showLURModal, onDismiss: {self.selectedLUR = nil}){LURDetails(lur: sselectedLUR!)}
                    }
                }
                SizedDivider(height: 10)
                ForEach(repo.brs.keys.sorted(), id: \.self) { key in
                    SizedDivider(height: 5)
                    Button(action: {
                        selectedBR = repo.brs[key]!
                        print("Selected br with uuid: \(key)")
                        showBRModal.toggle()
                    }){
                        RequestPreview(req: repo.brs[key]!)
                            .sheet(isPresented: $showBRModal, onDismiss: {self.selectedBR = nil}){BRDetails(br: sselectedBR!)}
                    }
                }
            }
        }
    }
}
