import { createResource } from "solid-js";
import useKlinkImportParams from "~/hooks/use-klink-key-params";
import useKlinkIdParam from "~/hooks/use-klinkid-params";
import { makeEncoder } from "~/lib/make-encoder";
import { makeKeyEncoder } from "~/lib/make-key-encoder";
import makeKlinkApi from "~/lib/make-klink-api";
import makeRelativeTime from "~/lib/relative-time";
import { importKlink } from "~/stores/klink-store";
import { Klink } from "~/types/domain";

export default function importKlinkStore() {
  const keyEncoder = makeKeyEncoder(makeEncoder());

  const klinkId = useKlinkIdParam();
  const encodedKey = useKlinkImportParams();

  const api = makeKlinkApi();
  const request = async () => {
    if (!encodedKey) {
      return Promise.reject();
    }
    const curr = klinkId();
    const { readKey, writeKey } = keyEncoder.decode(encodedKey);
    if (!curr) {
      return Promise.reject();
    }
    return api.getKlink({
      klinkId: curr,
      readKey: readKey,
      writeKey: writeKey,
    });
  };
  const [data, { refetch }] = createResource(request);

  const relativeTime = makeRelativeTime();

  const handleImportKlink = () => {
    if (!data.latest) {
      return;
    }
    importKlink(data.latest as Klink);
  };

  const updatedAt = () =>
    relativeTime.format(relativeTime.unixFromResponse(data.latest.updatedAt));

  return {
    data,
    updatedAt,
    importKlink: handleImportKlink,
    refetch,
  };
}
