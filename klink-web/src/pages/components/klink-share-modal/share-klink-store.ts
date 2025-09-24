import { makeKeyEncoder } from "~/lib/make-key-encoder";
import { makeEncoder } from "~/lib/make-encoder";
import { KlinkService as service } from "~/stores/klink-store";
import makeAsync from "~/lib/make-async";
import { createResource, createSignal } from "solid-js";
import { useKlink } from "~/stores/klink-hooks";
import makeKlinkApi from "~/lib/make-klink-api";
import { GetKlinkShortUrlRequest } from "~/generated";
import { klinkModel } from "~/types/domain";

export default function shareKlinkStore(klinkId: string) {
  const appBasePath = import.meta.env.VITE_APP_BASE;
  const klink = useKlink(klinkId);
  const model = () => klinkModel(klink());

  const api = makeKlinkApi()

  const keyEncoder = makeKeyEncoder(makeEncoder());

  const [readOnlyChecked, setReadOnlyChecked] = createSignal(false);
  const [loading, setLoading] = createSignal(false);

  const requestParams = (): GetKlinkShortUrlRequest | null => {
    if (!model().isShared) {
      return null;
    }
    const writeKey = readOnlyChecked() ? null : klink().writeKey;
    return {
      klinkId: klinkId,
      readKey: klink().readKey,
      writeKey: writeKey
    }
  }
  const fetcher = async (value: GetKlinkShortUrlRequest) => {
    return api.getKlinkShortUrl(value);
  }
  const [shareUrl] = createResource(
    requestParams,
    fetcher
  );

  // NOTE: is exposed to the component - holds either short url from server or local link
  // NOTE: local link is here just as a fallback
  const shareLink = () => {
    if (shareUrl.error || !shareUrl()) {
      if (readOnlyChecked()) {
        return createReadOnlyLink();
      }
      return createShareLink();
    }
    return shareUrl().url;
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
    isShareLoading: () => shareUrl.loading,

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
