import SwiftUI

struct TabViewController: View {
    let tab1 = FirstTab()
    let tab2 = SecondTab()
    @State var selectedIndex = 0
    var body: some View {
        VStack(spacing: 0) {
            if selectedIndex == 0 {
                tab1.onAppear() {
                    DB.controller.getChainStore(city: "Milano"){ (chains, autstores, error) in
                        guard error == nil else {return print(error!)}
                        DispatchQueue.main.async {
                            Repository.singleton.chains = chains!
                            Repository.singleton.stores = autstores!
                        }
                    }
                }
            } else if selectedIndex == 1 {
                tab2
            }
            Spacer()
            VStack(spacing: 0) {
                SizedDivider(height: 4, width: UIScreen.main.bounds.width)
                HStack {
                    HStack {
                        Spacer()
                        VStack(spacing: 2) {
                            Image(systemName: "bag").resizable().scaledToFit()
                            Text("Stores").font(.caption)
                        }.tintIf(selectedIndex == 0, .blueLabel, .gray)
                        .onTapGesture {self.selectedIndex = 0}
                        Spacer()
                    }
                    HStack {
                        Spacer()
                        VStack(spacing: 2) {
                            Image(systemName: "list.bullet").resizable().scaledToFit()
                            Text("Your requests").font(.caption)
                        }.tintIf(selectedIndex == 1, .blueLabel, .gray)
                        .onTapGesture {self.selectedIndex = 1}
                        Spacer()
                    }
                }
                SizedDivider(height: 22, width: UIScreen.main.bounds.width)
            }.frame(width: UIScreen.main.bounds.width, height: 75)
        }.edgesIgnoringSafeArea(.bottom)
    }
}
