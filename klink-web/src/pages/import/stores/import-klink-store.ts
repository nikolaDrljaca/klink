import { createResource } from "solid-js";
import useKlinkImportParams from "~/hooks/use-klink-key-params";
import useKlinkIdParam from "~/hooks/use-klinkid-params";
import makeRelativeTime from "~/lib/relative-time";
import { KlinkService } from "~/stores/klink-store";
import { Klink } from "~/types/domain";

export default function importKlinkStore() {
  const klinkId = useKlinkIdParam();
  const encodedKey = useKlinkImportParams();

  const request = async () => {
    return KlinkService.importKlink(klinkId(), encodedKey);
  };
  const [data, { refetch }] = createResource(request);

  const relativeTime = makeRelativeTime();

  const handleImportKlink = () => {
    if (!data.latest) {
      return;
    }
    const klink: Klink = {
      id: data.latest.id,
      name: data.latest.name,
      description: data.latest.description,
      updatedAt: new Date(data.latest.updatedAt),
      readKey: data.latest.readKey,
      writeKey: data.latest.writeKey
    }
    KlinkService.createKlink(klink, data.latest.entries);
  };

  const updatedAt = () =>
    relativeTime.format(new Date((data().updatedAt)));

  return {
    data,
    updatedAt,
    importKlink: handleImportKlink,
    refetch,
  };
}
