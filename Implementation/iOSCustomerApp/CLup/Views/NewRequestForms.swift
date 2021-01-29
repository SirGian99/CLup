import SwiftUI

struct NewLURView: View {
    let store: Store
    @State var numberOfPeople = 1
    
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
            
            Button(action: {print("Confirmmm!!")}){
                Text("Confirm")
                    .fontWeight(.semibold)
                    .font(.body)
                    .tint(.blueLabel)
            }.customButtonStyle()
            
        }
    }
}

struct NewBRView: View {
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
