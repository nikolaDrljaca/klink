import { Component, For } from "solid-js";
import { Plus, Import, Share, Trash, Copy, Edit } from "lucide-solid"
import clsx from "clsx";
import CreateKlinkModal from "~/components/CreateKlinkModal";
import DeleteKlinkModal from "~/components/DeleteKlinkModal";
import ShareKlinkModal from "~/components/ShareKlinkModal";
import { useKlinkCollectionActions, useKlinkCollectionStore } from "~/lib/klinks/context";
import { Klink } from "~/lib/klinks/store";
import { useNavigate, useParams } from "@solidjs/router";
import createModal from "~/components/modal/Modal";
import toast from "solid-toast";


const KlinkCollection: Component = () => {
  const state = useKlinkCollectionStore();
  const actions = useKlinkCollectionActions();

  const navigate = useNavigate();
  const params = useParams();
  const pathKlinkId = () => params.klinkId;

  const onDeleteKlinkItemClick = (id: string) => {
    if (id === pathKlinkId()) {
      navigate("/c");
    }
    actions.deleteKlink(id);
  }

  const onSelectKlink = (id: string) => {
    navigate(`/c/${id}`);
    actions.selectKlink(id);
  }

  const onCopyKlink = (id: string) => {
    actions.copyKlink(id);
  }

  const onImportClick = () => {
    // TODO: Import not implemented.
    toast("Not Implemented.");
  }

  return (
    <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">

      <p class="text-2xl px-4 pt-4 pb-2"># Collections</p>

      {/* Button Row */}
      <div class="flex flex-row gap-x-4 px-4 pt-2 pb-4 items-center justify-center w-full">
        {/* Create Modal */}
        <CreateKlinkModal onSubmit={actions.createKlink}>
          {(open) =>
            <button class="btn btn-neutral btn-sm w-1/2" onClick={open}>
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
      <div class="container items-center w-full">
        {/* List Item */}
        <For each={state.klinks}>
          {(item,) =>
            <KlinkListItem
              item={item}
              pathKlinkId={pathKlinkId()}
              onDeleteClick={() => onDeleteKlinkItemClick(item.id)}
              onSelect={() => onSelectKlink(item.id)}
              onCopyClick={() => onCopyKlink(item.id)}
            />
          }
        </For>
      </div>
    </div>
  );
}

type KlinkListItemProps = {
  item: Klink,
  pathKlinkId?: string,
  onDeleteClick: () => void,
  onCopyClick: () => void,
  onSelect: () => void
}

const KlinkListItem: Component<KlinkListItemProps> = (props) => {
  const isSelected = () => props.pathKlinkId === props.item.id;
  const classes = () => clsx(
    'flex flex-col p-2 w-full justify-center border-b-2 border-zinc-900',
    isSelected() && 'bg-neutral'
  );

  const deleteModal = createModal();
  const shareModal = createModal();

  const onEdit = () => {
    //TODO: 
    toast("Not implemented.");
  }

  return (
    <div class={classes()}>
      <div class="flex flex-col w-full hover:cursor-pointer" onClick={props.onSelect}>
        <p class="text-xs font-light text-zinc-400 pl-4">Updated at 15:43</p>
        <p class="pl-4 text-lg">{props.item.name}</p>
      </div>
      <div class="flex flex-row items-center justify-around w-full pt-4 pl-4">
        <button class="btn btn-circle btn-ghost btn-sm" onClick={onEdit}>
          <Edit size={14} />
        </button>
        <button class="btn btn-circle btn-ghost btn-sm" onClick={props.onCopyClick}>
          <Copy size={14} />
        </button>
        <button class="btn btn-circle btn-ghost btn-sm" onClick={shareModal.controller.open}>
          <Share size={14} />
        </button>
        <button class="btn btn-circle btn-sm btn-ghost" onClick={() => deleteModal.controller.open()}>
          <Trash size={14} />
        </button>

        <deleteModal.Modal>
          <DeleteKlinkModal
            klink={props.item}
            onClose={deleteModal.controller.close}
            onSubmit={props.onDeleteClick} />
        </deleteModal.Modal>
        <shareModal.Modal>
          <ShareKlinkModal item={props.item} />
        </shareModal.Modal>
      </div>
    </div>
  );
}

export default KlinkCollection;
