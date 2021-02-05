//
//  ChainViews.swift
//  CLup
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele.
//  Copyright © 2021 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele. All rights reserved.
//


import SwiftUI

let screenWidth = UIScreen.main.bounds.width

struct ChainView: View {
    @State var showModal: Bool = false
    let chain: Chain
    let city: String

    var body: some View {
        NavigationLink(destination: StoresOfChain(chain: chain, city: city)) {
            VStack(spacing: 0) {
                Image(uiImage: chain.image).resizable().scaledToFit()
                Text(chain.name)
                    .fontWeight(.medium)
                    .font(.title3)
                    .tint(.blueLabel)
                    .frame(height: 50)
            }.background(.blueHeaderBG)
        }
    }
}

struct StoresOfChain: View {
    @ObservedObject var chain: Chain
    let city: String
    var body: some View {
        ScrollView(.vertical, showsIndicators: false) {
            ForEach(Array(chain.stores.values), id: \.id) { store in
                StoreView(store: store).cornerRadius(10).padding()
            }
        }.onAppear() {
            DB.controller.getStores(chain: chain, city: city) { error in
                guard error == nil else {return print(error!)}
            }
        }
    }
}

struct StoreView: View {
    let store: Store
    var body: some View {
        NavigationLink(destination: StoreDetails(store: store)) {
            VStack(spacing: 0) {
                Image(uiImage: store.image).resizable().scaledToFit()
                Text(store.name)
                    .fontWeight(.medium)
                    .font(.title3)
                    .tint(.blueLabel)
                    .frame(height: 50)
            }
        }.background(.blueHeaderBG)
    }
}

struct StoreDetails: View {
    let store: Store

    var body: some View {
        ScrollView(.vertical, showsIndicators: false) {
            Image(uiImage: store.image).resizable().scaledToFit()
            Text(store.name)
                .fontWeight(.bold)
                .font(.title2)
            Text(store.address.description)
                .fontWeight(.medium)
                .font(.headline)
            VStack(spacing: 0) {
                SizedDivider(height: 7)
                Text("Working hours")
                    .fontWeight(.bold)
                    .font(.headline)
                SizedDivider(height: 5)
                VStack(spacing: 0) {
                    HStack {
                        Text("Sunday")
                        Spacer()
                        VStack {
                            ForEach(store.workingHours.sunday, id: \.self) { interval in
                                Text("\(interval.start.description) - \(interval.end.description)")
                            }
                        }
                    }.padding(.horizontal)
                    SizedDivider(height: 5)
                    Rectangle().frame(height: 1).padding(.horizontal)
                    SizedDivider(height: 5)
                }
                VStack(spacing: 0) {
                    HStack {
                        Text("Monday")
                        Spacer()
                        VStack {
                            ForEach(store.workingHours.monday, id: \.self) { interval in
                                Text("\(interval.start.description) - \(interval.end.description)")
                            }
                        }
                    }.padding(.horizontal)
                    SizedDivider(height: 5)
                    Rectangle().frame(height: 1).padding(.horizontal)
                    SizedDivider(height: 5)
                }
                VStack(spacing: 0) {
                    HStack {
                        Text("Tuesday")
                        Spacer()
                        VStack {
                            ForEach(store.workingHours.tuesday, id: \.self) { interval in
                                Text("\(interval.start.description) - \(interval.end.description)")
                            }
                        }
                    }.padding(.horizontal)
                    SizedDivider(height: 5)
                    Rectangle().frame(height: 1).padding(.horizontal)
                    SizedDivider(height: 5)
                }
                VStack(spacing: 0) {
                    HStack {
                        Text("Wednesday")
                        Spacer()
                        VStack {
                            ForEach(store.workingHours.wednesday, id: \.self) { interval in
                                Text("\(interval.start.description) - \(interval.end.description)")
                            }
                        }
                    }.padding(.horizontal)
                    SizedDivider(height: 5)
                    Rectangle().frame(height: 1).padding(.horizontal)
                    SizedDivider(height: 5)
                }
                VStack(spacing: 0) {
                    HStack {
                        Text("Thursday")
                        Spacer()
                        VStack {
                            ForEach(store.workingHours.thursday, id: \.self) { interval in
                                Text("\(interval.start.description) - \(interval.end.description)")
                            }
                        }
                    }.padding(.horizontal)
                    SizedDivider(height: 5)
                    Rectangle().frame(height: 1).padding(.horizontal)
                    SizedDivider(height: 5)
                }
                VStack(spacing: 0) {
                    HStack {
                        Text("Friday")
                        Spacer()
                        VStack {
                            ForEach(store.workingHours.friday, id: \.self) { interval in
                                Text("\(interval.start.description) - \(interval.end.description)")
                            }
                        }
                    }.padding(.horizontal)
                    SizedDivider(height: 5)
                    Rectangle().frame(height: 1).padding(.horizontal)
                    SizedDivider(height: 5)
                }
                VStack(spacing: 0) {
                    HStack {
                        Text("Saturday")
                        Spacer()
                        VStack {
                            ForEach(store.workingHours.saturday, id: \.self) { interval in
                                Text("\(interval.start.description) - \(interval.end.description)")
                            }
                        }
                    }.padding(.horizontal)
                    SizedDivider(height: 10)
                }
            }
            .lightBlueCard()
            HStack {Spacer(); LineUpButton(store: store); Spacer()}
                .lightBlueCard()
            HStack {Spacer(); BookingButton(store: store); Spacer()}
                .lightBlueCard()
        }
        .tint(.blueLabel)
    }
}
