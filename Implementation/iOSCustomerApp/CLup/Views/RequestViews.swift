//
//  RequestViews.swift
//  CLup
//
//  Created by Vincenzo Riccio on 23/01/2021.
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
    
    var body: some View {
        let timeToWait = Date(timeIntervalSinceNow: 0).distance(to: lur.estimatedTimeOfEntrance)/60
        let timeToWaitStr = timeToWait >= 60 ? ">1hr" : "\(timeToWait) min"
        return VStack(alignment: .center, spacing: 25) {
            SizedDivider(height: 1)
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
                        Text("Time when lining-up")
                        Spacer()
                        Text(lur.creation.getTime() ?? "Not available")
                    }.font(.subheadline)
                    SizedDivider(height: 5)
                    Rectangle().frame(height: 1)
                    SizedDivider(height: 5)
                    HStack {
                        Text("Number of people")
                        Spacer()
                        Text("\(lur.numberOfPeople)")
                    }.font(.subheadline)
                }.padding(.horizontal)
                SizedDivider(height: 6)
            }
            .frame(width: screenWidth-screenWidth/10)
            .lightBlueCard()
            HStack(spacing: 0) {
                Spacer()
                VStack(alignment: .center, spacing: 0) {
                    ZStack {
                        Rectangle()
                            .foregroundColor(getColor(.lightBlueHeaderBG))
                        Text(lur.estimatedTimeOfEntrance.getTime()!)
                            .fontWeight(.medium)
                            .font(.largeTitle)
                            .tint(.blueLabel)
                    }
                    Text("Estimated time of entrance")
                        .font(.subheadline)
                        .padding(.vertical, 5)
                        .tint(.blueLabel)
                }
                .background(.blueHeaderBG)
                .cornerRadius(10)
                .frame(width: screenWidth/2+screenWidth/50, height: 86)
                Spacer()
                VStack(alignment: .center, spacing: 0) {
                    ZStack {
                        Rectangle()
                            .foregroundColor(getColor(.lightBlueHeaderBG))
                        Text(timeToWaitStr)
                            .fontWeight(.medium)
                            .font(.largeTitle)
                            .tint(.blueLabel)
                    }
                    Text("Time to wait")
                        .font(.subheadline)
                        .padding(.vertical, 5)
                        .tint(.blueLabel)
                }
                .background(.blueHeaderBG)
                .cornerRadius(10)
                .frame(height: 86)
            }.padding(.horizontal)
            VStack(alignment: .center, spacing: 5) {
                SizedDivider(height: 5)
                Text(lur.state != .ready ? "Please wait for this code to be announced before entering the store" : "Please show the following code to access the store")
                    .font(.subheadline)
                    .multilineTextAlignment(.center)
                    .tint(.blueLabel)
                Text(lur.visitToken.hfid)
                    .font(.largeTitle)
                    .fontWeight(.bold)
                Image(uiImage: UIImage(qrFrom: lur.visitToken.uuid.uuidString))
                    .interpolation(.none)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 250, height: 250)
                    //.blur(radius: 7, opaque: lur.state != .ready)
                SizedDivider(height: 5)
            }
            .frame(width: screenWidth-screenWidth/10)
            .background(.lightBlueHeaderBG)
            .cornerRadius(10)
            CancelButton()
                .frame(width: screenWidth-screenWidth/10, height: 50)
                .background(.blueHeaderBG)
                .cornerRadius(10)
            Spacer()
        }.tint(.blueLabel)
    }
}

struct BRDetails: View {
    let br: BookingRequest
    
    var body: some View {
        VStack(alignment: .center, spacing: 25) {
            SizedDivider(height: 1)
            VStack (spacing: 0) {
                Text(br.store.name)
                    .fontWeight(.medium)
                    .font(.title)
                Text(br.store.address.description)
                    .fontWeight(.medium)
                    .font(.headline)
            }
            
            VStack(spacing: 0) {
                SizedDivider(height: 6)
                Text("Booking details")
                    .fontWeight(.bold)
                    .font(.headline)
                SizedDivider(height: 6)
                VStack(spacing: 5) {
                    HStack {
                        Text("Time of entrance")
                        Spacer()
                        Text(br.desiredTimeInterval.startingDateTime.getTime() ?? "Not available")
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
                SizedDivider(height: 6)
            }
            .frame(width: screenWidth-screenWidth/10)
            .lightBlueCard()
            
            VStack(alignment: .center, spacing: 5) {
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
                    //.blur(radius: 7, opaque: lur.state != .ready)
            }
            .padding()
            .frame(width: screenWidth-screenWidth/10)
            .background(.lightBlueHeaderBG)
            .cornerRadius(10)
            
            CancelButton()
                .frame(width: screenWidth-screenWidth/10, height: 50)
                .background(.blueHeaderBG)
                .cornerRadius(10)
            Spacer()
        }.tint(.blueLabel)
    }
}
