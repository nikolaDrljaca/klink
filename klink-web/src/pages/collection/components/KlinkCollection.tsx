import { Component, For } from "solid-js";
import collectionStore from "~/pages/collection/stores/collection-store";
import CreateKlinkView from "~/pages/collection/components/CreateKlinkView";
import KlinkCollectionItem from "~/pages/collection/components/KlinkCollectionItem";

const KlinkListEmpty: Component = () => {
  return (
    <div class="flex flex-col w-full h-full items-center justify-center p-4 text-center">
      <p>Use the 'Create' button to make a new Klink.</p>
    </div>
  );
};

const KlinkCollection: Component = () => {
  const store = collectionStore();

  const onCopyKlink = (id: string) => {
    store.copyKlink(id);
  };

  return (
    <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">
      <div class="flex flex-row items-center justify-between px-4 pt-6 pb-2">
        <p class="font-inter text-2xl"># Collections</p>
      </div>

      <CreateKlinkView onSubmit={store.createKlink} />

      {/* Klink List - Container */}
      <div class="lg:container items-center w-full" id="klink-collection">
        {/* List Item */}
        <For each={store.klinks()} fallback={<KlinkListEmpty />}>
          {(item) => (
            <KlinkCollectionItem
              item={item.model()}
              pathKlinkId={store.pathKlinkId()}
              onCopyClick={() => onCopyKlink(item.model().id)}
            />
          )}
        </For>
      </div>
    </div>
  );
};

export default KlinkCollection;
