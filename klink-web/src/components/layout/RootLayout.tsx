import { ParentComponent } from "solid-js";
import { KlinkCollectionStoreProvider } from "~/lib/klinks/context";


const RootLayout: ParentComponent = (props) => {
  return (
    <KlinkCollectionStoreProvider>
      <div class="overflow-none h-screen">
        {props.children}
      </div>
    </KlinkCollectionStoreProvider>
  );
}

export default RootLayout;
