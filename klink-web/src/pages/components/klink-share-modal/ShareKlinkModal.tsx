import { GlobeLock } from "lucide-solid";
import { Component, Match, onCleanup, Show, Switch } from "solid-js";
import toast from "solid-toast";
import { writeClipboard } from "@solid-primitives/clipboard";
import shareKlinkStore from "~/pages/components/klink-share-modal/share-klink-store";
import SocialsRow from "./SocialsRow";

type ShareKlinkModalProps = {
  klinkId: string,
}

const ShareKlinkModal: Component<ShareKlinkModalProps> = (props) => {
  const klinkId = props.klinkId;
  const store = shareKlinkStore(klinkId);

  const unsub = store.listen(event => {
    switch (event.type) {
      case "success":
        toast.success("Klink shared!");
        break;
      case "failure":
        toast.error("Something went wrong!");
        break;
    }
  });

  const onCopy = () => {
    writeClipboard(store.klinkStore.shareLink)
      .then(() => toast("Copied URL to clipboard."))
  }

  onCleanup(() => unsub());

  return (
    <div class="flex flex-col space-y-2">
      <p class="text-lg">Share Controls - <b>{store.klinkStore.klink.name}</b></p>
      {/* Keys Row */}
      <Switch>
        {/* Shared Component */}
        <Match when={store.klinkStore.isShared}>
          <Show when={!store.klinkStore.isReadOnly}>
            <div class="flex flex-col space-x-1 pb-4">
              {/* Toggle */}
              <div class="form-control">
                <label class="label cursor-pointer">
                  <span class="">Share as Read Only</span>
                  <input
                    type="checkbox"
                    class="toggle"
                    checked={store.klinkStore.readOnlyChecked}
                    onChange={store.setReadOnlyChecked} />
                </label>
              </div>
              {/* Explainer */}
              <p class="text-zinc-400 text-sm">When enabled, the collection will be shared in read only mode. Only you will be able to make changes to it.</p>
            </div>
          </Show>

          <button class="btn btn-primary w-full" onClick={onCopy}>Copy to Clipboard</button>

          <div class="pt-2">
            <div class="divider">Socials</div>
          </div>

          {/* Socials Row */}
          <div class="py-2">
            <SocialsRow shareTarget={store.klinkStore.socialShareTarget} />
          </div>
        </Match>

        {/* Local Only */}
        <Match when={!store.klinkStore.isShared}>
          <div class="pt-4"></div>
          <div class="flex flex-col space-y-2 items-center text-center pb-4">
            <GlobeLock size={48} />
            <p class="text-lg">This klink is <b>local only</b>.</p>
            <p class="">To be able to share it with others, <b>upload</b> the collection to the cloud first.</p>
          </div>
          <button class="btn btn-primary btn-sm" onClick={store.shareKlink}>
            <Show when={store.klinkStore.loading}>
              <span class="loading loading-spinner"></span>
            </Show>
            Upload
          </button>
        </Match>
      </Switch>
    </div>
  );
}

export default ShareKlinkModal;
