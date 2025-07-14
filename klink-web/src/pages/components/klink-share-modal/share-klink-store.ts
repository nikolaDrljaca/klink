import { makeKeyEncoder } from "~/lib/make-key-encoder";
import { makeEncoder } from "~/lib/make-encoder";
import { KlinkService as service } from "~/stores/klink-store";
import makeAsync from "~/lib/make-async";
import { createSignal } from "solid-js";
import { useKlink } from "~/stores/klink-hooks";

export default function shareKlinkStore(klinkId: string) {
  const appBasePath = import.meta.env.VITE_APP_BASE;
  const klink = useKlink(klinkId);

  const keyEncoder = makeKeyEncoder(makeEncoder());

  const [readOnlyChecked, setReadOnlyChecked] = createSignal(false);
  const [loading, setLoading] = createSignal(false);

  const shareLink = () => {
    if (readOnlyChecked()) {
      return createReadOnlyLink();
    }
    return createShareLink();
  };

  const socialTarget = () => {
    return {
      title: klink().name,
      description: klink().description ?? "",
      url: shareLink(),
    };
  };

  const createShareLink = () => {
    const encoded = keyEncoder.encode(klink());
    return `${appBasePath}/c/${klink().id}/i?q=${encoded}`;
  };

  const createReadOnlyLink = () => {
    const encoded = keyEncoder.encode({ readKey: klink().readKey });
    return `${appBasePath}/c/${klink().id}/i?q=${encoded}`;
  };

  return {
    klink: klink,
    loading: loading,
    readOnlyChecked: readOnlyChecked,
    isShared: () => klink().isShared,
    isReadOnly: () => klink().isReadOnly,
    shareLink: () => shareLink(),
    socialShareTarget: () => socialTarget(),

    setReadOnlyChecked() {
      setReadOnlyChecked((curr) => !curr);
    },

    async shareKlink() {
      if (loading()) {
        return;
      }
      setLoading(true);
      const [err, response] = await makeAsync(() =>
        service.shareKlink(klink().id)
      );
      if (err) {
        setLoading(false);
        return err;
      }
      setLoading(false);
      return;
    },
  };
}
