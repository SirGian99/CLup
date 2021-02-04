import SwiftUI

struct FirstTab: View {
    @ObservedObject var repo = Repository.singleton
    @State var searchText = ""
    @State var showAlert = false
    var body: some View {
        NavigationView {
            VStack(alignment: .center, spacing: 0) {
                SizedDivider(height: 15)
                SearchBar(text: $searchText, onClick: {
                    DB.controller.getChainStore(city: self.searchText){ (chains, autstores, error) in
                        guard error == nil else {print(error!); self.showAlert = true; return}
                        DispatchQueue.main.async {
                            Repository.singleton.chains = chains!
                            Repository.singleton.stores = autstores!
                        }
                    }
                })
                if !Repository.singleton.chains.isEmpty || !Repository.singleton.stores.isEmpty {
                    SizedDivider(height: 10)
                    ScrollView(.vertical, showsIndicators: false) {
                        if !Repository.singleton.chains.isEmpty {
                            Text("Chains").font(.title2).bold()
                            ForEach(repo.chainsArray(), id: \.name) { chain in
                                ChainView(chain: chain, city: searchText).cornerRadius(10).padding()
                            }
                        }
                        if !Repository.singleton.stores.isEmpty {
                            Text("Local stores").font(.title2).bold()
                            ForEach(repo.storesArray(), id: \.name) { store in
                                StoreView(store: store).cornerRadius(10).padding()
                            }
                        }
                    }
                }
                Spacer()
            }.alert(isPresented: $showAlert){defAlert}.navigationBarHidden(true).navigationBarTitleDisplayMode(.inline).transparentNavBar()
        }
    }
}

struct SecondTab: View {
    @ObservedObject var repo = Repository.singleton
    @State var showLURModal = false
    @State var showBRModal = false
    var body: some View {
        VStack(spacing: 0) {
            SizedDivider(height: 10)
            ScrollView(.vertical, showsIndicators: false) {
                if let lur = repo.lur {
                    Button(action: {showLURModal.toggle()}){
                        RequestPreview(req: lur)
                            .sheet(isPresented: $showLURModal){LURDetails(lur: lur)}
                    }
                    SizedDivider(height: 5)
                }
                ForEach(repo.brs.keys.sorted(), id: \.self) { key in
                    SizedDivider(height: 10)
                    Button(action: {showBRModal.toggle()}){
                        RequestPreview(req: repo.brs[key]!)
                            .sheet(isPresented: $showBRModal){BRDetails(br: repo.brs[key]!)}
                    }
                }
            }
        }
    }
}
