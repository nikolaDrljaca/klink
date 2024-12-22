import clsx from "clsx";
import { Component, Show } from "solid-js";
import { Klink } from "~/lib/klinks/store";
import createModal from "~/components/modal/Modal";
import { Copy, Edit, Share, Trash } from "lucide-solid";
import DeleteKlinkModal from "~/components/DeleteKlinkModal";
import ShareKlinkModal from "~/components/ShareKlinkModal";
import EditKlinkModal from "./EditKlinkModal";

type KlinkListItemProps = {
  item: Klink,
  pathKlinkId?: string,
  onCopyClick: () => void,
  onSelect: () => void
}

const KlinkListItem: Component<KlinkListItemProps> = (props) => {
  const isSelected = () => props.pathKlinkId === props.item.id;
  const classes = () => clsx(
    'flex flex-col p-2 w-full justify-center border-b-2 border-base-300',
    isSelected() && 'bg-base-300'
  );

  const deleteModal = createModal();
  const shareModal = createModal();
  const editModal = createModal();

  return (
    <div class={classes()}>
      {/* Top Anchor */}
      <a class="flex flex-col w-full px-4 text-base-content hover:cursor-pointer" onClick={props.onSelect}>
        <p class="text-xs font-light">Updated at 15:43</p>
        <p class="text-lg pt-1">{props.item.name}</p>
        <Show when={props.item.description}>
          <span class="text-sm py-2">{props.item.description}</span>
        </Show>
      </a>
      {/* Button Row */}
      <div class="flex flex-row items-center justify-around w-full pt-4 pl-4">
        <button class="btn btn-circle btn-ghost btn-sm" onClick={editModal.controller.open}>
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
            klinkId={props.item.id}
            onClose={deleteModal.controller.close} />
        </deleteModal.Modal>
        <shareModal.Modal>
          <ShareKlinkModal klinkId={props.item.id} />
        </shareModal.Modal>
        <editModal.Modal>
          <EditKlinkModal
            klinkId={props.item.id}
            onClose={editModal.controller.close}
          />
        </editModal.Modal>

      </div>
    </div>
  );
}

export default KlinkListItem;
