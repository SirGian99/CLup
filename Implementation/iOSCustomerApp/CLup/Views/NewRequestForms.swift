//
//  NewRequestForms.swift
//  CLup
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele.
//  Copyright © 2021 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele. All rights reserved.
//


import SwiftUI

struct NewLURView: View {
    let store: Store
    @State var numberOfPeople = 1
    @State var showAlert = false
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        VStack(spacing: 0) {
            SizedDivider(height: 1)
            VStack {
                SizedDivider(height: 1)
                Stepper("Number of people:   \(numberOfPeople)", value: $numberOfPeople, in: 0...20).padding(.horizontal)
                SizedDivider(height: 1)
            }
            .lightBlueCard()
            Spacer()
            Button(action: {
                DB.controller.lineup(store: store, numberOfPeople: numberOfPeople) { (lur, error) in
                    guard error == nil else {print("Error while lining up"); self.showAlert = true; return}
                    self.presentationMode.dismiss()
                    DispatchQueue.main.async { Repository.singleton.lurs[lur!.visitToken.uuid.uuidString] = lur! }
                }
            }){
                Text("Confirm")
                    .fontWeight(.semibold)
                    .font(.body)
                    .tint(.blueLabel)
            }.customButtonStyle()
            SizedDivider(height: 20)
        }.alert(isPresented: $showAlert) {defAlert}
    }
}

struct NewBRView: View {
    let store: Store
    @State var numberOfPeople = 1
    @State var selectedDateTime = Date(timeIntervalSinceReferenceDate: 0)
    @State var duration: Duration = 5
    @State var choosableSects: [String:Bool] = [:]
    @State var showAlert = false
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        VStack(spacing: 0) {
            SizedDivider(height: 10)
            HStack {
                VStack(alignment: .leading) {
                    Text(store.name)
                        .fontWeight(.medium)
                        .font(.title)
                    Text(store.address.description)
                        .fontWeight(.medium)
                        .font(.headline)
                }.tint(.blueLabel)
                Spacer()
            }.padding(.horizontal)
            
            VStack {
                SizedDivider(height: 6)
                Text("Select date and time of the visit")
                DatePickerGUI(selectedDate: $selectedDateTime).background(.lightBlueHeaderBG)
                HStack{Spacer()}
            }
            .lightBlueCard()
            
            VStack {
                SizedDivider(height: 1)
                Stepper("Estimated duration:   \(duration) min", value: $duration, in: 5...200, step: 5).padding(.horizontal)
                SizedDivider(height: 1)
            }
            .lightBlueCard()
            
            VStack {
                SizedDivider(height: 1)
                Stepper("Number of people:   \(numberOfPeople)", value: $numberOfPeople, in: 0...20).padding(.horizontal)
                SizedDivider(height: 1)
            }
            .lightBlueCard()
            
            if (!store.sections.isEmpty) {
                VStack {
                    SizedDivider(height: 5)
                    ForEach(store.sections, id: \.id) { sect in
                        Button(action: {
                            if choosableSects[sect.id] != nil {
                                self.choosableSects[sect.id]!.toggle()
                            } else {
                                self.choosableSects[sect.id] = true
                            }
                        }){
                            ZStack(alignment: .topLeading) {
                                if choosableSects[sect.id] != nil && choosableSects[sect.id]! == true {Image(systemName: "checkmark").padding(2)}
                                VStack(spacing: 0) {
                                    SizedDivider(height: 9)
                                    HStack {
                                        SizedDivider(height: 2, width: 8)
                                        Text(sect.name)
                                        SizedDivider(height: 2, width: 8)
                                    }
                                    SizedDivider(height: 9)
                                }
                            }
                            .background(.white)
                            .cornerRadius(10)
                            .tint(.blueLabel)
                        }.customButtonStyle()
                    }
                    SizedDivider(height: 5)
                    HStack{Spacer()}
                }
                .lightBlueCard()
            }
            Spacer()
            Button(action: {
                let chosenSectionsIDs = self.choosableSects.compactMap{cs in cs.value == true ? cs.key : nil}
                var chosenSections: [Section] = []
                for csid in chosenSectionsIDs {
                    chosenSections.append(store.sections.first(where: {s in s.id == csid})!)
                }
                DB.controller.booking(store: store, sections: chosenSections, numberOfPeople: numberOfPeople, desiredTimeInterval: CTimeInterval(startingDateTime: selectedDateTime, duration: duration)) { (br, error) in
                    guard error == nil else {print("Error while making a booking request"); self.showAlert = true; return}
                    self.presentationMode.dismiss()
                    DispatchQueue.main.async { Repository.singleton.brs[br!.visitToken.uuid.uuidString] = br }
                }
            }){
                Text("Confirm")
                    .fontWeight(.semibold)
                    .font(.body)
                    .tint(.blueLabel)
            }.customButtonStyle()
        }.alert(isPresented: $showAlert) {defAlert}
    }
}
