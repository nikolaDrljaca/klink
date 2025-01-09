import { writeClipboard } from "@solid-primitives/clipboard";
import { Copy } from "lucide-solid";
import { Component } from "solid-js";
import toast from "solid-toast";

const KlinkKeyField: Component<{ key: string, title: string }> = (props) => {

  const onCopy = async () => {
    if (props.key) {
      await writeClipboard(props.key);
      toast("Key Copied!");
    }
  }

  return (
    <div class="flex flex-col space-y-2 w-full">
      <p class="text-sm text-base-content">{props.title}</p>
      <div class="join">
        <input
          type="text"
          value={props.key ?? ""}
          disabled={true}
          class="input input-bordered w-full join-item" />
        <button class="btn join-item" onClick={onCopy}>
          <Copy size={14} />
        </button>
      </div>
    </div>
  );
}

export default KlinkKeyField;
