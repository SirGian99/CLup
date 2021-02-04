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
