import { createMemo, createResource } from "solid-js";
import useKlinkIdParam from "~/hooks/use-klinkid-params";
import { useKlinks } from "~/stores/klink-hooks";
import { KlinkService, KlinkService as service } from "~/stores/klink-store";

export default function collectionStore() {
  const pathKlinkId = useKlinkIdParam();
  const klinks = useKlinks();

  const [data, { refetch }] = createResource(KlinkService.syncKlinks);

  const copyKlink = (id: string) => {
    service.copyExistingKlink(id);
  };

  const createKlink = (data: {
    name: string;
    description?: string;
  }) => {
    service.createNewKlink(data);
  };

  return {
    klinks: createMemo(() => klinks().toReversed()),
    pathKlinkId,
    createKlink,
    copyKlink,
  };
}
