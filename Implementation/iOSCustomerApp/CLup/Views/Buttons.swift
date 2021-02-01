import SwiftUI

struct CloseButton: View {
    @Environment(\.presentationMode) var presentationMode
    var body: some View {
        Button(action: {
            withAnimation {
                self.presentationMode.wrappedValue.dismiss()
            }
        }){
            Image(systemName: "xmark.circle.fill").font(.largeTitle).tint(.white).opacity(0.9)
        }.customButtonStyle()
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
                Text("Estimated waiting: \(store.estimatedQueueDisposalTime) min")
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

struct CancelButton: View {
    var body: some View {
        Button(action: {print("CANCELLLL!!")}){
            VStack {
                SizedDivider(height: 5)
                Text("Cancel request")
                    .fontWeight(.semibold)
                    .font(.body)
                SizedDivider(height: 5)
            }
            .tint(.blueLabel)
        }.customButtonStyle()
    }
}
