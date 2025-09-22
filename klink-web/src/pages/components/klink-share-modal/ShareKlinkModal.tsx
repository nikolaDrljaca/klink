import { GlobeLock } from "lucide-solid";
import { Component, Match, Show, Switch } from "solid-js";
import toast from "solid-toast";
import { writeClipboard } from "@solid-primitives/clipboard";
import shareKlinkStore from "~/pages/components/klink-share-modal/share-klink-store";
import SocialsRow from "./SocialsRow";

type ShareKlinkModalProps = {
  klinkId: string;
};

const ShareKlinkModal: Component<ShareKlinkModalProps> = (props) => {
  const klinkId = props.klinkId;
  const store = shareKlinkStore(klinkId);

  const onCopy = () => {
    writeClipboard(store.shareLink())
      .then(() => toast("Copied URL to clipboard."));
  };

  const handleShare = async () => {
    const err = await store.shareKlink();
    if (err) {
      toast.error("Something went wrong");
      return;
    }
    toast.success("Klink shared!");
  };

  return (
    <div class="flex flex-col space-y-2">
      <p class="text-lg">
        Share Controls - <b>{store.klink().name}</b>
      </p>
      {/* Keys Row */}
      <Switch>
        {/* Shared Component */}
        <Match when={store.isShared()}>
          <Show when={!store.isReadOnly()}>
            <div class="flex flex-col space-x-1 pb-4">
              {/* Toggle */}
              <div class="form-control">
                <label class="label cursor-pointer">
                  <span class="">Share as Read Only</span>
                  <input
                    type="checkbox"
                    class="toggle"
                    checked={store.readOnlyChecked()}
                    onChange={store.setReadOnlyChecked}
                  />
                </label>
              </div>
              {/* Explainer */}
              <p class="text-zinc-400 text-sm">
                When enabled, the collection will be shared in read only mode.
                Only you will be able to make changes to it.
              </p>
            </div>
          </Show>

          <button class="btn btn-primary w-full" onClick={onCopy}>
            <Show when={store.isShareLoading()}>
              <span class="loading loading-spinner"></span>
            </Show>
            Copy to Clipboard
          </button>

          <div class="pt-2">
            <div class="divider">Socials</div>
          </div>

          {/* Socials Row */}
          <div class="py-2">
            <SocialsRow shareTarget={store.socialShareTarget()} />
          </div>
        </Match>

        {/* Local Only */}
        <Match when={!store.isShared()}>
          <div class="pt-4"></div>
          <div class="flex flex-col space-y-2 items-center text-center pb-4">
            <GlobeLock size={48} />
            <p class="text-lg">
              This klink is <b>local only</b>.
            </p>
            <p class="">
              To be able to share it with others, <b>upload</b>{" "}
              the collection to the cloud first.
            </p>
          </div>
          <button class="btn btn-primary btn-sm" onClick={handleShare}>
            <Show when={store.loading()}>
              <span class="loading loading-spinner"></span>
            </Show>
            Upload
          </button>
        </Match>

      </Switch>
    </div>
  );
};

export default ShareKlinkModal;
