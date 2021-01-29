import SwiftUI

let screenWidth = UIScreen.main.bounds.width

struct ChainView: View {
    @State var showModal: Bool = false
    let chain: Chain

    var body: some View {
//        NavigationLink(destination: ChainView(chain: Chain())) {
//            HStack {
//                Text("Your tasks")
//                    .font(.system(.title, design: .rounded))
//                Spacer()
//                Image(systemName: "chevron.right")
//                    .font(.headline)
//                    .orange()
//            }.padding(.horizontal, 20)
//        }.accentColor(getColor(.orange))
        Button(action: {self.showModal.toggle()}) {
            VStack(spacing: 0) {
                Image(uiImage: chain.image).resizable().scaledToFit()
                Text(chain.name)
                    .fontWeight(.medium)
                    .font(.title3)
                    .tint(.blueLabel)
                    .frame(width: screenWidth-screenWidth/10, height: 50)
                    .background(.blueHeaderBG)
            }
            .frame(width: screenWidth-screenWidth/10)
            .cornerRadius(10)
        }
        .customButtonStyle()
        .shadow(color: Color(.systemGray3), radius: 3)
    }
}

struct StoreList: View {
    let stores: [Store]
    var body: some View {
        VStack {
            ScrollView(.vertical, showsIndicators: false) {
                ForEach(stores, id: \.id) { store in
                    SizedDivider(height: 10)
                    NavigationLink(destination: StoreDetails(store: store)) {
                        StoreView(store: store)
                    }
                }
            }
        }
    }
}

struct StoreView: View {
    @State var showModal: Bool = false
    let store: Store

    var body: some View {
        Button(action: {self.showModal.toggle()}) {
            VStack(spacing: 0) {
                Image(uiImage: store.image).resizable()
                Text(store.name)
                    .fontWeight(.medium)
                    .font(.title3)
                    .tint(.blueLabel)
                    .frame(width: screenWidth-screenWidth/10, height: 50)
                    .background(.blueHeaderBG)
            }
            .frame(width: screenWidth-screenWidth/10, height: 220)
            .cornerRadius(10)
        }
        .customButtonStyle()
        .shadow(color: Color(.systemGray3), radius: 3)
        .sheet(isPresented: self.$showModal) { StoreDetails(store: store) }
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
                SizedDivider(height: 5)
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
            .frame(width: screenWidth-screenWidth/10)
            .cornerRadius(10)
            .background(.blueHeaderBG)
            LineUpButton(ete: "50")
                .frame(width: screenWidth-screenWidth/10)
                .cornerRadius(10)
                .background(.blueHeaderBG)
            BookingButton()
                .frame(width: screenWidth-screenWidth/10)
                .cornerRadius(10)
                .background(.blueHeaderBG)
        }
        .tint(.blueLabel)
//        .sheet(isPresented: self.$showModal) { DetailedView(need: task, user: needer, isDiscoverSheet: false) }
    }
}
