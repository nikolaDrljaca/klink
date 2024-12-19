import clsx from "clsx";
import { Component } from "solid-js";
import { Klink } from "~/lib/klinks/store";
import createModal from "~/components/modal/Modal";
import toast from "solid-toast";
import { Copy, Edit, Share, Trash } from "lucide-solid";
import DeleteKlinkModal from "~/components/DeleteKlinkModal";
import ShareKlinkModal from "~/components/ShareKlinkModal";

type KlinkListItemProps = {
  item: Klink,
  pathKlinkId?: string,
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
      {/* Top Anchor */}
      <a class="flex flex-col w-full px-4 hover:cursor-pointer" onClick={props.onSelect}>
        <p class="text-xs font-light text-zinc-400">Updated at 15:43</p>
        <p class="text-lg">{props.item.name}</p>
        <span class="text-sm py-2">{props.item.description}</span>
      </a>
      {/* Button Row */}
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
            klinkId={props.item.id}
            onClose={deleteModal.controller.close} />
        </deleteModal.Modal>
        <shareModal.Modal>
          <ShareKlinkModal klinkId={props.item.id} />
        </shareModal.Modal>
      </div>
    </div>
  );
}

export default KlinkListItem;
