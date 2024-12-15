import { Component, For, Show } from "solid-js";
import { Plus, Import } from "lucide-solid"
import clsx from "clsx";
import CreateKlinkModal from "~/components/CreateKlinkModal";
import { useNavigate } from "@solidjs/router";
import toast from "solid-toast";
import KlinkListItem from "~/components/KlinkListItem";
import collectionStore from "~/lib/collectionStore";

const KlinkCollection: Component = () => {
  const store = collectionStore();

  const klinkNotEmpty = () => store.state.klinks.length > 0;

  const navigate = useNavigate();

  const onDeleteKlinkItemClick = (id: string) => {
    if (id === store.pathKlinkId()) {
      navigate("/c");
    }
    store.deleteKlink(id);
  }

  const onSelectKlink = (id: string) => {
    navigate(`/c/${id}`);
    store.selectKlink(id);
  }

  const onCopyKlink = (id: string) => {
    store.copyKlink(id);
  }

  const onImportClick = () => {
    // TODO: Import not implemented.
    toast("Not Implemented.");
  }

  const createButtonClass = () => clsx(
    'btn btn-sm w-1/2',
    !klinkNotEmpty() && 'btn-primary animate-pulse',
    klinkNotEmpty() && 'btn-neutral'
  );

  return (
    <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">

      <p class="text-2xl px-4 pt-6 pb-2"># Collections</p>

      {/* Button Row */}
      <div class="flex flex-row gap-x-4 px-4 pt-2 pb-4 items-center justify-center w-full">
        {/* Create Modal */}
        <CreateKlinkModal onSubmit={store.createKlink}>
          {(open) =>
            <button class={createButtonClass()} onClick={open}>
              <Plus size={20} />
              Create
            </button>
          }
        </CreateKlinkModal>
        <button class="btn btn-sm w-1/2" onClick={onImportClick}>
          <Import size={20} />
          Import
        </button>
      </div>

      {/* Klink List - Container */}
      <Show when={klinkNotEmpty()} fallback={<KlinkListEmpty />}>
        <div class="lg:container items-center w-full">
          {/* List Item */}
          <For each={store.state.klinks}>
            {(item,) =>
              <KlinkListItem
                item={item}
                pathKlinkId={store.pathKlinkId()}
                onDeleteClick={() => onDeleteKlinkItemClick(item.id)}
                onSelect={() => onSelectKlink(item.id)}
                onCopyClick={() => onCopyKlink(item.id)}
              />
            }
          </For>
        </div>
      </Show>
    </div>
  );
}

const KlinkListEmpty: Component = () => {
  return (
    <div class="flex flex-col w-full h-full items-center justify-center p-4 text-center">
      <p>Use the 'Create' button to make a new Klink.</p>
    </div>
  );
}

export default KlinkCollection;
