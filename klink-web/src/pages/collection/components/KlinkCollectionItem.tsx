import clsx from "clsx";
import { Component, Show } from "solid-js";
import createModal from "~/components/modal/Modal";
import { Copy, Edit, Share2, Trash } from "lucide-solid";
import DeleteKlinkModal from "~/pages/components/klink-delete-modal/DeleteKlinkModal";
import ShareKlinkModal from "~/pages/components/klink-share-modal/ShareKlinkModal";
import EditKlinkModal from "~/pages/components/klink-edit-modal/EditKlinkModal";
import { Klink } from "~/types/domain";
import makeRelativeTime from "~/lib/relative-time";

type KlinkListItemProps = {
  item: Klink,
  pathKlinkId?: string,
  onCopyClick: () => void,
  onSelect: () => void
}

const KlinkCollectionItem: Component<KlinkListItemProps> = (props) => {
  const isSelected = () => props.pathKlinkId === props.item.id;
  const relativeTime = makeRelativeTime();

  const date = () => relativeTime.format(props.item.updatedAt);

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
        <p class="text-xs font-light">Updated {date()}</p>
        <p class="text-lg pt-1">{props.item.name}</p>
        <Show when={props.item.description}>
          <span class="text-sm py-2">{props.item.description}</span>
        </Show>
      </a>
      {/* Button Row */}
      <div class="flex flex-row items-center justify-around w-full pt-4 pl-4">
        <div class="tooltip tooltip-bottom hover:cursor-pointer" data-tip="Edit">
          <button class="btn btn-circle btn-ghost btn-sm" onClick={editModal.controller.open}>
            <Edit size={14} />
          </button>
        </div>
        <div class="tooltip tooltip-bottom hover:cursor-pointer" data-tip="Create a Copy">
          <button class="btn btn-circle btn-ghost btn-sm" onClick={props.onCopyClick}>
            <Copy size={14} />
          </button>
        </div>
        <div class="tooltip tooltip-bottom hover:cursor-pointer" data-tip="Share">
          <button class="btn btn-circle btn-ghost btn-sm" onClick={shareModal.controller.open}>
            <Share2 size={14} />
          </button>
        </div>
        <div class="tooltip tooltip-bottom hover:cursor-pointer" data-tip="Delete">
          <button class="btn btn-circle btn-sm btn-ghost" onClick={() => deleteModal.controller.open()}>
            <Trash size={14} />
          </button>
        </div>

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

export default KlinkCollectionItem;
