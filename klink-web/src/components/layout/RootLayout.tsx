import { ParentComponent } from "solid-js";
import { KlinkCollectionStoreProvider } from "~/stores/app-store-context";

const RootLayout: ParentComponent = (props) => {
  return (
    <KlinkCollectionStoreProvider>
      <div class="lg:container lg:mx-auto h-screen overflow-none">
        {props.children}
      </div>
    </KlinkCollectionStoreProvider>
  );
}

export default RootLayout;
