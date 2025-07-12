import { createEventBus } from "@solid-primitives/event-bus";
import { makeKeyEncoder } from "~/lib/make-key-encoder";
import { makeEncoder } from "~/lib/make-encoder";
import { shareKlink, useKlink } from "~/stores/klink-store";
import makeAsync from "~/lib/make-async";
import { createSignal } from "solid-js";

type ShareKlinkEvent =
  | { type: "success" }
  | { type: "failure" };

export default function shareKlinkStore(klinkId: string) {
  const appBasePath = import.meta.env.VITE_APP_BASE;
  const klink = useKlink(klinkId);

  const { listen, emit, clear } = createEventBus<ShareKlinkEvent>();
  const keyEncoder = makeKeyEncoder(makeEncoder());

  const [readOnlyChecked, setReadOnlyChecked] = createSignal(false);
  const [loading, setLoading] = createSignal(false);

  const klinkStore = () => ({
    klink: klink(),
    loading: loading(),
    readOnlyChecked: readOnlyChecked(),
    get isShared() {
      return !!klink().readKey;
    },
    get isReadOnly() {
      if (!!klink().writeKey) {
        return false;
      }
      return !!klink().readKey;
    },
    get shareLink() {
      return shareLink();
    },
    get socialShareTarget() {
      return socialTarget();
    },
  });

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
      url: klinkStore().shareLink,
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
    klinkStore,
    listen,

    setReadOnlyChecked() {
      setReadOnlyChecked((curr) => !curr);
    },

    async shareKlink() {
      if (loading()) {
        return;
      }
      setLoading(true);
      const [err, response] = await makeAsync(shareKlink(klink().id));
      if (err) {
        emit({ type: "failure" });
        setLoading(false);
        return;
      }
      setLoading(false);
      emit({ type: "success" });
    },
  };
}
