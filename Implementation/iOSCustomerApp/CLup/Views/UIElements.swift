import SwiftUI

let defaultButtonDimensions = (width: CGFloat(155.52), height: CGFloat(48))

let customDateFormat: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateStyle = .medium
    formatter.timeStyle = .short
    return formatter
}()

func defaultAlert(title: String, message: String) -> UIAlertController {
    let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
    alert.addAction(UIAlertAction(title: "Got it!", style: .default))
    return alert
}

struct ElementPickerGUI: View {
    var pickerElements: [String]
    @Binding var selectedValue: Int
    
    var body: some View {
        Picker("Select your need", selection: self.$selectedValue) {
            ForEach(0 ..< self.pickerElements.count) {
                Text(self.pickerElements[$0])
                    .font(.headline)
                    .fontWeight(.medium)
            }
        }
        .labelsHidden()
        .frame(width: UIScreen.main.bounds.width, height: 250)
        .background(Color.primary.colorInvert())
    }
}

struct DatePickerGUI: View {
    @Binding var selectedDate: Date
    
    var body: some View {
        let dateBinding: Binding<Date> = Binding(
            get: {self.selectedDate},
            set: { newDate in
                self.selectedDate = newDate
            }
        )
        return //VStack (spacing: 0){
            DatePicker("", selection: dateBinding, in: Date()..., displayedComponents: [.date, .hourAndMinute])
                .datePickerStyle(WheelDatePickerStyle())
                .labelsHidden()
                //.frame(width: UIScreen.main.bounds.width)
            //Spacer()
        //}//.frame(width: UIScreen.main.bounds.width, height: 270)
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

struct AlertView: View {
    @Binding var isPresented: Bool
    
    var body: some View {
        VStack {
            EmptyView()
        }.alert(isPresented: $isPresented) {
            Alert(title: Text("Oh no!"), message: Text("Something went wrong"), dismissButton: .default(Text("Got it!")))
        }
    }
}

struct SearchBar: UIViewRepresentable {
    @Binding var text : String
    let onDelete: () -> Void

    class Coordinator : NSObject, UISearchBarDelegate {
        @Binding var text: String
        let onDelete: () -> Void
        init(_ text: Binding<String>, onDelete: @escaping () -> Void) {
            _text = text
            self.onDelete = onDelete
        }
        func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
            text = searchText
        }
        func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
            text = ""
            searchBar.endEditing(true)
            onDelete()
        }
    }

    func makeCoordinator() -> Coordinator { return Coordinator($text, onDelete: onDelete) }

    func makeUIView(context: UIViewRepresentableContext<SearchBar>) -> UISearchBar {
        let searchBar = UISearchBar(frame: .zero)
        searchBar.delegate = context.coordinator
        searchBar.searchBarStyle = .minimal
        searchBar.showsCancelButton = true
        return searchBar
    }

    func updateUIView(_ uiView: UISearchBar, context: UIViewRepresentableContext<SearchBar>) {
        uiView.text = text
    }
}
