//
//  RequestViews.swift
//  CLup
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele.
//  Copyright © 2021 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele. All rights reserved.
//


import SwiftUI

struct RequestPreview<Request:VisitRequest>: View {
    let req: Request
    
    var body: some View {
        VStack(spacing: 5) {
            HStack {
                Text(req is LineUpRequest ? "Line-up" : "Booking").font(.title3)
                Spacer()
                Text(req.visitToken.hfid).font(.title3).bold()
            }.padding()
            Text("\(req.store.name), \(req.store.address.description)").padding()
        }.tint(.blueLabel).lightBlueCard()
    }
}

struct LURDetails: View {
    let lur: LineUpRequest
    @State var showAlert = false
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        let timeToWait = lur.ete != nil ? Int(Date().distance(to: lur.ete!)/60) : 0
        let timeToWaitStr: String!
        if timeToWait < 0 {
            timeToWaitStr = "We are sorry, you need to wait a bit more"
        } else {
            timeToWaitStr = timeToWait != 0 ? (timeToWait >= 60 ? ">1hr" : "\(timeToWait) min") : "Enter now!"
        }
        return VStack(alignment: .center, spacing: 10) {
            SizedDivider(height: 5)
            VStack (spacing: 0) {
                Text(lur.store.name)
                    .fontWeight(.medium)
                    .font(.title)
                Text(lur.store.address.description)
                    .fontWeight(.medium)
                    .font(.headline)
            }
            VStack(spacing: 0) {
                SizedDivider(height: 6)
                Text("Line-up details")
                    .fontWeight(.bold)
                    .font(.headline)
                SizedDivider(height: 6)
                VStack(spacing: 0) {
                    HStack {
                        Text("Number of people")
                        Spacer()
                        Text("\(lur.numberOfPeople)")
                    }.font(.subheadline)
                    SizedDivider(height: 5)
                    Rectangle().frame(height: 1)
                    SizedDivider(height: 5)
                    HStack {
                        Text("Estimated time of entrance")
                        Spacer()
                        Text(lur.ete?.getTime() ?? "Now")
                    }.font(.subheadline)
                }.padding(.horizontal)
                SizedDivider(height: 6)
            }
            .lightBlueCard()
            VStack(alignment: .center, spacing: 0) {
                VStack(spacing: 0) {
                    SizedDivider(height: 5)
                    Text(timeToWaitStr)
                        .fontWeight(.medium)
                        .font(.largeTitle)
                    SizedDivider(height: 5)
                    HStack{Spacer()}
                }.padding().background(.lightBlueHeaderBG)
                SizedDivider(height: 8)
                Text("Time to wait")
                    .font(.subheadline)
                SizedDivider(height: 8)
            }
            .blueCard()
            
            VStack(alignment: .center, spacing: 5) {
                SizedDivider(height: 3)
                Text(lur.state != .ready ? "Please wait for this code to be announced before entering the store" : "Please show the following code to access the store")
                    .multilineTextAlignment(.center)
                    .fixedSize(horizontal: false, vertical: true)
                    .font(.subheadline)
                    .padding(.horizontal)
                Text(lur.visitToken.hfid)
                    .font(.largeTitle)
                    .fontWeight(.bold)
                Image(uiImage: UIImage(qrFrom: lur.visitToken.uuid.uuidString))
                    .interpolation(.none)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 250, height: 250)
                    .blur(radius: 9, opaque: lur.state != .ready)
                SizedDivider(height: 6)
                HStack{Spacer()}
            }
            .lightBlueCard()
            Button(action: {
                SI.controller.deleteLUR(lur: lur) { error in
                    guard error == nil else {print(error!); self.showAlert = true; return}
                    print("LUR deleted")
                    self.presentationMode.dismiss()
                    DispatchQueue.main.async { Repository.singleton.lurs[lur.visitToken.uuid.uuidString] = nil }
                }
            }){
                VStack (spacing: 0) {
                    SizedDivider(height: 15)
                    Text("Cancel request")
                        .fontWeight(.semibold)
                        .font(.body)
                    SizedDivider(height: 15)
                    HStack{Spacer()}
                }
                .tint(.blueLabel)
            }.customButtonStyle()
            .blueCard()
            Spacer()
        }.tint(.blueLabel).alert(isPresented: $showAlert) {defAlert}
    }
}

struct BRDetails: View {
    let br: BookingRequest
    @State var showAlert = false
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        VStack(alignment: .center, spacing: 10) {
            SizedDivider(height: 5)
            VStack (spacing: 0) {
                Text(br.store.name)
                    .fontWeight(.medium)
                    .font(.title)
                Text(br.store.address.description)
                    .fontWeight(.medium)
                    .font(.headline)
            }
            
            VStack(spacing: 0) {
                SizedDivider(height: 8)
                Text("Booking details")
                    .fontWeight(.bold)
                    .font(.headline)
                SizedDivider(height: 6)
                VStack(spacing: 5) {
                    HStack {
                        Text("Date of entrance")
                        Spacer()
                        Text(br.desiredTimeInterval.startingDateTime.getDate()+"\nat "+br.desiredTimeInterval.startingDateTime.getTime())
                    }.font(.subheadline)
                    Rectangle().frame(height: 1)
                    HStack {
                        Text("Estimated duration")
                        Spacer()
                        Text("\(br.desiredTimeInterval.duration) min")
                    }.font(.subheadline)
                    Rectangle().frame(height: 1)
                    HStack {
                        Text("Product Sections     ")
                        Spacer()
                        Text(br.listOfSections())
                            .multilineTextAlignment(.trailing)
                    }.font(.subheadline)
                    Rectangle().frame(height: 1)
                    HStack {
                        Text("Number of people")
                        Spacer()
                        Text("\(br.numberOfPeople)")
                    }.font(.subheadline)
                }.padding(.horizontal)
                SizedDivider(height: 8)
            }
            .lightBlueCard()
            
            VStack(alignment: .center, spacing: 5) {
                SizedDivider(height: 6)
                Text(br.state != .ready ? "Please wait for this code to be announced before entering the store" : "Please show the following code to access the store")
                    .multilineTextAlignment(.center)
                    .font(.subheadline)
                Text(br.visitToken.hfid)
                    .font(.largeTitle)
                    .fontWeight(.bold)
                Image(uiImage: UIImage(qrFrom: br.visitToken.uuid.uuidString))
                    .interpolation(.none)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 250, height: 250)
                    .blur(radius: 9, opaque: br.state != .ready)
                SizedDivider(height: 6)
                HStack{Spacer()}
            }
            .lightBlueCard()
            
            Button(action: {
                SI.controller.deleteBR(br: br) { error in
                    guard error == nil else {print(error!); self.showAlert = true; return}
                    print("BR deleted")
                    self.presentationMode.dismiss()
                    DispatchQueue.main.async { Repository.singleton.brs[br.visitToken.uuid.uuidString] = nil }
                }
            }){
                VStack (spacing: 0) {
                    SizedDivider(height: 15)
                    Text("Cancel request")
                        .fontWeight(.semibold)
                        .font(.body)
                    SizedDivider(height: 15)
                    HStack{Spacer()}
                }
                .tint(.blueLabel)
            }.customButtonStyle()
            .blueCard()
            Spacer()
        }.tint(.blueLabel)
    }
}
