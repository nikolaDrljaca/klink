import { Component, For } from "solid-js";
import { useDashboardActions, useDashboardStore } from "~/lib/dashboard/context";
import { Plus, Import, Share, Trash } from "lucide-solid"
import { Klink } from "~/lib/dashboard/store";
import clsx from "clsx";


const Dashboard: Component = () => {
  const state = useDashboardStore();
  const actions = useDashboardActions();

  const onCreateKlinkClick = () => {
    actions.createKlink({
      name: Math.random().toString(36).slice(2, 7)
    });
  }

  const onDeleteKlinkItemClick = (id: string) => {
    actions.deleteKlink(id);
  }

  const onSelectKlink = (id: string) => {
    actions.selectKlink(id);
  }

  return (<div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">

    <p class="text-2xl px-4 pt-4 pb-2"># Collections</p>

    {/* Button Row */}
    <div class="flex flex-row gap-x-4 px-4 pt-2 pb-4 items-center justify-center w-full">
      <button class="btn btn-neutral btn-sm w-1/2" onClick={onCreateKlinkClick}>
        <Plus size={20} />
        Create
      </button>
      <button class="btn btn-sm w-1/2">
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
            onDeleteClick={() => onDeleteKlinkItemClick(item.id)}
            onSelect={() => onSelectKlink(item.id)}
          />
        }
      </For>
    </div>

  </div>);
}

type KlinkListItemProps = {
  item: Klink,
  onDeleteClick: () => void,
  onSelect: () => void
}

const KlinkListItem: Component<KlinkListItemProps> = (props) => {
  const isSelected = () => true;
  const classes = clsx(
    'flex flex-col p-2 w-full justify-center border-b-2 border-zinc-900',
    isSelected() && 'bg-neutral'
  );

  return (
    <div class={classes}>
      <div class="flex flex-col w-full hover:cursor-pointer" onClick={props.onSelect}>
        <p class="text-xs font-light text-zinc-400 pl-4">Updated at 15:43</p>
        <p class="pl-4 text-lg">{props.item.name}</p>
      </div>
      <div class="flex flex-row items-center justify-around w-full pt-2 pl-4">
        <button class="btn btn-circle btn-ghost btn-sm" onClick={props.onSelect}>
          <Share size={14} />
        </button>
        <button class="btn btn-circle btn-sm btn-ghost" onClick={props.onDeleteClick}>
          <Trash size={14} />
        </button>
      </div>
    </div>
  );
}

export default Dashboard;
