import SwiftUI

struct AddBookingView: View {
    
    let store: Store
    
    struct SectionView: View {
        let section: Section
        @State var isChosen = false
        var body: some View {
            Button(action: {isChosen.toggle()}){
                ZStack(alignment: .topLeading) {
                    if isChosen {Image(systemName: "checkmark").padding(2)}
                    VStack(spacing: 0) {
                        SizedDivider(height: 9)
                        HStack {
                            SizedDivider(height: 2, width: 8)
                            Text(section.name)
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
    }
    
    @State var numberOfPeople = 1
    @State var selectedDateTime = Date(timeIntervalSinceReferenceDate: 0)
    @State var duration: Duration = 5


    
    private func addRequest(br: BookingRequest) {
//        DB.controller.addRequest(request: request) { id, error in
//            if error == nil, let id = id {
//                DispatchQueue.main.async {
//                    request.id = id
//                    request.waitingForServerResponse = false
//                    CD.controller.pendingRequests.removeFirst()
//                    CD.controller.safeSave()
//                    Calendar.controller.addRequest(request)
//                }
//           } else {
//                let alert = UIAlertController(title: "Oh no!", message: "It seems we had a problem adding your request.\nDo you want to try again?", preferredStyle: .alert)
//                alert.addAction(UIAlertAction(title: "Cancel", style: .destructive, handler: { _ in
//                    request.waitingForServerResponse = false
//                    CD.controller.pendingRequests.removeFirst()
//                }))
//                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { _ in
//                    addRequest(request: request)
//                }))
//                DispatchQueue.main.async { UIViewController.foremost.present(alert) }
//           }
//       }
    }

    var confirmButton: some View {
        Button(action: {
//            titleNeeded = titlePickerValue == -1
//            descriptionNeeded = true
//            selectedDateTime = selectedDateTime < Date()
//            if !(locationNeeded || titleNeeded || dateNeeded || descriptionNeeded) {
//                DispatchQueue.main.async { UIViewController.foremost.dismiss() }
//            }
        }) {
            Text("Confirm").orange().bold()
        }
    }
    
    var body: some View {
        UIViewController.foremost.presentationController?.delegate = PresentationDelegate.shared
        return VStack(spacing: 0) {
            SizedDivider(height: 1)
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
            
            VStack {
                SizedDivider(height: 5)
                ForEach(store.sections, id: \.id) { section in
                    SectionView(section: section)
                }
                SizedDivider(height: 5)
                HStack{Spacer()}
            }
            .lightBlueCard()
            
            Spacer()
            
            Button(action: {print("Confirmmm!!")}){
                Text("Confirm")
                    .fontWeight(.semibold)
                    .font(.body)
                    .tint(.blueLabel)
            }.customButtonStyle()
            
        }
    }
}
