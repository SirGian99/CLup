//
//  UIElements.swift
//  CLup
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele.
//  Copyright © 2021 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele. All rights reserved.
//


import SwiftUI


struct DatePickerGUI: View {
    @Binding var selectedDate: Date
    
    var body: some View {
        let dateBinding: Binding<Date> = Binding(
            get: {self.selectedDate},
            set: { newDate in
                self.selectedDate = newDate
            }
        )
        return
            DatePicker("", selection: dateBinding, in: Date()..., displayedComponents: [.date, .hourAndMinute])
                .datePickerStyle(WheelDatePickerStyle())
                .labelsHidden()
    }
}


struct SizedDivider: View {
    let width: CGFloat
    let height: CGFloat
    
    init(height: CGFloat, width: CGFloat = 1) {
        self.height = height
        self.width = width
    }
    var body: some View {
        Rectangle().frame(width: width, height: height).hidden()
    }
}

let defAlert = Alert(title: Text("Oh no!"), message: Text("Something went wrong"), dismissButton: .default(Text("Got it!")))

struct SearchBar: UIViewRepresentable {
    @Binding var text : String
    let onClick: () -> Void

    class Coordinator : NSObject, UISearchBarDelegate {
        @Binding var text: String
        let onClick: () -> Void
        init(_ text: Binding<String>, onClick: @escaping () -> Void) {
            _text = text
            self.onClick = onClick
        }
        func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
            text = searchText
        }
        func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
            searchBar.endEditing(true)
            onClick()
        }
    }

    func makeCoordinator() -> Coordinator { return Coordinator($text, onClick: onClick) }

    func makeUIView(context: UIViewRepresentableContext<SearchBar>) -> UISearchBar {
        let searchBar = UISearchBar(frame: .zero)
        searchBar.delegate = context.coordinator
        searchBar.searchBarStyle = .minimal
        searchBar.showsCancelButton = false
        searchBar.placeholder = "Type the city you are interested in"
        return searchBar
    }

    func updateUIView(_ uiView: UISearchBar, context: UIViewRepresentableContext<SearchBar>) {
        uiView.text = text
    }
}

struct LineUpButton: View {
    let store: Store
    @State var showModal = false
    var body: some View {
        Button(action: {self.showModal.toggle()}){
            VStack {
                SizedDivider(height: 5)
                Text("Line-up now")
                    .fontWeight(.semibold)
                    .font(.body)
                Text("Queue disposal time: \(store.estimatedQueueDisposalTime) min")
                    .font(.subheadline)
                SizedDivider(height: 5)
            }
            .tint(.blueLabel)
            .sheet(isPresented: $showModal) {NewLURView(store: store)}
        }.customButtonStyle()
    }
}

struct BookingButton: View {
    let store: Store
    @State var showModal = false
    var body: some View {
        Button(action: {self.showModal.toggle()}){
            VStack {
                SizedDivider(height: 5)
                Text("Book a visit")
                    .fontWeight(.semibold)
                    .font(.body)
                SizedDivider(height: 5)
            }
            .tint(.blueLabel)
            .sheet(isPresented: $showModal) {NewBRView(store: store)}
        }
        .customButtonStyle()
    }
}
